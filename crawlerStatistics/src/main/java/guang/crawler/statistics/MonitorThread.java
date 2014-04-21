package guang.crawler.statistics;

import guang.crawler.statistics.connector.SiteManagerConnectHelper;

import java.io.IOException;
import java.io.InputStream;

public class MonitorThread extends Thread
{
	private SiteManagerConnectHelper	helper;
	private StatisticsGetter	     getter;
	private final InputStream	     input;
	
	public MonitorThread(SiteManagerConnectHelper helper,
	        StatisticsGetter getter)
	{
		this.helper = helper;
		this.getter = getter;
		getter.setMonitorThread(this);
		this.input = System.in;
	}
	
	@Override
	public void run()
	{
		
		try
		{
			while (true)
			{
				if (System.in.available() > 0)
				{
					char cmd = (char) this.input.read();
					if ('q' == cmd)
					{
						System.out.println("[INFO] System will exit!");
						try
						{
							this.getter.shutdown();
							this.helper.exit();
						} catch (IOException e)
						{
							// Nothing to do.
						}
						this.input.close();
						System.out.println("[INFO] System exited!");
						break;
					}
				} else
				{
					try
					{
						Thread.sleep(1000);
					} catch (InterruptedException e)
					{
						break;
					}
				}
				
			}
		} catch (IOException e)
		{
			// Nothing to do.
		}
		
	}
	
	public void shutdown()
	{
		this.interrupt();
		
	}
}
