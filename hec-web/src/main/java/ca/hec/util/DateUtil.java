package ca.hec.util;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil
{
	private static final String DEFAULT_OUT_FORMAT = "dd/MM/yyyy HH:mm:ss";
	private static final String ORACLE_IN_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String ORACLE_OUT_FORMAT = "yyyyMMddHHmmss";
	private static final String GUICHET_OUT_FORMAT = "yyyy/MM/dd HH:mm:ss";
	private static final String PS_COMMENT_FORMAT = "yyyy-MM-dd";
	private static final String ONLY_DATE_FORMAT = "yyyy/MM/dd";

	public static Date parseOracleDate (String date) throws ParseException
	{
		if (date == null)
		{
			return null;
		}

		return new SimpleDateFormat(ORACLE_IN_FORMAT).parse(date);
	}

	public static Date parseOnlyDateOrNull (String date)
	{
		try
		{
			return parseOnlyDate(date);
		}
		catch (ParseException e)
		{
			return null;
		}
	}

	public static Date parseOnlyDate (String date) throws ParseException
	{
		SimpleDateFormat fmt = new SimpleDateFormat(ONLY_DATE_FORMAT);

		fmt.setLenient(false);

		return fmt.parse(date);
	}

	public static Date parseOnlyDate (String date, String format) throws ParseException
	{
		SimpleDateFormat fmt = new SimpleDateFormat(format);

		fmt.setLenient(false);

		return fmt.parse(date);
	}

	public static Date parsePsCommentDate (String date) throws ParseException
	{
		return new SimpleDateFormat(PS_COMMENT_FORMAT).parse(date);
	}

	public static Date parseExpirationDate (String date)
	{
		if (date == null)
		{
			return null;
		}

		String[] comps = date.split("/");

		if (comps.length < 2)
		{
			return null;
		}

		try
		{
			int month = Integer.parseInt(comps[0]);
			int year = Integer.parseInt(comps[1]);

			Calendar cal = Calendar.getInstance();

			cal.set(Calendar.MONTH, month - 1);
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.DAY_OF_MONTH, 1);

			cal.add(Calendar.MONTH, 1);

			return cal.getTime();
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	public static String formatOracleDate (Date date)
	{
		return new SimpleDateFormat(ORACLE_OUT_FORMAT).format(date);
	}

	public static String formatDate (Date date)
	{
		return new SimpleDateFormat(DEFAULT_OUT_FORMAT).format(date);
	}

	public static String formatDate (Date date, String format)
	{
		return new SimpleDateFormat(format).format(date);
	}

	public static String formatOnlyDate (Date date)
	{
		if (date == null)
		{
			return "";
		}

		return new SimpleDateFormat(ONLY_DATE_FORMAT).format(date);
	}

	public static String formatDateGuichet (Date date)
	{
		return new SimpleDateFormat(GUICHET_OUT_FORMAT).format(date);
	}

	public static String formatDatePSComment (Date date)
	{
		return new SimpleDateFormat(PS_COMMENT_FORMAT).format(date);
	}

	public static boolean isDateInRangeInclusive (Date date, Date start, Date end)
	{
		return date.compareTo(start) >= 0 && date.compareTo(end) <= 0;
	}

	public static boolean isDateAfter (Date date, Date end)
	{
		return date.compareTo(end) > 0;
	}

	public static Date addOffsetSec (Date start, int offsetSec)
	{
		Calendar cal = Calendar.getInstance();

		cal.setTime(start);
		cal.add(Calendar.SECOND, offsetSec);

		return cal.getTime();
	}

	public static Date advanceToEndOfDay (Date date)
	{
		Calendar cal = Calendar.getInstance();

		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);

		return cal.getTime();
	}

	public static Date getFirstDayOfMonth (Date month)
	{
		Calendar cal = Calendar.getInstance();

		cal.setTime(month);
		cal.set(Calendar.DAY_OF_MONTH, 1);

		return cal.getTime();
	}

	public static Date getLastDayOfMonth (Date month)
	{
		Calendar cal = Calendar.getInstance();

		cal.setTime(month);

		int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		cal.set(Calendar.DAY_OF_MONTH, daysInMonth);

		return cal.getTime();
	}

	public static long countDaysBetween (Date start, Date end)
	{
		if (start == null || end == null)
		{
			return 0;
		}

		long diff = end.getTime() - start.getTime();

		return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}

	public static long countHoursBetween (Date start, Date end)
	{
		if (start == null || end == null)
		{
			return 0;
		}

		long diff = end.getTime() - start.getTime();

		return TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
	}

	public static int getCurrentYear ()
	{
		return getYear(new Date());
	}

	public static int getYear (Date date)
	{
		Calendar cal = Calendar.getInstance();

		cal.setTime(date);

		return cal.get(Calendar.YEAR);
	}

	public static String formatDateWeb (Date date)
	{
		return formatDate(date, "yyyy-MM-dd");
	}

	public static Date parseDateWeb (String date) throws ParseException
	{
		return parseOnlyDate(date, "yyyy-MM-dd");
	}

	public static Date parseDateWebOrNull (String date)
	{
		try
		{
			return parseDateWeb(date);
		}
		catch (ParseException e)
		{
			return null;
		}
	}

	public static class DateRange
	{
		private Long start;
		private Long end;

		public DateRange (Date start, Date end)
		{
			this.start = start.getTime();
			this.end = end.getTime();
		}

		public boolean overlapsWith (DateRange other)
		{
			return Math.max(start, other.start) <= Math.min(end, other.end);
		}
	}
}
