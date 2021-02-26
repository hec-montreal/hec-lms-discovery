package ca.hec.zcd.ws.configuration;

import ca.hec.configuration.BaseApplicationProperties;
import ca.hec.configuration.ConfigurationException;

public class ApplicationProperties extends BaseApplicationProperties
{
	public ApplicationProperties (String name) throws ConfigurationException
	{
		super(name);
	}

	public DiscoveryApiConfiguration getDiscoveryApiConfiguration () throws ConfigurationException
	{
		return new DiscoveryApiConfiguration(this);
	}
}
