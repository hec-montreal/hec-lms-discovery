package ca.hec.configuration;

import java.io.FileInputStream;
import java.util.Properties;

import ca.hec.util.StringUtil;

public class BaseApplicationProperties
{
	private String name;

	private Properties properties;

	public BaseApplicationProperties (String name) throws ConfigurationException
	{
		this.name = name;

		try
		{
			properties = new Properties();

			properties.load(new FileInputStream(System.getProperty("catalina.base") + "/properties/" + name));
		}
		catch (Exception e)
		{
			throw new ConfigurationException("Cannot load properties file");
		}

		if (properties == null)
		{
			throw new ConfigurationException("Cannot load properties file");
		}
	}

	public BaseApplicationProperties (String name, Properties properties)
	{
		this.name = name;
		this.properties = properties;
	}

	protected void setProperties (Properties properties)
	{
		this.properties = properties;
	}

	public String getProperty (String key)
	{
		return properties.getProperty(key);
	}

	public String getMandatoryProperty (String key) throws ConfigurationException
	{
		String ret = getProperty(key);

		if (ret == null)
		{
			throw new ConfigurationException("La propriété " + (key == null ? "[null]" : key) + " n'existe pas");
		}

		return ret;
	}

	public int getMandatoryIntProperty (String key) throws ConfigurationException
	{
		try
		{
			return Integer.parseInt(getMandatoryProperty(key));
		}
		catch (NumberFormatException e)
		{
			throw new ConfigurationException("Property '" + key + "' is not a valid integer");
		}
	}

	public double getMandatoryDoubleProperty (String key) throws ConfigurationException
	{
		try
		{
			return Double.parseDouble(getMandatoryProperty(key));
		}
		catch (NumberFormatException e)
		{
			throw new ConfigurationException("Property '" + key + "' is not a valid double");
		}
	}

	public DataSourceConfiguration getDataSourceConfiguration (String name) throws ConfigurationException
	{
		DataSourceConfiguration ret = new DataSourceConfiguration(name);

		ret.setName(name);
		ret.setDriver(getDataSourceProperty(name, "driver"));
		ret.setUrl(getDataSourceProperty(name, "url"));
		ret.setUsername(getDataSourceProperty(name, "username"));
		ret.setPassword(getDataSourceProperty(name, "password"));

		return ret;
	}

	public MailConfiguration getMailConfiguration () throws ConfigurationException
	{
		MailConfiguration ret = new MailConfiguration();

		ret.setHost(getMandatoryProperty("app.mail.host"));
		ret.setPort(getMandatoryIntProperty("app.mail.port"));

		ret.setUsername(getProperty("app.mail.username"));

		if (ret.getUsername() != null)
		{
			ret.setPassword(getMandatoryProperty("app.mail.password"));
			ret.setProtocol(getMandatoryProperty("app.mail.protocol"));
			ret.setSmtpAuth(getMandatoryProperty("app.mail.smpt.auth").equals("true"));
			ret.setSmtpEnableStarttls(getMandatoryProperty("app.mail.smpt.starttls.enable").equals("true"));
			ret.setFrom(getMandatoryProperty("app.mail.from"));
		}
		else
		{
			ret.setFrom(getMandatoryProperty("app.mail.from"));
			ret.setSmtpAuth(false);
		}

		String whitelist = getProperty("app.mail.whitelist");

		if (!StringUtil.isStringEmpty(whitelist))
		{
			for (String w : whitelist.split(","))
			{
				ret.getWhitelist().add(w);
			}
		}

		return ret;
	}

	private String getDataSourceProperty (String sourceName, String prop) throws ConfigurationException
	{
		return getMandatoryProperty("app.data.source." + sourceName + "." + prop);
	}

	public String getName ()
	{
		return name;
	}

	public Properties getProperties ()
	{
		return properties;
	}
}
