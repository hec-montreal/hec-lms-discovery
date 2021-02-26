package ca.hec.configuration;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class MailSenderFactory
{
	@Autowired
	private BaseApplicationProperties config;

	public MailSenderFactory()
	{

	}

	public JavaMailSender createMailSender () throws ConfigurationException
	{
		JavaMailSenderImpl ret = new JavaMailSenderImpl();

		configureMailSender(ret);

		return ret;
	}

	private void configureMailSender (JavaMailSenderImpl mailSender) throws ConfigurationException
	{
		MailConfiguration mailConfig = config.getMailConfiguration();

		mailSender.setHost(mailConfig.getHost());
		mailSender.setPort(mailConfig.getPort());

		if (mailConfig.isSmtpAuth())
		{
			mailSender.setUsername(mailConfig.getUsername());
			mailSender.setPassword(mailConfig.getPassword());
			mailSender.setProtocol(mailConfig.getProtocol());

			Properties javaMailProperties = new Properties();

			javaMailProperties.setProperty("mail.smtp.auth", "true");
			javaMailProperties.setProperty("mail.smpt.starttls.enable", String.valueOf(mailConfig.isSmtpEnableStarttls()));

			mailSender.setJavaMailProperties(javaMailProperties);
		}
	}
}
