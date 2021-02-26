package ca.hec.configuration;

import java.util.ArrayList;
import java.util.List;

public class MailConfiguration
{
	private String from;
	private String host;
	private int port;
	private String username;
	private String password;
	private String protocol;
	private boolean smtpAuth;
	private boolean smtpEnableStarttls;
	private List<String> whitelist;

	public MailConfiguration()
	{
		this.whitelist = new ArrayList<>();
	}

	public String getFrom ()
	{
		return from;
	}

	public void setFrom (String from)
	{
		this.from = from;
	}

	public String getHost ()
	{
		return host;
	}

	public void setHost (String host)
	{
		this.host = host;
	}

	public int getPort ()
	{
		return port;
	}

	public void setPort (int port)
	{
		this.port = port;
	}

	public String getUsername ()
	{
		return username;
	}

	public void setUsername (String username)
	{
		this.username = username;
	}

	public String getPassword ()
	{
		return password;
	}

	public void setPassword (String password)
	{
		this.password = password;
	}

	public String getProtocol ()
	{
		return protocol;
	}

	public void setProtocol (String protocol)
	{
		this.protocol = protocol;
	}

	public boolean isSmtpAuth ()
	{
		return smtpAuth;
	}

	public void setSmtpAuth (boolean smtpAuth)
	{
		this.smtpAuth = smtpAuth;
	}

	public boolean isSmtpEnableStarttls ()
	{
		return smtpEnableStarttls;
	}

	public void setSmtpEnableStarttls (boolean smtpEnableStarttls)
	{
		this.smtpEnableStarttls = smtpEnableStarttls;
	}

	public List<String> getWhitelist ()
	{
		return whitelist;
	}

	public void setWhitelist (List<String> whitelist)
	{
		this.whitelist = whitelist;
	}
}
