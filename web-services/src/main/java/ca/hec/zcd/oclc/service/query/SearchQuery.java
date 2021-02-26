package ca.hec.zcd.oclc.service.query;

import lombok.Data;

@Data
public class SearchQuery
{
	private String query;

	public SearchQuery (String query)
	{
		this.query = query;
	}

	public String buildDiscoveryApiQuery ()
	{
		return query;
	}
}
