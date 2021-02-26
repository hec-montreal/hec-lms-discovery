package ca.hec.data.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.hec.util.JsonException;
import ca.hec.util.JsonUtil;

public class JsonMap
{
	private Map<String, Object> map;

	public JsonMap (String json) throws JsonException
	{
		this.map = JsonUtil.jsonToMap(json);
	}

	private JsonMap (Map<String, Object> map) throws JsonException
	{
		this.map = map;
	}

	public boolean hasKey (String key)
	{
		return get(key) != null;
	}

	public boolean isString (String key)
	{
		if (!hasKey(key))
		{
			return false;
		}

		return get(key) instanceof String;
	}

	public boolean isList (String key)
	{
		return get(key) instanceof List;
	}

	public boolean isStringList (String key)
	{
		if (!isList(key))
		{
			return false;
		}

		List<?> l = (List<?>) map.get(key);

		if (l.size() == 0)
		{
			return false;
		}

		return l.get(0) instanceof String;
	}

	@SuppressWarnings("unchecked")
	public JsonMap getObject (String key) throws JsonException
	{
		Map<String, Object> ret = (Map<String, Object>) get(key);

		if (ret == null)
		{
			return null;
		}

		return new JsonMap(ret);
	}

	@SuppressWarnings("unchecked")
	public List<JsonMap> getList (String key) throws JsonException
	{
		List<JsonMap> ret = new ArrayList<>();
		List<Object> l = (List<Object>) map.get(key);

		if (l == null)
		{
			throw new JsonException("No list value for key " + key);
		}

		for (Object o : l)
		{
			ret.add(new JsonMap((Map<String, Object>) o));
		}

		return ret;
	}

	@SuppressWarnings("unchecked")
	public List<String> getStringList (String key) throws JsonException
	{
		return (List<String>) map.get(key);
	}

	public String getString (String key)
	{
		return (String) get(key);
	}

	public Integer getInteger (String key)
	{
		return (Integer) get(key);
	}

	public Object get (String key)
	{
		return map.get(key);
	}

	public String toJson () throws JsonException
	{
		return JsonUtil.objectToJson(map);
	}
}
