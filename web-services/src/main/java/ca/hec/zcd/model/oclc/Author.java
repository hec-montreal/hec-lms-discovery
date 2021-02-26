package ca.hec.zcd.model.oclc;

import lombok.Data;

@Data
public class Author
{
	public static enum Tag
	{
		AUTHOR,
		CREATOR,
		CONTRIBUTOR
	}

	private String id;
	private String name;
	private Tag tag;
}
