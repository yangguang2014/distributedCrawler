package guang.crawler.siteManager.jobQueue;

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
 * 基于JE(Berkeley DB Java Edition)构建的MapQueue.
 *
 * @author yang
 */
public class JEQueue<T> extends MapQueue<T> implements Sync {
	/**
	 * 存放URL的Database
	 */
	protected Database	                    urlsDB	 = null;
	/**
	 * Berkeley DB的配置环境
	 */
	protected Environment	                env;
	/**
	 * 是否是可以重复使用的数据库.如果是,那么程序退出后不删除数据文件,否则就清除所有的数据.类似于临时文件的deleteOnExit方式.
	 */
	protected boolean	                    resumable;
	/**
	 * 实现T类型数据的转换器,将T类型的数据转换成Berkeledy DB中的数据
	 */
	private final JEQueueElementTransfer<T>	transfer;
	/**
	 * 当前队列是否被关闭了.
	 */
	private boolean	                        shutdown	= false;
	
	/**
	 * 在指定目录下创建JEQueue
	 *
	 * @param envHome
	 *            JEQueue的本地文件目录
	 * @param dbName
	 *            数据库的名称
	 * @param resumable
	 *            数据库是否是可以被重复利用的;如果可以重复利用,那么在系统退出后并不删除数据文件,否则,所有的数据文件将被删除.
	 * @param transfer
	 *            T类型的数据对象转换成Berkeley DB中存放的数据类型.
	 * @throws Exception
	 */
	public JEQueue(final File envHome, final String dbName,
	        final boolean resumable, final JEQueueElementTransfer<T> transfer)
	        throws Exception {
		// 每个不同的siteManager都有其自身的工作目录
		
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
	public synchronized void close() {
		this.shutdown = true;
		try {
			this.urlsDB.close();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public synchronized boolean delete(final T data) {
		boolean success = false;
		DatabaseEntry value = new DatabaseEntry();
		this.transfer.objectToEntry(data, value);
		Transaction txn;
		if (this.resumable) {
			txn = this.env.beginTransaction(null, null);
		} else {
			txn = null;
		}
		OperationStatus status = this.urlsDB.delete(txn,
		                                            this.transfer.getDatabaseEntryKey(data));
		if ((status == OperationStatus.SUCCESS)
		        || (status == OperationStatus.NOTFOUND)) {
			success = true;
		}
		if (this.resumable) {
			if (txn != null) {
				txn.commit();
			}
		}
		return success;
		
	}
	
	/**
	 * 最多获取max个元素
	 */
	@Override
	public synchronized List<T> get(final int max) throws DatabaseException {
		
		int matches = 0;
		List<T> results = new ArrayList<T>(max);
		
		Cursor cursor = null;
		OperationStatus result;
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry value = new DatabaseEntry();
		Transaction txn;
		if (this.resumable) {
			txn = this.env.beginTransaction(null, null);
		} else {
			txn = null;
		}
		try {
			cursor = this.urlsDB.openCursor(txn, null);
			result = cursor.getFirst(key, value, null);
			
			while ((matches < max) && (result == OperationStatus.SUCCESS)) {
				if (value.getData().length > 0) {
					results.add(this.transfer.entryToObject(value));
					cursor.delete();
					matches++;
				}
				result = cursor.getNext(key, value, null);
			}
		} catch (DatabaseException e) {
			if (txn != null) {
				txn.abort();
				txn = null;
			}
			throw e;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (txn != null) {
				txn.commit();
			}
		}
		return results;
		
	}
	
	/**
	 * 获取工作队列的长度，也就是DB中存储的条目。
	 */
	@Override
	public synchronized long getLength() {
		if (this.shutdown) {
			return -1;
		}
		try {
			return this.urlsDB.count();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	@Override
	public MapQueueIterator<T> iterator() {
		Cursor cursor = null;
		Transaction txn;
		if (this.resumable) {
			txn = this.env.beginTransaction(null, null);
		} else {
			txn = null;
		}
		cursor = this.urlsDB.openCursor(txn, null);
		return new JECursorIterator<T>(cursor, this.transfer);
		
	}
	
	@Override
	public synchronized void put(final T data) {
		DatabaseEntry value = new DatabaseEntry();
		this.transfer.objectToEntry(data, value);
		Transaction txn;
		if (this.resumable) {
			txn = this.env.beginTransaction(null, null);
		} else {
			txn = null;
		}
		this.urlsDB.put(txn, this.transfer.getDatabaseEntryKey(data), value);
		if (this.resumable) {
			if (txn != null) {
				txn.commit();
			}
		}
	}
	
	/**
	 * 对数据进行一下同步，从而能够使数据与磁盘的同步。
	 */
	@Override
	public synchronized void sync() {
		if (this.resumable) {
			return;
		}
		if (this.urlsDB == null) {
			return;
		}
		try {
			this.urlsDB.sync();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
	
}
