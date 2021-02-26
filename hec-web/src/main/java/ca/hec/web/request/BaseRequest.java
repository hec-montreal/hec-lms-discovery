package ca.hec.web.request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public abstract class BaseRequest<T extends BaseRequest<T>>
{
	public abstract T fromHttpRequest (HttpServletRequest request) throws ValidationException;

	@SuppressWarnings("unchecked")
	protected Body readBody (HttpServletRequest request) throws ValidationException
	{
		try
		{
			String body = IOUtils.toString(request.getInputStream(), "utf-8");

			return new Body((Map<String, Object>) new ObjectMapper().readValue(body, new TypeReference<HashMap<String, Object>>()
			{
			}));
		}
		catch (IOException e)
		{
			throw new ValidationException("body", "Cannot read request body");
		}
	}
}
