package guang.crawler.crawlWorker.parser;

import guang.crawler.core.WebURL;

import java.util.List;

/**
 * 如果解析得到的数据类型是HTML，那么使用这种数据类型
 * 
 * @author yang
 */
public class HtmlParseData implements ParseData
{
	private String	     html;
	private String	     title;
	private String	     text;
	private List<WebURL>	outgoingUrls;
	
	public String getHtml()
	{
		return this.html;
	}
	
	public List<WebURL> getOutgoingUrls()
	{
		return this.outgoingUrls;
	}
	
	public String getText()
	{
		return this.text;
	}
	
	public String getTitle()
	{
		return this.title;
	}
	
	public void setHtml(String html)
	{
		this.html = html;
	}
	
	public void setOutgoingUrls(List<WebURL> outgoingUrls)
	{
		this.outgoingUrls = outgoingUrls;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	@Override
	public String toString()
	{
		return this.html;
	}
	
}
