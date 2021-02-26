package ca.hec.util;

import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.text.RandomStringGenerator;

public class StringUtil
{
	public static interface ValueExtractor<T>
	{
		String extractValue (T obj);
	}

	public static String safeString (String str)
	{
		if (str == null)
		{
			return "";
		}

		return str;
	}

	public static boolean isStringEmpty (String str)
	{
		return str == null || str.length() == 0;
	}

	public static String join (List<String> list, String separator)
	{
		return join(list, new StringValueExtractor(), separator);
	}

	public static <T> String join (List<T> list, ValueExtractor<T> extractor, String separator)
	{
		StringBuffer ret = new StringBuffer();

		for (int i = 0; i < list.size(); i++)
		{
			ret.append(extractor.extractValue(list.get(i)));

			if (i < list.size() - 1)
			{
				ret.append(separator);
			}
		}

		return ret.toString();
	}

	public static String encodeBase64 (byte[] array)
	{
		return Base64.encodeBase64String(array);
	}

	public static String encodeBase64 (String input, Charset charset)
	{
		return encodeBase64(input.getBytes(charset));
	}

	public static byte[] decodeBase64 (String base64)
	{
		return Base64.decodeBase64(base64);
	}

	public static String encodeHex (byte[] array)
	{
		return Hex.encodeHexString(array);
	}

	public static String padLeft (String str, int totalLength, String padWith)
	{
		StringBuffer ret = new StringBuffer();

		while (ret.length() + str.length() < totalLength)
		{
			ret.append(padWith);
		}

		return ret.toString() + str;
	}

	public static String randomString (int length)
	{
		return new RandomStringGenerator.Builder().withinRange('a', 'z').build().generate(length);
	}

	@SuppressWarnings("deprecation")
	public static String capitalizeAllWords (String str)
	{
		return WordUtils.capitalize(str);
	}

	public static class StringValueExtractor implements ValueExtractor<String>
	{
		@Override
		public String extractValue (String obj)
		{
			return obj;
		}
	}
}
