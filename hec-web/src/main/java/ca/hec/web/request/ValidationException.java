package ca.hec.web.request;

public class ValidationException extends Exception
{
	private static final long serialVersionUID = 1L;

	private String fieldName;

	public ValidationException(String fieldName, String message)
	{
		super(message);

		this.fieldName = fieldName;
	}

	public String getFieldName ()
	{
		return fieldName;
	}
}
