package guang.crawler.crawlWorker.plugins;

import guang.crawler.connector.WebDataTableConnector;
import guang.crawler.core.WebURL;
import guang.crawler.crawlWorker.fetcher.Page;
import guang.crawler.crawlWorker.parser.HtmlParseData;

import java.io.IOException;

public class SaveToHbasePlugin implements DownloadPlugin
{
	private WebDataTableConnector	webDataTableConnector;
	
	public SaveToHbasePlugin(WebDataTableConnector webDataTableConnector)
	{
		this.webDataTableConnector = webDataTableConnector;
	}
	
	@Override
	public boolean work(Page page)
	{
		if (page.getParseData() instanceof HtmlParseData)
		{
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String html = htmlParseData.getHtml();
			WebURL webURL = page.getWebURL();
			try
			{
				this.webDataTableConnector.addHtmlData(webURL, html, false);
				System.out.println("[OK] save url success:" + webURL.getURL());
				return true;
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}
	
}
