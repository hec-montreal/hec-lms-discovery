package ca.hec.zcd.model.oclc;

import java.util.Date;

import ca.hec.util.DateUtil;
import lombok.Data;

@Data
public class AccessToken
{
	private String raw;
	private String accessToken;
	private String tokenType;
	private Date createdAt;
	private int expiresIn;

	public AccessToken ()
	{
		this.createdAt = new Date();
	}

	public boolean isExpired ()
	{
		return new Date().compareTo(DateUtil.addOffsetSec(createdAt, expiresIn)) > 0;
	}

	public boolean isValid ()
	{
		return accessToken != null && !isExpired();
	}
}
