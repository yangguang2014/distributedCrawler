package guang.crawler.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesHelper
{
	public static boolean loadConfigFile(File file, Properties properties)
	{
		if (file.exists())
		{
			try
			{
				properties.load(new FileInputStream(file));
				return true;
			} catch (IOException e)
			{
				System.err.println("Error load site-manager config file");
				return false;
			}
		}
		return true;
	}
	
	public static boolean readBoolean(Properties properties, String key,
			boolean defaultValue)
	{
		if (properties == null)
		{
			return defaultValue;
		}
		String result = properties.getProperty(key).toUpperCase();
		if (result == null)
		{
			return defaultValue;
		}
		if (result.equals("YES") || result.equals("TRUE"))
		{
			return true;
		} else
		{
			return false;
		}

	}
	
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
