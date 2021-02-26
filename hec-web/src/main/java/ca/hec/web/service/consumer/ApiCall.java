package ca.hec.web.service.consumer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import ca.hec.util.Logger;
import ca.hec.util.TemplateUtil;
import lombok.Getter;
import lombok.Setter;

public abstract class ApiCall<R>
{
	@Getter
	@Setter
	private Map<String, String> headers;

	@Getter
	private String url;

	public ApiCall (String url)
	{
		this.headers = new HashMap<>();
		this.url = url;
	}

	public ApiCall (String urlTemplate, Map<String, Object> model)
	{
		this(TemplateUtil.tryRender(urlTemplate, model));
	}

	public abstract R execute () throws ApiCallException;

	public ApiResponse get () throws ApiCallException
	{
		try
		{
			return doRequest(new HttpGet(url));
		}
		catch (IOException e)
		{
			throw new ApiCallException(e);
		}
	}

	public ApiResponse post (Map<String, String> parameters) throws ApiCallException
	{
		HttpPost post = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<>();

		for (String key : parameters.keySet())
		{
			params.add(new BasicNameValuePair(key, parameters.get(key)));
		}

		try
		{
			post.setEntity(new UrlEncodedFormEntity(params));

			return doRequest(post);
		}
		catch (Exception e)
		{
			throw new ApiCallException(e);
		}
	}

	protected static String urlEncode (String s)
	{
		try
		{
			return URLEncoder.encode(s, "utf-8");
		}
		catch (UnsupportedEncodingException e)
		{
			return "";
		}
	}

	private ApiResponse doRequest (HttpRequestBase request) throws ClientProtocolException, IOException, ApiCallException
	{
		if (!headers.containsKey("user-agent"))
		{
			headers.put("user-agent", "hec-montreal-api");
		}

		for (String key : headers.keySet())
		{
			request.setHeader(key, headers.get(key));
		}

		ApiResponse ret = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();

		try
		{
			Logger.info("Executing request (" + this.getUrl() + ")");

			CloseableHttpResponse response = httpClient.execute(request);

			try
			{
				HttpEntity entity = response.getEntity();
				String result = "";

				if (entity != null)
				{
					result = EntityUtils.toString(entity);
				}

				ret = new ApiResponse(response.getStatusLine().getStatusCode(), this.getUrl(), result);

				Logger.info("\tStatus code: " + ret.getStatusCode());

				Logger.info(ret.getResult());
			}
			finally
			{
				response.close();
			}
		}
		finally
		{
			httpClient.close();
		}

		return ret;
	}
}
