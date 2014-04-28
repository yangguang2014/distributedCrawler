package guang.crawler.controller;

import guang.crawler.connector.ZookeeperConnector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Transaction;

public abstract class ZookeeperElement
{
	protected final String	           path;
	protected final ZookeeperConnector	connector;
	private HashMap<String, String>	   values;
	private static final String	       PATH_LOCK	= "/lock";
	
	public ZookeeperElement(String path, ZookeeperConnector connector)
	{
		this.path = path;
		this.connector = connector;
		this.values = new HashMap<>();
	}
	
	public boolean delete(Transaction transaction) throws InterruptedException
	{
		return this.connector.recursiveDelete(this.path, transaction);
	}
	
	public String get(String key) throws InterruptedException
	{
		if (!this.values.containsKey(key))
		{
			this.load(key);
		}
		return this.values.get(key);
		
	}
	
	public String getPath()
	{
		return this.path;
	}
	
	public boolean load() throws InterruptedException
	{
		List<String> children = this.connector.getChildren(this.path);
		if ((children != null) && (children.size() > 0))
		{
			for (String child : children)
			{
				this.load(child);
			}
		}
		return true;
	}
	
	public boolean load(String key) throws InterruptedException
	{
		if (!this.values.containsKey(key))
		{
			byte[] data = this.connector.getData(this.path + "/" + key);
			if (data != null)
			{
				this.values.put(key, new String(data));
			}
		}
		return true;
		
	}
	
	public boolean lock()
	{
		// TODO 这里应当仔细的检查该锁是否已经被当前线程获取了。
		try
		{
			String realPath = this.connector.createNode(this.path
			        + ZookeeperElement.PATH_LOCK, CreateMode.EPHEMERAL, Long
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
	
	public void put(String key, String value, boolean refreshNow)
	        throws InterruptedException
	{
		this.values.put(key, value);
		if (refreshNow)
		{
			this.update(key, null);
		}
		
	}
	
	public boolean unlock()
	{
		try
		{
			return this.connector.simpleDelete(this.path
			        + ZookeeperElement.PATH_LOCK, null);
		} catch (InterruptedException e)
		{
			return false;
		}
	}
	
	public boolean update(String key, Transaction transaction)
	        throws InterruptedException
	{
		String value = this.values.get(key);
		boolean success = false;
		if ("".equals(value))
		{
			success = this.connector.simpleDelete(this.path + "/" + key,
			        transaction);
		} else
		{
			success = this.connector.createOrUpdate(this.path + "/" + key,
			        value.getBytes(), CreateMode.PERSISTENT, transaction);
		}
		
		return success;
	}
	
	public boolean update(Transaction transaction) throws InterruptedException
	{
		Iterator<String> keys = this.values.keySet().iterator();
		while (keys.hasNext())
		{
			String key = keys.next();
			boolean success = this.update(key, transaction);
			if (!success)
			{
				return false;
			}
		}
		return true;
	}
}
