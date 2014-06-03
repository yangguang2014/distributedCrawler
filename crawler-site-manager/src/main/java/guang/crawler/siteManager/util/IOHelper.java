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
public class IOHelper
{
	
	public static boolean deleteFolder(File folder)
	{
		return IOHelper.deleteFolderContents(folder) && folder.delete();
	}
	
	public static boolean deleteFolderContents(File folder)
	{
		System.out.println("Deleting content of: " + folder.getAbsolutePath());
		File[] files = folder.listFiles();
		if (files == null)
		{
			return true;
		}
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
				if (!IOHelper.deleteFolder(file))
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
