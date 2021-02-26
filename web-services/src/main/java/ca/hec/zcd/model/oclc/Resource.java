package ca.hec.zcd.model.oclc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ca.hec.util.StringUtil;
import lombok.Data;

@Data
public class Resource
{
	private String id;
	private String oclcNumber;
	private List<String> copyrightYears;
	private ResourceTypes type;
	private List<Author> authors;
	private String locale;
	private String name;
	private List<String> isbns;
	private String raw;
	private Resource partOf;
	private String volumeNumber;
	private String issueNumber;
	private String pageStart;
	private String pageEnd;
	private String partOfLabel;
	private String placeOfPublication;
	private String doi;
	private boolean hasError;

	public Resource (String raw)
	{
		this.hasError = false;

		this.raw = raw;

		this.copyrightYears = new ArrayList<>();
		this.authors = new ArrayList<>();
		this.isbns = new ArrayList<>();
	}

	public String getAuthorsLine ()
	{
		if (authors.size() == 0)
		{
			return "";
		}

		Collections.sort(authors, new Comparator<Author>()
		{
			@Override
			public int compare (Author o1, Author o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});

		StringBuffer ret = new StringBuffer();

		for (int i = 0; i < authors.size(); i++)
		{
			if (authors.get(i).getName().length() > 0)
			{
				ret.append(authors.get(i).getName());

				if (i < authors.size() - 1)
				{
					ret.append(", ");
				}
			}
		}

		return ret.toString();
	}

	public String getTypeName ()
	{
		if (this.getType() == null)
		{
			return "";
		}

		return this.getType().getLabel();
	}

	public String getSakaiType ()
	{
		if (this.getType() == null)
		{
			return "";
		}

		return this.getType().getSakaiType();
	}

	public String getOpenUrlType ()
	{
		if (this.getType() == null)
		{
			return "";
		}

		return this.getType().getOpenUrlType();
	}

	public String getLink ()
	{
		if (!StringUtil.isStringEmpty(doi))
		{
			return this.getDoi().replaceFirst("http.*://dx.doi.org/", "http://proxy2.hec.ca/login?url=http://dx.doi.org/");
		}

		return this.getId().replaceFirst("http.*://www.worldcat.org/", "https://hecmontreal.on.worldcat.org/");
	}
}
