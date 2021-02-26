package ca.hec.web.request;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.hec.util.DateUtil;
import ca.hec.util.StringUtil;

public class Body
{
	private String name;
	private Map<String, ?> parameters;

	public Body (Map<String, ?> parameters)
	{
		this("root", parameters);
	}

	public String summary ()
	{
		StringBuffer ret = new StringBuffer();

		for (String key : parameters.keySet())
		{
			Object param = parameters.get(key);

			if (param instanceof Object[])
			{
				param = ((Object[]) param)[0];
			}

			ret.append(key += ": " + param.toString() + ", ");
		}

		if (ret.length() > 2)
		{
			return ret.substring(0, ret.length() - 2).toString();
		}
		else
		{
			return "";
		}
	}

	public Body (String name, Map<String, ?> parameters)
	{
		this.name = name;
		this.parameters = parameters;
	}

	public String validateString (String key, Validator validator) throws ValidationException
	{
		return (String) validateObject(key, validator);
	}

	public String validateString (String key, Validator validator, String errorMessage) throws ValidationException
	{
		return (String) validateObject(key, validator, errorMessage);
	}

	public String validateEmail (String key, Validator validator) throws ValidationException
	{
		String s = validateString(key, validator);

		if (s == null)
		{
			return null;
		}

		Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(s);

		if (!m.find())
		{
			throw new ValidationException(path(key), "Courriel invalide");
		}

		return s;
	}

	public Integer validateInteger (String key, Validator validator) throws ValidationException
	{
		Object ret = validateObject(key, validator);

		if (ret instanceof String)
		{
			if (((String) ret).length() == 0)
			{
				return null;
			}

			try
			{
				return Integer.parseInt((String) ret);
			}
			catch (NumberFormatException e)
			{
				throw new ValidationException(path(key), "Nombre entier invalide");
			}
		}
		else
		{
			return (Integer) validateObject(key, validator);
		}
	}

	public Integer validateInteger (String key, Validator validator, int min) throws ValidationException
	{
		Integer ret = validateInteger(key, validator);

		if (ret < min)
		{
			throw new ValidationException(key, "Nombre entier doit être plus petit que " + min);
		}

		return ret;
	}

	public Integer validateInteger (String key, Validator validator, int min, int max) throws ValidationException
	{
		Integer ret = validateInteger(key, validator);

		if (ret < min || ret > max)
		{
			throw new ValidationException(key, "Nombre entier doit être entre " + min + " et " + max);
		}

		return ret;
	}

	public Long validateLong (String key, Validator validator) throws ValidationException
	{
		Integer i = validateInteger(key, validator);

		if (i == null)
		{
			return null;
		}

		return new Long(i);
	}

	public Boolean validateStringBoolean (String key, Validator validator) throws ValidationException
	{
		String s = validateString(key, validator);

		if (s == null)
		{
			return null;
		}

		s = s.toLowerCase();

		if (s.equals("1") || s.equals("oui") || s.equals("true"))
		{
			return true;
		}
		else if (s.equals("0") || s.equals("non") || s.equals("false"))
		{
			return false;
		}
		else
		{
			throw new ValidationException(path(key), "Valeur booléenne invalide");
		}
	}

	public Date validateDate (String key, Validator validator) throws ValidationException
	{
		try
		{
			Date ret = DateUtil.parseDateWeb(validateString(key, validator));

			if (ret != null)
			{
				Calendar cal = Calendar.getInstance();

				cal.setTime(ret);

				if (cal.get(Calendar.YEAR) < 2000 || cal.get(Calendar.YEAR) > 2100)
				{
					throw new ValidationException(path(key), "Date invalide");
				}
			}

			return ret;
		}
		catch (ParseException e)
		{
			throw new ValidationException(path(key), "Date invalide");
		}
	}

	public Body validateChild (String key, Validator validator) throws ValidationException
	{
		return (Body) validateObject(key, validator);
	}

	@SuppressWarnings("unchecked")
	public List<Body> validateChildren (String key, Validator validator) throws ValidationException
	{
		List<Body> ret = new ArrayList<>();

		List<?> children = (List<?>) validateObject(key, validator);

		for (int i = 0; i < children.size(); i++)
		{
			ret.add(new Body(path(key, "" + (i + 1)), (Map<String, ?>) children.get(i)));
		}

		return ret;
	}

	public Boolean validateBoolean (String key, Validator validator) throws ValidationException
	{
		return (Boolean) validateObject(key, validator);
	}

	public String validateTitle (String key, Validator validator) throws ValidationException
	{
		String value = validateString(key, validator);

		if (value == null)
		{
			return null;
		}

		if (!value.toLowerCase().equals("monsieur") && !value.toLowerCase().equals("madame"))
		{
			throw new ValidationException(path(key), "Titre invalide");
		}

		return Character.toUpperCase(value.charAt(0)) + value.toLowerCase().substring(1);
	}

	public Object validateObject (String key, Validator validator) throws ValidationException
	{
		return validateObject(key, validator, "Champ obligatoire");
	}

	@SuppressWarnings("unchecked")
	public Object validateObject (String key, Validator validator, String errorMessage) throws ValidationException
	{
		Object ret = parameters.get(key);

		// In case of HttpServletRequest parameter map
		if (ret instanceof String[])
		{
			String[] reta = (String[]) ret;

			if (reta != null && reta.length > 0)
			{
				ret = reta[0];
			}
			else
			{
				ret = null;
			}
		}

		if (ret instanceof Map)
		{
			ret = new Body(path(key), (Map<String, Object>) ret);
		}

		if (validator.validate(ret) == ValidationResults.EMPTY)
		{
			throw new ValidationException(path(key), errorMessage);
		}

		return ret;
	}

	public String getName ()
	{
		return name;
	}

	public Map<String, ?> getParameters ()
	{
		return parameters;
	}

	public String path (String... keys)
	{
		List<String> akeys = new ArrayList<>();
		akeys.add(name);
		akeys.addAll(Arrays.asList(keys));

		return StringUtil.join(akeys, ".");
	}
}
