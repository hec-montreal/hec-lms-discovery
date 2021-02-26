package ca.hec.zcd.model.oclc;

import lombok.Getter;

@Getter
public enum ResourceTypes
{
	BOOK("schema:Book", "Livre", "book", "book"),
	ARTICLE("schema:Article", "Article", "article", "journal"),
	CHAPTER("schema:Chapter", "Chapitre", "book", "book"),
	UNKNOWN("", "Inconnu", "Unknown", "Unknown");

	private String oclcId;
	private String label;
	private String sakaiType;
	private String openUrlType;

	private ResourceTypes (String oclcId, String label, String sakaiType, String openUrlType)
	{
		this.oclcId = oclcId;
		this.label = label;
		this.sakaiType = sakaiType;
		this.openUrlType = openUrlType;
	}
}
