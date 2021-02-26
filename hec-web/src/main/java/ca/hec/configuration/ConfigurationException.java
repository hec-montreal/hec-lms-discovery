package ca.hec.configuration;

public class ConfigurationException extends Exception
{
	private static final long serialVersionUID = 1L;

	public ConfigurationException(String message)
	{
		super(message);
	}

	public ConfigurationException(Exception e)
	{
		super(e);
	}
}
