package guang.crawler.siteManager.daemon;

import guang.crawler.commons.WebURL;
import guang.crawler.siteManager.SiteConfig;
import guang.crawler.siteManager.SiteManager;
import guang.crawler.siteManager.jobQueue.MapQueue;
import guang.crawler.siteManager.jobQueue.MapQueueIterator;
import guang.crawler.siteManager.urlFilter.ObjectFilter;

import java.io.EOFException;
import java.io.IOException;
import java.net.URI;
import java.util.TimerTask;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.alibaba.fastjson.JSON;

public class SiteBackupDaemon extends TimerTask
{
	
	public static SiteBackupDaemon newDaemon()
	{
		return new SiteBackupDaemon();
	}
	
	private FileSystem	fileSystem;
	
	private SiteBackupDaemon()
	{
	}
	
	private void backupFilter(ObjectFilter filter, String path)
	        throws IOException
	{
		FSDataOutputStream fsout = null;
		try
		{
			fsout = this.fileSystem.create(new Path(path));
			fsout.writeUTF(filter.toBackupString());
			
		} finally
		{
			if (fsout != null)
			{
				fsout.close();
			}
		}
		
	}
	
	private void backupList(MapQueue<WebURL> listToBackup, String path)
	        throws IOException
	{
		MapQueueIterator<WebURL> iteraor = listToBackup.iterator();
		FSDataOutputStream fsout = null;
		try
		{
			fsout = this.fileSystem.create(new Path(path));
			while (iteraor.hasNext())
			{
				WebURL url = iteraor.next();
				fsout.writeUTF(JSON.toJSONString(url));
			}
		} finally
		{
			if (fsout != null)
			{
				fsout.close();
			}
			iteraor.close();
		}
		
	}
	
	public void forceBackup()
	{
		this.run();
	}
	
	public SiteBackupDaemon init() throws IOException
	{
		Configuration configuration = new Configuration();
		this.fileSystem = FileSystem.get(
		        URI.create(SiteConfig.me().getHadoopURL()), configuration);
		return this;
	}
	
	/**
	 * 加载备份的数据
	 * 
	 * @throws IOException
	 */
	public boolean loadBackupData() throws IOException
	{
		SiteConfig config = SiteConfig.me();
		String rootDir = config.getHadoopPath() + "/"
		        + config.getSiteManagerInfo().getSiteToHandle() + "/backup";
		Path maxVersionFilePath = new Path(rootDir + "/max-version");
		boolean exists;
		try
		{
			exists = this.fileSystem.exists(maxVersionFilePath);
		} catch (IOException e)
		{
			return false;
		}
		if (exists)
		{
			int version = -1;
			FSDataInputStream fsin = this.fileSystem.open(maxVersionFilePath);
			try
			{
				version = fsin.readInt();
			} finally
			{
				fsin.close();
			}
			String backDir = rootDir + "/" + version;
			SiteManager siteManager = SiteManager.me();
			this.readBackupList(siteManager.getToDoTaskList(), backDir
			        + "/todoList");
			this.readBackupList(siteManager.getWorkingTaskList(), backDir
			        + "/workingList");
			this.readBackupList(siteManager.getFailedTaskList(), backDir
			        + "/failedList");
			this.readFilter(siteManager.getUrlsFilter(), backDir + "/filter");
			config.setBackupVersion(version);
			return true;
		} else
		{
			return false;
		}
	}
	
	private void readBackupList(MapQueue<WebURL> list, String backupFilePath)
	        throws IOException
	{
		FSDataInputStream fsin = this.fileSystem.open(new Path(backupFilePath));
		try
		{
			while (true)
			{
				try
				{
					String urlJSON = fsin.readUTF();
					WebURL weburl = JSON.parseObject(urlJSON, WebURL.class);
					list.put(weburl);
				} catch (EOFException e)
				{
					break;
					
				}
			}
			
		} finally
		{
			fsin.close();
		}
		
	}
	
