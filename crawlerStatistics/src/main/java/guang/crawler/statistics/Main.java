package guang.crawler.statistics;

import guang.crawler.statistics.connector.SiteManagerConnectHelper;

import java.io.IOException;

public class Main
{
	public static void main(String[] args)
	{
		
		MonitorThread monitorThread = null;
		try
		{
			SiteManagerConnectHelper helper = new SiteManagerConnectHelper();
			StatisticsGetter getter = new StatisticsGetter(helper);
			monitorThread = new MonitorThread(helper, getter);
			monitorThread.start();
			getter.start();
		} catch (IOException e)
		{
			System.out.println("[FAILED] Failed to connect site manager!");
			return;
		}
		
	}
	
}
