package guang.crawler.centerConfig;

import guang.crawler.connector.CenterConfigConnector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Transaction;
import org.apache.zookeeper.Watcher;

public abstract class CenterConfigElement
{
	protected final String	              path;
	protected final CenterConfigConnector	connector;
	private Properties	                  values;
	private static final String	          PATH_LOCK	         = "_lock";
	private static final String	          KEY_NOTIFY_CHANGED	= "notify.changed";
	
	public CenterConfigElement(String path, CenterConfigConnector connector)
	{
		this.path = path;
		this.connector = connector;
		this.values = new Properties();
	}
	
	public boolean delete(Transaction transaction) throws InterruptedException
	{
		return this.connector.recursiveDelete(this.path, transaction);
	}
	
	public void deleteProperty(String key, boolean refreshNow)
	        throws InterruptedException, IOException, KeeperException
	{
		this.values.remove(key);
		if (refreshNow)
		{
			this.update();
		}
	}
	
	public boolean exists() throws KeeperException, InterruptedException
	{
		return this.connector.isNodeExists(this.path);
	}
	
	public String getPath()
	{
		return this.path;
	}
	
	public Properties getProperties()
	{
		return this.values;
	}
	
	public String getProperty(String key)
	{
		return this.values.getProperty(key);
	}
	
	public boolean load() throws InterruptedException, IOException
	{
		byte[] data = this.connector.getData(this.path);
		if (data != null)
		{
			this.values.load(new ByteArrayInputStream(data));
		}
		return true;
	}
	
	public boolean lock()
	{
		// TODO 这里应当仔细的检查该锁是否已经被当前线程获取了。
		try
		{
			String realPath = this.connector.createNode(this.path
			        + CenterConfigElement.PATH_LOCK, CreateMode.EPHEMERAL, Long
			        .toString(Thread.currentThread().getId()).getBytes());
			if (realPath == null)
			{
				return false;
			} else
			{
				return true;
			}
			
		} catch (InterruptedException e)
		{
			return false;
		}
	}
	
	public void notifyChanged() throws InterruptedException, IOException,
	        KeeperException
	{
		this.setProperty(CenterConfigElement.KEY_NOTIFY_CHANGED,
		        new Date().toString(), true);
	}
	
	public void setProperty(String key, String value, boolean refreshNow)
	        throws InterruptedException, IOException, KeeperException
	{
		this.values.put(key, value);
		if (refreshNow)
		{
			this.update();
		}
		
	}
	
	public boolean unlock()
	{
		try
		{
			return this.connector.simpleDelete(this.path
			        + CenterConfigElement.PATH_LOCK, null);
		} catch (InterruptedException e)
		{
			return false;
		}
	}
	
	public boolean update() throws InterruptedException, IOException,
	        KeeperException
	{
		if (!this.exists())
		{
			return false;
		}
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		this.values.store(byteOut, "update at " + new Date().toString());
		byte[] data = byteOut.toByteArray();
		this.connector.updateData(this.path, data);
		return true;
	}
	
	public void watchChildren(Watcher watcher) throws KeeperException,
	        InterruptedException
	{
		this.connector.watchChildren(this.path, watcher);
	}
	
	public void watchNode(Watcher watcher) throws KeeperException,
	        InterruptedException
	{
		this.connector.watchNode(this.path, watcher);
	}
}
