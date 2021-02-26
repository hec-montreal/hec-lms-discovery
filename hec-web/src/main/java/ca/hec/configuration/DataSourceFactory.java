package ca.hec.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

public class DataSourceFactory
{
	@Autowired
	private BaseApplicationProperties configuration;

	public DataSource create (String name) throws ConfigurationException
	{
		return configuration.getDataSourceConfiguration(name).createDataSource();
	}
}
