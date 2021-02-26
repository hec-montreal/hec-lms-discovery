package ca.hec.util;

public class JsonException extends Exception
{
	private static final long serialVersionUID = 1L;

	public JsonException (Exception e)
	{
		super(e);
	}

	public JsonException (String msg)
	{
		super(msg);
	}
}
