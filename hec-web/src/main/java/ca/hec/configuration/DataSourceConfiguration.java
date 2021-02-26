package ca.hec.configuration;

import com.zaxxer.hikari.HikariDataSource;

public class DataSourceConfiguration
{
	private String name;
	private String driver;
	private String url;
	private String username;
	private String password;

	public DataSourceConfiguration(String name)
	{
		setName(name);
	}

	public HikariDataSource createDataSource ()
	{
		HikariDataSource ret = new HikariDataSource();

		ret.setDriverClassName(driver);
		ret.setJdbcUrl(url);
		ret.setUsername(username);
		ret.setPassword(password);

		return ret;
	}

	public String getName ()
	{
		return name;
	}

	public void setName (String name)
	{
		this.name = name;
	}

	public String getDriver ()
	{
		return driver;
	}

	public void setDriver (String driver)
	{
		this.driver = driver;
	}

	public String getUrl ()
	{
		return url;
	}

	public void setUrl (String url)
	{
		this.url = url;
	}

	public String getUsername ()
	{
		return username;
	}

	public void setUsername (String username)
	{
		this.username = username;
	}

	public String getPassword ()
	{
		return password;
	}

	public void setPassword (String password)
	{
		this.password = password;
	}
}
