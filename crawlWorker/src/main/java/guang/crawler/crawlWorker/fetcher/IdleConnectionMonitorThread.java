package guang.crawler.crawlWorker.fetcher;

import java.util.concurrent.TimeUnit;

import org.apache.http.impl.conn.PoolingClientConnectionManager;

/**
 * 这个类用来时不时的关闭已经过期的连接和超过30秒未被使用的连接。
 * 
 * @author yang
 */
public class IdleConnectionMonitorThread extends Thread
{

	private final PoolingClientConnectionManager	connMgr;
	private volatile boolean						shutdown;

	public IdleConnectionMonitorThread(PoolingClientConnectionManager connMgr)
	{
		super("Connection Manager");
		this.connMgr = connMgr;
	}

	@Override
	public void run()
	{
		try
		{
			while (!this.shutdown)
			{
				synchronized (this)
				{
					this.wait(5000);
					// Close expired connections
					this.connMgr.closeExpiredConnections();
					// Optionally, close connections
					// that have been idle longer than 30 sec
					this.connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
				}
			}
		} catch (InterruptedException ex)
		{
			// terminate
		}
	}

	public void shutdown()
	{
		this.shutdown = true;
		synchronized (this)
		{
			this.notifyAll();
		}
	}

}
