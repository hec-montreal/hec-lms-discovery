package ca.hec.zcd.ws.controller;

import org.springframework.beans.factory.annotation.Autowired;

import ca.hec.configuration.ConfigurationException;
import ca.hec.util.Logger;
import ca.hec.web.service.consumer.ApiCallException;
import ca.hec.zcd.model.oclc.AccessToken;
import ca.hec.zcd.oclc.service.AccessTokenCall;
import ca.hec.zcd.ws.configuration.ApplicationProperties;

public class BaseController
{
	@Autowired
	private ApplicationProperties properties;

	private AccessToken token = null;

	protected AccessToken checkToken () throws ApiCallException, ConfigurationException
	{
		if (token == null || token.isExpired())
		{
			Logger.info("Getting new token");

			token = new AccessTokenCall(properties.getDiscoveryApiConfiguration()).execute();
		}

		return token;
	}

	protected ApplicationProperties getApplicationProperties ()
	{
		return properties;
	}
}
