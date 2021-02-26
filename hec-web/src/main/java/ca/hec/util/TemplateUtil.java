package ca.hec.util;

import java.io.IOException;
import java.util.Map;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

public class TemplateUtil
{
	private final static Handlebars HANDLEBARS = new Handlebars();

	public static String render (String template, Map<String, Object> model) throws TemplateException
	{
		try
		{
			Template t = HANDLEBARS.compileInline(template);

			return t.apply(model);
		}
		catch (IOException e)
		{
			throw new TemplateException(e);
		}
	}

	public static String tryRender (String template, Map<String, Object> model)
	{
		try
		{
			Template t = HANDLEBARS.compileInline(template);

			return t.apply(model);
		}
		catch (IOException e)
		{
			return template;
		}
	}
}
