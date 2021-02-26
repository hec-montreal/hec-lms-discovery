package ca.hec.zcd.oclc.service;

import java.util.HashMap;
import java.util.Map;

import ca.hec.util.HttpBasicAuthUtil;
import ca.hec.util.HttpBasicAuthUtil.HttpBasicAuthPair;
import ca.hec.util.JsonException;
import ca.hec.util.JsonUtil;
import ca.hec.web.service.consumer.ApiCall;
import ca.hec.web.service.consumer.ApiCallException;
import ca.hec.web.service.consumer.ApiResponse;
import ca.hec.zcd.model.oclc.AccessToken;
import ca.hec.zcd.ws.configuration.DiscoveryApiConfiguration;

public class AccessTokenCall extends ApiCall<AccessToken>
{
	private DiscoveryApiConfiguration configuration;

	public AccessTokenCall (DiscoveryApiConfiguration configuration)
	{
		super("https://oauth.oclc.org/token");

		this.configuration = configuration;
	}

	@Override
	public AccessToken execute () throws ApiCallException
	{
		getHeaders().put("Authorization", HttpBasicAuthUtil.createBasicAuth(new HttpBasicAuthPair(configuration.getKey(), configuration.getSecret())));

		AccessToken ret = new AccessToken();

		@SuppressWarnings("serial")
		ApiResponse response = this.post(new HashMap<String, String>()
		{
			{
				put("grant_type", configuration.getGrantType());
				put("scope", configuration.getScope());
			}
		});

		ret.setRaw(response.getResult());

		try
		{
			Map<String, Object> m = JsonUtil.jsonToMap(ret.getRaw());

			String errorCode = (String) m.get("error_code");

			if (errorCode != null)
			{
				throw new ApiCallException("Could not get access token (" + errorCode + ")");
			}

			ret.setAccessToken((String) m.get("access_token"));
			ret.setTokenType((String) m.get("token_type"));
			ret.setExpiresIn((Integer) m.get("expires_in"));
		}
		catch (JsonException e)
		{
			throw new ApiCallException(e);
		}

		return ret;
	}
}
