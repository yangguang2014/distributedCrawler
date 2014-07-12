package guang.crawler.siteManager.util;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 用来处理一些IO操作
 *
 * @author yang
 *
 */
public class IOHelper {
	/**
	 * 删除某个目录
	 *
	 * @param folder
	 * @return
	 */
	public static boolean deleteFolder(final File folder) {
		return IOHelper.deleteFolderContents(folder) && folder.delete();
	}

	/**
	 * 删除目录的内容,但是目录自身并不删除
	 *
	 * @param folder
	 * @return
	 */
	public static boolean deleteFolderContents(final File folder) {
		System.out.println("Deleting content of: " + folder.getAbsolutePath());
		File[] files = folder.listFiles();
		if (files == null) {
			return true;
		}
		for (File file : files) {
			if (file.isFile()) {
				if (!file.delete()) {
					return false;
				}
			} else {
				if (!IOHelper.deleteFolder(file)) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 将字节数组存储到某个文件中.
	 * 
	 * @param bytes
	 * @param destination
	 */
	public static void writeBytesToFile(final byte[] bytes,
	        final String destination) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(
			        destination);
			FileChannel fc = fileOutputStream.getChannel();
			fc.write(ByteBuffer.wrap(bytes));
			fileOutputStream.close();
			fc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
