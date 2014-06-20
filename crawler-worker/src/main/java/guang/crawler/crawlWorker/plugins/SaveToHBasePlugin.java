package guang.crawler.crawlWorker.plugins;

import guang.crawler.commons.WebURL;
import guang.crawler.connector.WebDataTableConnector;
import guang.crawler.crawlWorker.fetcher.Page;
import guang.crawler.crawlWorker.parser.HtmlParseData;
import guang.crawler.crawlWorker.parser.TextParseData;

import java.io.IOException;

public class SaveToHBasePlugin implements DownloadPlugin
{
	private WebDataTableConnector	webDataTableConnector;
	
	public SaveToHBasePlugin(WebDataTableConnector webDataTableConnector)
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
		}else if(page.getParseData() instanceof TextParseData){
			TextParseData textParseData=(TextParseData)page.getParseData();
			WebURL webURL = page.getWebURL();
			String text=textParseData.getTextContent();
			try
			{
				this.webDataTableConnector.addHtmlData(webURL, text, false);
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
