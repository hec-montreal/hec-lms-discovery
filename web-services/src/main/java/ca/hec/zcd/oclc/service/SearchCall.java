package ca.hec.zcd.oclc.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.hec.data.json.JsonMap;
import ca.hec.util.JsonException;
import ca.hec.util.JsonUtil;
import ca.hec.util.StringUtil;
import ca.hec.web.service.consumer.ApiCall;
import ca.hec.web.service.consumer.ApiCallException;
import ca.hec.web.service.consumer.ApiResponse;
import ca.hec.zcd.model.oclc.AccessToken;
import ca.hec.zcd.model.oclc.Author;
import ca.hec.zcd.model.oclc.Resource;
import ca.hec.zcd.model.oclc.ResourceTypes;
import ca.hec.zcd.model.oclc.SearchResult;
import ca.hec.zcd.oclc.service.query.SearchQuery;

public class SearchCall extends ApiCall<SearchResult>
{
	private AccessToken token;

	@SuppressWarnings("serial")
	public SearchCall (final SearchQuery query, AccessToken token, final String urlBase, final String dbIds, final String hecId)
	{
		super(urlBase + "?q={{{query}}}&dbIds={{dbIds}}&heldBy={{hecId}}", new HashMap<String, Object>()
		{
			{
				put("query", query.buildDiscoveryApiQuery());
				put("dbIds", dbIds);
				put("hecId", hecId);
			}
		});

		this.token = token;
	}

	@Override
	public SearchResult execute () throws ApiCallException
	{
		getHeaders().put("Authorization", "Bearer " + token.getAccessToken());
		getHeaders().put("Accept", "application/json");

		ApiResponse response = this.get();

		try
		{
			return parseResult(response);
		}
		catch (JsonException e)
		{
			throw new ApiCallException(e);
		}
	}

	private SearchResult parseResult (ApiResponse response) throws JsonException
	{
		SearchResult ret = new SearchResult(JsonUtil.makePretty(response.getResult()));
		JsonMap map = new JsonMap(ret.getRaw());
		List<JsonMap> graph = map.getList("@graph");

		ret.setQuery(response.getQuery());

		if (graph.size() < 1)
		{
			throw new JsonException("@graph node must have at least 1 children");
		}

		JsonMap itemsPerPage = graph.get(0).getObject("discovery:itemsPerPage");

		if (itemsPerPage != null)
		{
			ret.setItemsPerPage(Integer.parseInt(itemsPerPage.getString("@value")));
		}

		JsonMap startIndex = graph.get(0).getObject("discovery:startIndex");

		if (startIndex != null)
		{
			ret.setStartIndex(Integer.parseInt(startIndex.getString("@value")));
		}

		JsonMap totalResults = graph.get(0).getObject("discovery:totalResults");

		if (totalResults != null)
		{
			ret.setTotalResults(Integer.parseInt(totalResults.getString("@value")));
		}

		if (graph.get(0).hasKey("discovery:hasPart"))
		{
			if (graph.get(0).isList("discovery:hasPart"))
			{
				List<JsonMap> discoveryParts = graph.get(0).getList("discovery:hasPart");

				for (JsonMap part : discoveryParts)
				{
					ret.getResources().add(parseResource(part, false));
				}
			}
			else
			{
				ret.getResources().add(parseResource(graph.get(0).getObject("discovery:hasPart"), false));
			}
		}

		return ret;
	}

	private Resource parseResource (JsonMap json, boolean isPartOf) throws JsonException
	{
		Resource ret = new Resource(json.toJson());

		if (!isPartOf)
		{
			json = json.getObject("schema:about");

			if (json == null)
			{
				return ret;
			}
		}

		try
		{
			ret.setId(json.getString("@id"));
			ret.setOclcNumber(json.getString("library:oclcnum"));

			parseType(json, ret);

			parseDates(json, ret);

			parseAuthors(json, ret);

			parseLocale(json, ret);

			ret.setName(parseName(json));

			parseIsbns(json, ret);

			if (isPartOf)
			{
				ret.setVolumeNumber(parseVolumeNumber(json));
				ret.setIssueNumber(parseIssueNumber(json));
				ret.setPartOfLabel(parsePartOfLabel(json));
			}

			parsePages(json, ret);

			parsePartOf(json, ret);

			parseDoi(json, ret);
		}
		catch (Exception e)
		{
			ret.setHasError(true);

			throw e;
		}

		return ret;
	}

