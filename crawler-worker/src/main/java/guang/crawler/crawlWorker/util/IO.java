package guang.crawler.crawlWorker.util;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 简单的文件操作
 * 
 * @author yang
 */
public class IO
{

	public static boolean deleteFolder(File folder)
	{
		return IO.deleteFolderContents(folder) && folder.delete();
	}

	public static boolean deleteFolderContents(File folder)
	{
		System.out.println("Deleting content of: " + folder.getAbsolutePath());
		File[] files = folder.listFiles();
		for (File file : files)
		{
			if (file.isFile())
			{
				if (!file.delete())
				{
					return false;
				}
			} else
			{
				if (!IO.deleteFolder(file))
				{
					return false;
				}
			}
		}
		return true;
	}

	public static void writeBytesToFile(byte[] bytes, String destination)
	{
		try
		{
			FileOutputStream fileOutputStream = new FileOutputStream(
					destination);
			FileChannel fc = fileOutputStream.getChannel();
			fc.write(ByteBuffer.wrap(bytes));
			fileOutputStream.close();
			fc.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
