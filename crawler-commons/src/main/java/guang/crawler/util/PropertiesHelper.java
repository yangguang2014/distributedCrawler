package guang.crawler.util;

import java.util.Properties;

public class PropertiesHelper
{
	public static int readInt(Properties properties, String key,
	        int defaultValue)
	{
		if (properties == null)
		{
			return defaultValue;
		}
		String result = properties.getProperty(key);
		if (result == null)
		{
			return defaultValue;
		}
		try
		{
			int resultInt = Integer.parseInt(result);
			return resultInt;
		} catch (NumberFormatException e)
		{
			return defaultValue;
		}
		
	}
	
	public static long readLong(Properties properties, String key,
	        long defaultValue)
	{
		if (properties == null)
		{
			return defaultValue;
		}
		String result = properties.getProperty(key);
		if (result == null)
		{
			return defaultValue;
		}
		try
		{
			long resultLong = Long.parseLong(result);
			return resultLong;
		} catch (NumberFormatException e)
		{
			return defaultValue;
		}
		
	}
	
	public static String readString(Properties properties, String key,
	        String defaultValue)
	{
		if (properties == null)
		{
			return defaultValue;
		}
		String result = properties.getProperty(key);
		return result == null ? defaultValue : result;
	}
}
