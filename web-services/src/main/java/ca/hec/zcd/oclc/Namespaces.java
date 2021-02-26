package ca.hec.zcd.oclc;

import lombok.Getter;

@Getter
public enum Namespaces
{
	OPEN_SEARCH("http://a9.com/-/spec/opensearch/1.1/");

	private String url;

	private Namespaces (String url)
	{
		this.url = url;
	}
}
