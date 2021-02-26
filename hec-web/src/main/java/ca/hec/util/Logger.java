package ca.hec.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;

import ca.hec.util.HttpBasicAuthUtil.HttpBasicAuthPair;
import ca.hec.web.request.Body;
import ca.hec.web.request.ValidationException;

public class Logger
{
	public static interface CurrentUsernameExtractor
	{
		String getCurrentUsername (HttpServletRequest request);
	}

	private static org.apache.logging.log4j.Logger getLogger ()
	{
		return LogManager.getLogger();
	}

	public static void info (String message)
	{
		getLogger().info(message);
	}

	public static void error (Throwable t)
	{
		getLogger().error("Error: ");
		getLogger().error(t);

		if (t instanceof ValidationException)
		{
			String field = ((ValidationException) t).getFieldName();

			if (field == null)
			{
				field = "?";
			}

			getLogger().error("Field: " + field);
		}
	}

	public static void request (HttpServletRequest request, String description)
	{
		request(request, description, null);
	}

	public static void request (HttpServletRequest request, String description, CurrentUsernameExtractor currentUsernameExtractor)
	{
		StringBuffer buff = new StringBuffer();

		buff.append("Http request (" + description + ")\n");

		// Get full url
		StringBuffer fullUrl = request.getRequestURL();

		if (request.getQueryString() != null)
		{
			fullUrl.append("?").append(request.getQueryString());
		}

		buff.append("\t" + request.getMethod() + " " + fullUrl.toString() + "\n");

		// Auth
		try
		{
			HttpBasicAuthPair pair = HttpBasicAuthUtil.getBasicAuth(request);

			if (!StringUtil.isStringEmpty(pair.getUsername()))
			{
				buff.append("\tUser: " + pair.getUsername() + "\n");
			}
		}
		catch (NoHttpBasicAuthException e)
		{

		}
		catch (InvalidHttpBasicAuthException e)
		{
			buff.append("\tInvalid basic auth\n");
		}

		// Current username
		if (currentUsernameExtractor != null)
		{
			String currentUsername = currentUsernameExtractor.getCurrentUsername(request);

			if (!StringUtil.isStringEmpty(currentUsername))
			{
				buff.append("\tCurrent user: " + currentUsername + "\n");
			}
		}

		Body body = new Body(request.getParameterMap());

		buff.append("\tParameters:\n");
		buff.append("\t" + body.summary());

		info(buff.toString());
	}
}
