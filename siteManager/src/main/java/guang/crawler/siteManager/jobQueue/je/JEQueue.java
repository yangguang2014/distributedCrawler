package guang.crawler.siteManager.jobQueue.je;

import guang.crawler.siteManager.jobQueue.MapQueue;
import guang.crawler.siteManager.jobQueue.Sync;
import guang.crawler.siteManager.util.IO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

/**
 * 工作队列
 * 
 * @author yang
 */
public class JEQueue<T> extends MapQueue<T> implements Sync
{
	protected Database	                    urlsDB	 = null;
	protected Environment	                env;
	protected boolean	                    resumable;
	private final JEQueueElementTransfer<T>	transfer;
	protected final Object	                mutex	 = new Object();
	
	private boolean	                        shutdown	= false;
	
	public JEQueue(String dataHomeDir, String dbName, boolean resumable,
	        JEQueueElementTransfer<T> transfer) throws Exception
	{
		File envHome = new File(dataHomeDir);
		if (!envHome.exists())
		{
			if (!envHome.mkdir())
			{
				throw new Exception("Couldn't create this folder: "
				        + envHome.getAbsolutePath());
			}
		}
		if (!resumable)
		{
			IO.deleteFolderContents(envHome);
		}
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		envConfig.setTransactional(resumable);
		envConfig.setLocking(resumable);
		this.env = new Environment(envHome, envConfig);
		this.resumable = resumable;
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
		dbConfig.setTransactional(resumable);
		dbConfig.setDeferredWrite(!resumable);
		this.urlsDB = this.env.openDatabase(null, dbName, dbConfig);
		this.transfer = transfer;
	}
	
	@Override
	public synchronized void close()
	{
		this.shutdown = true;
		try
		{
			this.urlsDB.close();
		} catch (DatabaseException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public synchronized boolean delete(T data)
	{
		boolean success = false;
		DatabaseEntry value = new DatabaseEntry();
		this.transfer.objectToEntry(data, value);
		Transaction txn;
		if (this.resumable)
		{
			txn = this.env.beginTransaction(null, null);
		} else
		{
			txn = null;
		}
		OperationStatus status = this.urlsDB.delete(txn,
		        this.transfer.getDatabaseEntryKey(data));
		if ((status == OperationStatus.SUCCESS)
		        || (status == OperationStatus.NOTFOUND))
		{
			success = true;
		}
		if (this.resumable)
		{
			if (txn != null)
			{
				txn.commit();
			}
		}
		return success;
		
	}
	
	/**
	 * 最多获取max个元素
	 */
	@Override
	public synchronized List<T> get(int max) throws DatabaseException
	{
		synchronized (this.mutex)
		{
			int matches = 0;
			List<T> results = new ArrayList<>(max);
			
			Cursor cursor = null;
			OperationStatus result;
			DatabaseEntry key = new DatabaseEntry();
			DatabaseEntry value = new DatabaseEntry();
			Transaction txn;
			if (this.resumable)
			{
				txn = this.env.beginTransaction(null, null);
			} else
			{
				txn = null;
			}
			try
			{
				cursor = this.urlsDB.openCursor(txn, null);
				result = cursor.getFirst(key, value, null);
				
				while ((matches < max) && (result == OperationStatus.SUCCESS))
				{
					if (value.getData().length > 0)
					{
						results.add(this.transfer.entryToObject(value));
						cursor.delete();
						matches++;
					}
					result = cursor.getNext(key, value, null);
				}
			} catch (DatabaseException e)
			{
				if (txn != null)
				{
					txn.abort();
					txn = null;
				}
				throw e;
			} finally
			{
				if (cursor != null)
				{
					cursor.close();
				}
				if (txn != null)
				{
					txn.commit();
				}
			}
			return results;
		}
	}
	
	/**
	 * 获取工作队列的长度，也就是DB中存储的条目。
	 */
	@Override
	public synchronized long getLength()
	{
		if (this.shutdown)
		{
			return -1;
		}
		try
		{
			return this.urlsDB.count();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return -1;
	}
	
	@Override
	public synchronized void put(T data)
	{
		DatabaseEntry value = new DatabaseEntry();
		this.transfer.objectToEntry(data, value);
		Transaction txn;
		if (this.resumable)
		{
			txn = this.env.beginTransaction(null, null);
		} else
		{
			txn = null;
		}
		this.urlsDB.put(txn, this.transfer.getDatabaseEntryKey(data), value);
		if (this.resumable)
		{
			if (txn != null)
			{
				txn.commit();
			}
		}
	}
	
	/**
	 * 对数据进行一下同步，从而能够使数据与磁盘的同步。
	 */
	@Override
	public synchronized void sync()
	{
		if (this.resumable)
		{
			return;
		}
		if (this.urlsDB == null)
		{
			return;
		}
		try
		{
			this.urlsDB.sync();
		} catch (DatabaseException e)
		{
			e.printStackTrace();
		}
	}
	
}
