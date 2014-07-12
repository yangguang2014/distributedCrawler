package guang.crawler.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 属性的帮助类
 * 
 * @author sun
 *
 */
public class PropertiesHelper {
	/**
	 * 从属性配置文件中读取属性
	 * 
	 * @param file
	 * @param properties
	 * @return
	 */
	public static boolean loadConfigFile(final File file,
	        final Properties properties) {
		if (file.exists()) {
			try {
				properties.load(new FileInputStream(file));
				return true;
			} catch (IOException e) {
				System.err.println("Error load site-manager config file");
				return false;
			}
		}
		return true;
	}

	/**
	 * 读取boolean属性值
	 * 
	 * @param properties
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static boolean readBoolean(final Properties properties,
	        final String key, final boolean defaultValue) {
		if (properties == null) {
			return defaultValue;
		}
		String result = properties.getProperty(key)
		                          .toUpperCase();
		if (result == null) {
			return defaultValue;
		}
		if (result.equals("YES") || result.equals("TRUE")) {
			return true;
		} else {
			return false;
		}
		
	}

	/**
	 * 读取int属性值
	 * 
	 * @param properties
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static int readInt(final Properties properties, final String key,
	        final int defaultValue) {
		if (properties == null) {
			return defaultValue;
		}
		String result = properties.getProperty(key);
		if (result == null) {
			return defaultValue;
		}
		try {
			int resultInt = Integer.parseInt(result);
			return resultInt;
		} catch (NumberFormatException e) {
			return defaultValue;
		}
		
	}
	
	/**
	 * 读取long类型属性值
	 * 
	 * @param properties
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static long readLong(final Properties properties, final String key,
	        final long defaultValue) {
		if (properties == null) {
			return defaultValue;
		}
		String result = properties.getProperty(key);
		if (result == null) {
			return defaultValue;
		}
		try {
			long resultLong = Long.parseLong(result);
			return resultLong;
		} catch (NumberFormatException e) {
			return defaultValue;
		}
		
	}

	/**
	 * 读取String类型属性值.
	 * 
	 * @param properties
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String readString(final Properties properties,
	        final String key, final String defaultValue) {
		if (properties == null) {
			return defaultValue;
		}
		String result = properties.getProperty(key);
		return result == null ? defaultValue : result;
	}
}
