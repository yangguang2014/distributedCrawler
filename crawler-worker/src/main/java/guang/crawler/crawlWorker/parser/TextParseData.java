package guang.crawler.crawlWorker.parser;

/**
 * 如果是纯文本数据，那么解析的结果就是这种数据类型
 * 
 * @author yang
 */
public class TextParseData implements ParseData
{
	private String	textContent;

	public String getTextContent()
	{
		return this.textContent;
	}

	public void setTextContent(String textContent)
	{
		this.textContent = textContent;
	}

	@Override
	public String toString()
	{
		return this.textContent;
	}

}
