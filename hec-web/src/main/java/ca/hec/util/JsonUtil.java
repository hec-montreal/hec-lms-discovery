package ca.hec.util;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil
{
	public static Map<String, Object> jsonToMap (String json) throws JsonException
	{
		try
		{
			return new ObjectMapper().readValue(json, new TypeReference<Map<String, Object>>()
			{
			});
		}
		catch (Exception e)
		{
			throw new JsonException(e);
		}
	}

	public static String objectToJson (Object object) throws JsonException
	{
		try
		{
			return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
		}
		catch (JsonProcessingException e)
		{
			throw new JsonException(e);
		}
	}

	public static String makePretty (String json) throws JsonException
	{
		return objectToJson(jsonToMap(json));
	}
}
