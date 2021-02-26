package ca.hec.zcd.ws.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.hec.configuration.ConfigurationException;
import ca.hec.util.JsonException;
import ca.hec.util.JsonUtil;
import ca.hec.web.service.consumer.ApiCallException;
import ca.hec.zcd.model.oclc.SearchResult;
import ca.hec.zcd.oclc.service.SearchCall;
import ca.hec.zcd.oclc.service.SearchException;
import ca.hec.zcd.oclc.service.query.SearchQuery;
import ca.hec.zcd.ws.configuration.DiscoveryApiConfiguration;

@Controller
public class ResourceController extends BaseController
{
	@RequestMapping(value = "/v1/resources/search", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public String search (HttpServletRequest request, HttpServletResponse response) throws ApiCallException, ConfigurationException, JsonException, SearchException
	{
		response.setHeader("Access-Control-Allow-Origin", "*");

		DiscoveryApiConfiguration config = getApplicationProperties().getDiscoveryApiConfiguration();

		SearchResult result = new SearchCall(new SearchQuery(request.getParameter("query")), checkToken(), config.getUrlBase(), config.getDbIds(), config.getHecId()).execute();

		return JsonUtil.objectToJson(result);
	}
}
