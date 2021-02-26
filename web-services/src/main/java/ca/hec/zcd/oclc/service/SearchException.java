package ca.hec.zcd.oclc.service;

public class SearchException extends Exception
{
	private static final long serialVersionUID = 1L;

	public SearchException (Exception e)
	{
		super(e);
	}

	public SearchException (String msg)
	{
		super(msg);
	}
}
