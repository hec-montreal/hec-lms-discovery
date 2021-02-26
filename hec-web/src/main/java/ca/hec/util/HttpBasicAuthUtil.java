package ca.hec.util;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;

public class HttpBasicAuthUtil
{
	private static final String ENCODING = "utf-8";

	public static String getBasicAuthString (HttpServletRequest request) throws NoHttpBasicAuthException
	{
		String ret = request.getHeader("Authorization");

		if (StringUtil.isStringEmpty(ret))
		{
			throw new NoHttpBasicAuthException();
		}

		return ret;
	}

	public static HttpBasicAuthPair getBasicAuth (HttpServletRequest request) throws NoHttpBasicAuthException, InvalidHttpBasicAuthException
	{
		String basic = getBasicAuthString(request);
		String decoded = null;

		if (!basic.startsWith("Basic "))
		{
			throw new InvalidHttpBasicAuthException();
		}

		basic = basic.replace("Basic ", "");

		try
		{
			decoded = new String(Base64.decodeBase64(basic.getBytes(ENCODING)), ENCODING);
		}
		catch (Exception e)
		{
			throw new InvalidHttpBasicAuthException();
		}

		return new HttpBasicAuthPair(decoded);
	}

	public static String createBasicAuth (HttpBasicAuthPair pair)
	{
		return "Basic " + pair.encode();
	}

	public static class HttpBasicAuthPair
	{
		private String username;
		private String password;

		public HttpBasicAuthPair(String username, String password)
		{
			this.username = username;
			this.password = password;
		}

		public HttpBasicAuthPair(String basicAuth) throws InvalidHttpBasicAuthException
		{
			String[] comps = basicAuth.split(":");

			if (comps.length < 2)
			{
				throw new InvalidHttpBasicAuthException();
			}

			username = comps[0];
			password = comps[1];

			if (StringUtil.isStringEmpty(username) || StringUtil.isStringEmpty(password))
			{
				throw new InvalidHttpBasicAuthException();
			}
		}

		public String encode ()
		{
			try
			{
				return Base64.encodeBase64String((username + ":" + password).getBytes("utf-8"));
			}
			catch (UnsupportedEncodingException e)
			{
				return null;
			}
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
}
