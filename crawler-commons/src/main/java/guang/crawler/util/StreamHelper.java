package guang.crawler.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.alibaba.fastjson.JSON;

/**
 * 流操作的帮助类
 *
 * @author sun
 *
 */
public class StreamHelper {
	/**
	 * 从流中读取某个类的对象,需要注意的是,必须使用当前类中{@link #writeObject(OutputStream, Object)}
	 * 方法写入的对象才可以用当前方法读出.
	 *
	 * @param input
	 * @param claz
	 * @return
	 * @throws IOException
	 */
	public static <T> T readObject(final InputStream input, final Class<T> claz)
	        throws IOException {

		ByteArrayOutputStream byteout = new ByteArrayOutputStream();
		int ch;
		while ((ch = input.read()) != -1) {
			if (ch == '\0') {
				break;
			} else {
				byteout.write(ch);
			}
		}
		String data = new String(byteout.toByteArray());
		try {
			return JSON.parseObject(data, claz);
		} catch (Exception e) {
			throw new IOException("parse JSON String failed", e);
		}
	}

	/**
	 * 向流中写入某个对象,写入的对象只能用当前类中的{@link #readObject(InputStream, Class)}方法读出.
	 *
	 * @param out
	 * @param data
	 * @throws IOException
	 */
	public static void writeObject(final OutputStream out, final Object data)
	        throws IOException {
		try {
			String result = JSON.toJSONString(data);
			out.write(result.getBytes());
			out.write('\0');
			out.flush();
		} catch (Exception e) {
			throw new IOException("write object failed", e);
		}
	}
}