	private void parseType (JsonMap json, Resource res) throws JsonException
	{
		List<String> types = new ArrayList<>();

		if (json.isList("@type"))
		{
			types.addAll(json.getStringList("@type"));
		}
		else if (json.isString("@type"))
		{
			types.add(json.getString("@type"));
		}

		for (String type : types)
		{
			for (ResourceTypes t : ResourceTypes.values())
			{
				if (type.equals(t.getOclcId()))
				{
					res.setType(t);

					return;
				}
			}
		}

		res.setType(ResourceTypes.UNKNOWN);
	}

	private void parseDates (JsonMap json, Resource res) throws JsonException
	{
		if (json.hasKey("schema:copyrightYear"))
		{
			if (json.isList("schema:copyrightYear"))
			{
				res.getCopyrightYears().addAll(json.getStringList("schema:copyrightYear"));
			}
			else
			{
				res.getCopyrightYears().add(json.getString("schema:copyrightYear"));
			}
		}

		if (json.hasKey("schema:datePublished"))
		{
			if (json.isString("schema:datePublished"))
			{
				res.getCopyrightYears().add(json.getString("schema:datePublished"));
			}
		}
	}

	private void parseAuthors (JsonMap json, Resource res) throws JsonException
	{
		parseAuthorsType(json, res, "schema:author", Author.Tag.AUTHOR);
		parseAuthorsType(json, res, "schema:creator", Author.Tag.CREATOR);
		parseAuthorsType(json, res, "schema:contributor", Author.Tag.CONTRIBUTOR);
	}

	private void parseAuthorsType (JsonMap json, Resource res, String key, Author.Tag tag) throws JsonException
	{
		if (json.hasKey(key))
		{
			if (json.isList(key))
			{
				List<JsonMap> list = json.getList(key);

				for (JsonMap map : list)
				{
					res.getAuthors().add(parseAuthor(map, tag));
				}
			}
			else
			{
				res.getAuthors().add(parseAuthor(json.getObject(key), tag));
			}
		}
	}

	private Author parseAuthor (JsonMap json, Author.Tag tag) throws JsonException
	{
		Author ret = new Author();

		ret.setId(json.getString("@id"));

		if (json.isList("schema:name"))
		{
			if (json.isStringList("schema:name"))
			{
				ret.setName(StringUtil.safeString(json.getStringList("schema:name").get(0)));
			}
			else
			{
				ret.setName(StringUtil.safeString(json.getList("schema:name").get(0).getString("@value")));
			}
		}
		else if (json.isString("schema:name"))
		{
			ret.setName(StringUtil.safeString(json.getString("schema:name")));
		}
		else
		{
			JsonMap name = json.getObject("schema:name");

			if (name != null)
			{
				ret.setName(StringUtil.safeString(name.getString("@value")));
			}
			else
			{
				ret.setName("");
			}
		}

		ret.setTag(tag);

		return ret;
	}

	private String parseName (JsonMap json) throws JsonException
	{
		if (!json.hasKey("schema:name"))
		{
			return "";
		}

		if (json.isString("schema:name"))
		{
			return json.getString("schema:name");
		}
		else if (json.isList("schema:name"))
		{
			if (json.isStringList("schema:name"))
			{
				return json.getStringList("schema:name").get(0);
			}
			else
			{
				return json.getList("schema:name").get(0).getString("@value");
			}
		}
		else
		{
			return json.getObject("schema:name").getString("@value");
		}
	}

