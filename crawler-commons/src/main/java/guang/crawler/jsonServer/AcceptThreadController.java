package guang.crawler.jsonServer;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AcceptThreadController
{
	public static final byte	   TYPE_SHUTDOWN_NOW	    = 0;
	public static final byte	   TYPE_SHUTDOWN_GRACEFULLY	= 1;
	public static final byte	   TYPE_START	            = 2;
	private byte	               type;
	private ReentrantReadWriteLock	lock	                = new ReentrantReadWriteLock();
	
	public AcceptThreadController()
	{
		this.type = AcceptThreadController.TYPE_START;
	}
	
	public byte getType()
	{
		this.lock.readLock().lock();
		byte result = this.type;
		this.lock.readLock().unlock();
		return result;
	}
	
	public void setType(byte type)
	{
		this.lock.writeLock().lock();
		this.type = type;
		this.lock.writeLock().unlock();
	}
	
}