	private void readFilter(ObjectFilter filter, String backupFilePath)
	        throws IOException
	{
		FSDataInputStream fsin = this.fileSystem.open(new Path(backupFilePath));
		try
		{
			
			try
			{
				String filterData = fsin.readUTF();
				filter.fromBackupString(filterData);
			} catch (EOFException e)
			{
				return;
				
			}
			
		} finally
		{
			fsin.close();
		}
		
	}
	
	public void rescheduleTaskList(MapQueue<WebURL> fromList)
	{
		if (fromList.getLength() > 0)
		{
			MapQueueIterator<WebURL> iterator = fromList.iterator();
			try
			{
				while (iterator.hasNext())
				{
					SiteManager.me().getToDoTaskList()
					        .put(iterator.next().resetTryTime());
				}
			} finally
			{
				iterator.close();
			}
			
		}
	}
	
	public void clearBackups(){
		SiteConfig config = SiteConfig.me();
		// 首先设置系统为backup time，从而让用户不再获取新的任务，也暂停清理线程的工作
		config.setBackTime(true);
		// 找到备份的目录
		String rootDir = config.getHadoopPath() + "/"
		        + config.getSiteManagerInfo().getSiteToHandle() + "/backup";
		try
		{
			Path path=new Path(rootDir);
			if(this.fileSystem.exists(path))
			this.fileSystem.delete(path,true);
		} catch (IOException e)
		{
			return;
		} catch (IllegalArgumentException e)
		{
			return;
		}
		config.setBackTime(false);
	}
	
	@Override
	public void run()
	{
		SiteConfig config = SiteConfig.me();
		// 首先设置系统为backup time，从而让用户不再获取新的任务，也暂停清理线程的工作
		config.setBackTime(true);
		// 找到备份的目录
		String rootDir = config.getHadoopPath() + "/"
		        + config.getSiteManagerInfo().getSiteToHandle() + "/backup";
		try
		{
			this.fileSystem.mkdirs(new Path(rootDir));
		} catch (IOException e)
		{
			return;
		} catch (IllegalArgumentException e)
		{
			return;
		}
		// 找到当前最大的备份版本号
		Path maxVersionFilePath = new Path(rootDir + "/max-version");
		boolean exists;
		try
		{
			exists = this.fileSystem.exists(maxVersionFilePath);
		} catch (IOException e)
		{
			return;
		}
		int maxVersion = config.getBackupVersion();
		if (exists)
		{
			FSDataInputStream fsin = null;
			try
			{
				fsin = this.fileSystem.open(maxVersionFilePath);
				int version = fsin.readInt();
				maxVersion = maxVersion < version ? version : maxVersion;
				maxVersion++;
			} catch (IOException e)
			{
				return;
			} finally
			{
				if (fsin != null)
				{
					try
					{
						fsin.close();
					} catch (IOException e)
					{
					}
				}
			}
		}
		
		String backDir = rootDir + "/" + maxVersion;
		Path backDirPath = new Path(backDir);
		try
		{
			if (this.fileSystem.exists(backDirPath))
			{
				this.fileSystem.delete(backDirPath, true);
			}
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try
		{
			this.fileSystem.mkdirs(backDirPath);
		} catch (IllegalArgumentException e)
		{
			return;
		} catch (IOException e)
		{
			return;
		}
		// 备份三个队列的数据
		SiteManager siteManager = SiteManager.me();
		try
		{
			this.backupList(siteManager.getToDoTaskList(), backDir
			        + "/todoList");
			this.backupList(siteManager.getWorkingTaskList(), backDir
			        + "/workingList");
			this.backupList(siteManager.getFailedTaskList(), backDir
			        + "/failedList");
			this.backupFilter(siteManager.getUrlsFilter(), backDir + "/filter");
		} catch (IOException e)
		{
			return;
		}
		try
		{
			FSDataOutputStream fsout = this.fileSystem.create(
			        maxVersionFilePath, true);
			try
			{
				fsout.writeInt(maxVersion);
			} finally
			{
				fsout.close();
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 到此为止，备份结束了
		config.setBackupVersion(maxVersion);
		config.setBackTime(false);
	}
}
