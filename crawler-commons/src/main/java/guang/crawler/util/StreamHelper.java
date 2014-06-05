package guang.crawler.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.alibaba.fastjson.JSON;

public class StreamHelper
{
	public static <T> T readObject(InputStream input, Class<T> claz)
	        throws IOException
	{
		
		ByteArrayOutputStream byteout = new ByteArrayOutputStream();
		int ch;
		while ((ch = input.read()) != -1)
		{
			if (ch == '\0')
			{
				break;
			} else
			{
				byteout.write(ch);
			}
		}
		String data = new String(byteout.toByteArray());
		try
		{
			return JSON.parseObject(data, claz);
		} catch (Exception e)
		{
			throw new IOException("parse JSON String failed", e);
		}
	}
	
	public static void writeObject(OutputStream out, Object data)
	        throws IOException
	{
		try
		{
			String result = JSON.toJSONString(data);
			out.write(result.getBytes());
			out.write('\0');
			out.flush();
		} catch (Exception e)
		{
			throw new IOException("write object failed", e);
		}
	}
}
