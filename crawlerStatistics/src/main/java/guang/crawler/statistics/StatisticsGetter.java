package guang.crawler.statistics;

import guang.crawler.statistics.connector.SiteManagerConnectHelper;

import java.io.IOException;

public class StatisticsGetter
{
	private SiteManagerConnectHelper	helper;
	private boolean	                 shutdown	= false;
	private MonitorThread	         monitorThread;
	
	public StatisticsGetter(SiteManagerConnectHelper helper)
	{
		this.helper = helper;
		
	}
	
	public boolean isShutdown()
	{
		return this.shutdown;
	}
	
	public void setMonitorThread(MonitorThread monitorThread)
	{
		this.monitorThread = monitorThread;
	}
	
	public void shutdown()
	{
		this.shutdown = true;
	}
	
	public void start()
	{
		while (!this.shutdown)
		{
			try
			{
				long[] statistics = this.helper.getStatics();
				if (statistics != null)
				{
					System.out.println("todo:\t" + statistics[0]
					        + "\tworking:\t" + statistics[1] + "\tfinished:\t"
					        + statistics[2]);
					Thread.sleep(1000);
				} else
				{
					break;
				}
				
			} catch (IOException e)
			{
				break;
			} catch (InterruptedException e)
			{
				break;
			}
			
		}
		this.shutdown = true;
		if (this.monitorThread != null)
		{
			this.monitorThread.shutdown();
		}
		System.out.println("[FINISHED] Finished get statistics.");
	}
	
}
