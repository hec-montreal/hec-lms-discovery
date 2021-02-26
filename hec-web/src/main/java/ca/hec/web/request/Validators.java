package ca.hec.web.request;

import java.util.List;

import ca.hec.util.StringUtil;

public class Validators
{
	public static Validator NOT_EMPTY = new Validator()
	{
		@Override
		public ValidationResults validate (Object value)
		{
			if (value == null)
			{
				return ValidationResults.EMPTY;
			}

			if (value instanceof String)
			{
				if (StringUtil.isStringEmpty((String) value))
				{
					return ValidationResults.EMPTY;
				}
			}

			if (value instanceof List)
			{
				if (((List<?>) value).size() == 0)
				{
					return ValidationResults.EMPTY;
				}
			}

			return ValidationResults.OK;
		}
	};

	public static Validator MAY_BE_EMPTY = new Validator()
	{
		@Override
		public ValidationResults validate (Object value)
		{
			return ValidationResults.OK;
		}
	};
}
