package guang.crawler.crawlWorker.plugins;

import guang.crawler.core.WebURL;
import guang.crawler.crawlWorker.fetcher.Page;
import guang.crawler.crawlWorker.parser.HtmlParseData;
import guang.crawler.crawlWorker.parser.TextParseData;
import guang.crawler.crawlWorker.util.SiteManagerConnectorManager;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class ExtractOutGoingUrlsPlugin implements DownloadPlugin
{
	private SiteManagerConnectorManager	siteManagerConnectHelper;
	private Pattern	                 filter	= Pattern
	                                                .compile(".*(\\.(css|js|bmp|gif|jpe?g"
	                                                        + "|png|tiff?|mid|mp2|mp3|mp4"
	                                                        + "|wav|avi|mov|mpeg|ram|m4v|pdf"
	                                                        + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	
	public ExtractOutGoingUrlsPlugin()
	{
	}
	
	public ExtractOutGoingUrlsPlugin(
	        SiteManagerConnectorManager siteManagerConnectHelper)
	{
		this.siteManagerConnectHelper = siteManagerConnectHelper;
	}
	
	public void setSiteManagerConnector(
	        SiteManagerConnectorManager siteManagerConnectHelper)
	{
		this.siteManagerConnectHelper = siteManagerConnectHelper;
	}
	
	@Override
	public boolean work(Page page)
	{
		if (page != null)
		{
			if (page.getParseData() instanceof HtmlParseData)
			{
				HtmlParseData htmlParseData = (HtmlParseData) page
				        .getParseData();
				try
				{
					List<WebURL> urlLists = htmlParseData.getOutgoingUrls();
					Iterator<WebURL> it = urlLists.iterator();
					while (it.hasNext())
					{
						if (this.filter.matcher(it.next().getURL()).matches())
						{
							it.remove();
						}
					}
					this.siteManagerConnectHelper.putData(page.getWebURL(),
					        urlLists);
					return true;
				} catch (IOException e)
				{
					return false;
				}
			} else if (page.getParseData() instanceof TextParseData)
			{
				try
				{
					this.siteManagerConnectHelper.putData(page.getWebURL(),
					        null);
					return true;
				} catch (IOException e)
				{
					return false;
				}
			}
		}
		return false;
	}
	
}
