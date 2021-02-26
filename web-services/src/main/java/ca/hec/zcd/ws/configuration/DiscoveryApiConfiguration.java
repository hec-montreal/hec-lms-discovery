package ca.hec.zcd.ws.configuration;

import ca.hec.configuration.ConfigurationException;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiscoveryApiConfiguration
{
	private String urlBase;
	private String key;
	private String secret;
	private String grantType;
	private String scope;
	private String dbIds;
	private String hecId;

	public DiscoveryApiConfiguration (ApplicationProperties properties) throws ConfigurationException
	{
		this.urlBase = properties.getMandatoryProperty("discovery.api.urlBase");
		this.key = properties.getMandatoryProperty("discovery.api.key");
		this.secret = properties.getMandatoryProperty("discovery.api.secret");
		this.grantType = properties.getMandatoryProperty("discovery.api.grantType");
		this.scope = properties.getMandatoryProperty("discovery.api.scope");
		this.dbIds = properties.getMandatoryProperty("discovery.api.dbIds");
		this.hecId = properties.getMandatoryProperty("discovery.api.ownId");
	}
}
