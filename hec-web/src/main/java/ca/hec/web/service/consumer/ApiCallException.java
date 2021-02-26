package ca.hec.web.service.consumer;

public class ApiCallException extends Exception
{
	private static final long serialVersionUID = 1L;

	public ApiCallException(Exception cause)
	{
		super(cause);
	}

	public ApiCallException(String message)
	{
		super(message);
	}
}
