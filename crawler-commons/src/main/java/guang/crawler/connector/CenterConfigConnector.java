package guang.crawler.connector;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.Transaction;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class CenterConfigConnector
{
	private ZooKeeper	zookeeper;
	
	public CenterConfigConnector(String connectString) throws IOException
	{
		this.zookeeper = new ZooKeeper(connectString, 3000, null);
	}
	
	public String checkAndCreateNode(String path, CreateMode createMode,
	        byte[] data) throws InterruptedException
	{
		boolean exists = false;
		if ((createMode == CreateMode.EPHEMERAL)
		        || (createMode == CreateMode.PERSISTENT))
		{
			try
			{
				exists = this.isNodeExists(path);
			} catch (KeeperException e)
			{
				e.printStackTrace();
			}
		}
		if (!exists)
		{
			String realPath = this.createNode(path, createMode, data);
			return realPath;
		}
		return path;
	}
	
	public String createNode(String path, CreateMode createMode, byte[] data)
	        throws InterruptedException
	{
		try
		{
			String realPath = this.zookeeper.create(path, data,
			        Ids.OPEN_ACL_UNSAFE, createMode);
			return realPath;
		} catch (KeeperException e)
		{
			return null;
		}
		
	}
	
	public boolean createOrUpdate(String path, byte[] data,
	        CreateMode createMode, Transaction transaction)
	        throws InterruptedException
	{
		if (transaction == null)
		{
			boolean nodeExists = true;
			try
			{
				this.zookeeper.setData(path, data, -1);
			} catch (KeeperException e)
			{
				if (e.code() == Code.NONODE)
				{
					nodeExists = false;
				} else
				{
					e.printStackTrace();
				}
			}
			if (!nodeExists)
			{
				try
				{
					this.zookeeper.create(path, data, Ids.OPEN_ACL_UNSAFE,
					        createMode);
				} catch (KeeperException e1)
				{
					return false;
				}
			}
			return true;
		} else
		{
			transaction.delete(path, -1);
			transaction.create(path, data, Ids.OPEN_ACL_UNSAFE, createMode);
			return true;
		}
		
	}
	
	public List<String> getChildren(String path) throws InterruptedException
	{
		try
		{
			return this.zookeeper.getChildren(path, false);
		} catch (KeeperException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] getData(String path) throws InterruptedException
	{
		try
		{
			Stat exists = this.zookeeper.exists(path, false);
			if (exists != null)
			{
				byte[] data = this.zookeeper.getData(path, false, null);
				return data;
			}
			return null;
		} catch (KeeperException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean isNodeExists(String path) throws KeeperException,
	        InterruptedException
	{
		boolean nodeExists = false;
		Stat status = this.zookeeper.exists(path, null);
		nodeExists = (status != null);
		return nodeExists;
	}
	
	public void moveTo(String fromPath, String toPath) throws KeeperException,
	        InterruptedException
	{
		Transaction transaction = this.zookeeper.transaction();
		LinkedList<String> mvPath = new LinkedList<String>();
		mvPath.add("");
		while (!mvPath.isEmpty())
		{
			String first = mvPath.removeFirst();
			String path = fromPath + first;
			byte[] data = this.zookeeper.getData(path, false, null);
			transaction.create(toPath, data, Ids.OPEN_ACL_UNSAFE,
			        CreateMode.PERSISTENT);
			List<String> children = this.zookeeper.getChildren(path, false);
			if ((children != null) && (children.size() > 0))
			{
				for (String child : children)
				{
					mvPath.add(first + "/" + child);
				}
			}
		}
		this.recursiveDelete(fromPath, transaction);
		transaction.commit();
		
	}
	
	public boolean recursiveDelete(String path, Transaction transaction)
	        throws InterruptedException
	{
		
		try
		{
			List<String> children = this.zookeeper.getChildren(path, false);
			if (children.size() == 0)
			{
				this.simpleDelete(path, transaction);
				return true;
			} else
			{
				boolean success = true;
				for (String child : children)
				{
					success = this.recursiveDelete(path + "/" + child,
					        transaction);
					if (!success)
					{
						return false;
					}
				}
				this.simpleDelete(path, transaction);
				return true;
			}
		} catch (KeeperException e)
		{
			return false;
		}
	}
	
	public void shutdown() throws InterruptedException
	{
		this.zookeeper.close();
	}
	
	public boolean simpleDelete(String path, Transaction transaction)
	        throws InterruptedException
	{
		try
		{
			if (transaction != null)
			{
				transaction.delete(path, -1);
			} else
			{
				this.zookeeper.delete(path, -1);
			}
			return true;
		} catch (KeeperException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public Transaction transaction()
	{
		return this.zookeeper.transaction();
	}
	
	public void updateData(String path, byte[] data) throws KeeperException,
	        InterruptedException
	{
		this.zookeeper.setData(path, data, -1);
	}
	
	public void watchChildren(String path, Watcher watcher)
	        throws KeeperException, InterruptedException
	{
		this.zookeeper.getChildren(path, watcher);
	}
	
	public void watchNode(String path, Watcher watcher) throws KeeperException,
	        InterruptedException
	{
		this.zookeeper.exists(path, watcher);
	}
	
}