	private void parseLocale (JsonMap json, Resource resource) throws JsonException
	{
		if (json.isList("schema:inLanguage"))
		{
			if (json.isStringList("schema:inLanguage"))
			{
				resource.setLocale(json.getStringList("schema:inLanguage").get(0));
			}
			else
			{
				resource.setLocale("eng");
			}
		}
		else if (json.isString("schema:inLanguage"))
		{
			resource.setLocale(json.getString("schema:inLanguage"));
		}
		else
		{
			resource.setLocale("eng");
		}
	}

	private void parseIsbns (JsonMap json, Resource resource) throws JsonException
	{
		if (!json.hasKey("schema:workExample"))
		{
			return;
		}

		List<JsonMap> works = new ArrayList<>();

		if (json.isList("schema:workExample"))
		{
			works.addAll(json.getList("schema:workExample"));
		}
		else
		{
			works.add(json.getObject("schema:workExample"));
		}

		for (JsonMap work : works)
		{
			if (!work.hasKey("schema:isbn"))
			{
				continue;
			}

			if (work.isString("schema:isbn"))
			{
				resource.getIsbns().add(work.getString("schema:isbn"));
			}
			else if (work.isStringList("schema:isbn"))
			{
				List<String> isbns = work.getStringList("schema:isbn");

				for (String isbn : isbns)
				{
					resource.getIsbns().add(isbn);
				}
			}
		}
	}

	private String parseVolumeNumber (JsonMap json)
	{
		if (json.hasKey("schema:volumeNumber"))
		{
			return json.getString("schema:volumeNumber");
		}

		return "?";
	}

	private String parseIssueNumber (JsonMap json)
	{
		if (json.hasKey("schema:issueNumber"))
		{
			return json.getString("schema:issueNumber");
		}

		return "?";
	}

	private String parsePartOfLabel (JsonMap json) throws JsonException
	{
		if (json.isString("http://www.w3.org/2000/01/rdf-schema#label"))
		{
			return json.getString("http://www.w3.org/2000/01/rdf-schema#label");
		}
		else if (json.isStringList("http://www.w3.org/2000/01/rdf-schema#label"))
		{
			return json.getStringList("http://www.w3.org/2000/01/rdf-schema#label").get(0);
		}

		return "?";
	}

	private void parsePages (JsonMap json, Resource resource) throws JsonException
	{
		if (json.hasKey("schema:pageStart"))
		{
			if (json.isStringList("schema:pageStart"))
			{
				resource.setPageStart(json.getStringList("schema:pageStart").get(0));
			}
			else if (json.isString("schema:pageStart"))
			{
				resource.setPageStart(json.getString("schema:pageStart"));
			}
			else
			{
				resource.setPageStart("?");
			}
		}
		else
		{
			resource.setPageStart("?");
		}

		if (json.hasKey("schema:pageEnd"))
		{
			if (json.isStringList("schema:pageEnd"))
			{
				resource.setPageEnd(json.getStringList("schema:pageEnd").get(0));
			}
			else if (json.isString("schema:pageEnd"))
			{
				resource.setPageEnd(json.getString("schema:pageEnd"));
			}
			else
			{
				resource.setPageEnd("?");
			}
		}
		else
		{
			resource.setPageEnd("?");
		}
	}

	private void parsePartOf (JsonMap json, Resource resource) throws JsonException
	{
		JsonMap partOf;

		if (json.isList("schema:isPartOf"))
		{
			partOf = json.getList("schema:isPartOf").get(0);
		}
		else
		{
			partOf = json.getObject("schema:isPartOf");
		}

		if (partOf == null)
		{
			return;
		}

		resource.setPartOf(parseResource(partOf, true));
	}

	private void parseDoi (JsonMap json, Resource resource) throws JsonException
	{
		if (json.hasKey("schema:sameAs"))
		{
			if (json.isString("schema:sameAs")) {
				resource.setDoi(json.getString("schema:sameAs"));
				return;
			}

			JsonMap sameAs = json.getObject("schema:sameAs");

			if (sameAs != null)
			{
				if (sameAs.hasKey("@id"))
				{
					if (sameAs.isString("@id"))
					{
						resource.setDoi(sameAs.getString("@id"));
					}
				}
			}
		}

		if (resource.getDoi() == null)
		{
			resource.setDoi("");
		}
	}
}
