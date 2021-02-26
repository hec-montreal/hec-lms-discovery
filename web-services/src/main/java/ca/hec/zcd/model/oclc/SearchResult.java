package ca.hec.zcd.model.oclc;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SearchResult
{
	private List<Resource> resources;
	private String raw;
	private int itemsPerPage;
	private int startIndex;
	private int totalResults;
	private String query;

	public SearchResult (String raw)
	{
		this.raw = raw;

		this.resources = new ArrayList<>();
	}

	public int getCurrentPage ()
	{
		return startIndex / itemsPerPage;
	}
}
