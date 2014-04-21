package guang.crawler.crawlWorker;

import guang.crawler.connector.WebDataTableConnector;
import guang.crawler.core.WebURL;
import guang.crawler.crawlWorker.fetcher.WebGeter;
import guang.crawler.crawlWorker.plugins.ExtractOutGoingUrlsPlugin;
import guang.crawler.crawlWorker.plugins.SaveToHbasePlugin;
import guang.crawler.crawlWorker.util.SiteManagerConnectHelper;

import java.io.IOException;
import java.util.LinkedList;

public class Main
{
	public static void main(String[] args)
	{
		SiteManagerConnectHelper siteManagerConnectHelper = null;
		try
		{
			siteManagerConnectHelper = new SiteManagerConnectHelper();
		} catch (IOException e)
		{
			System.out.println("Can not connect to site manager");
			return;
		}
		WebGeter wget = new WebGeter();
		ExtractOutGoingUrlsPlugin extractOutGoingUrlsPlugin = new ExtractOutGoingUrlsPlugin();
		extractOutGoingUrlsPlugin
		        .setSiteManagerConnector(siteManagerConnectHelper);
		
		WebDataTableConnector webDataTableConnector = new WebDataTableConnector(
		        "ubuntu-3,ubuntu-2,ubuntu-6,ubuntu-7,ubuntu-8", "2181");
		try
		{
			webDataTableConnector.open();
		} catch (IOException e)
		{
			System.out.println("Can not open hbase connect");
		}
		SaveToHbasePlugin saveToHbasePlugin = new SaveToHbasePlugin(
		        webDataTableConnector);
		wget.addPlugin(extractOutGoingUrlsPlugin);
		wget.addPlugin(saveToHbasePlugin);
		for (int i = 0; i < 100000; i++)
		{
			LinkedList<WebURL> urls = null;
			try
			{
				
				urls = siteManagerConnectHelper.getURLs(1);
			} catch (IOException ex)
			{
				ex.printStackTrace();
				break;
			}
			if (urls != null)
			{
				for (WebURL url : urls)
				{
					wget.processUrl(url);
				}
			} else
			{
				try
				{
					Thread.sleep(10000);
				} catch (InterruptedException e)
				{
					// Nothing to do.
				}
			}
			
		}
		try
		{
			siteManagerConnectHelper.exit();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		wget.shutdown();
	}
}
