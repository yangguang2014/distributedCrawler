package guang.crawler.crawlWorker.parser;

/**
 * 抽取链接时，获得的链接的锚点信息和链接对。有时候需要这些信息的。
 * 
 * @author yang
 */
public class ExtractedUrlAnchorPair
{

	private String	href;
	private String	anchor;

	public String getAnchor()
	{
		return this.anchor;
	}

	public String getHref()
	{
		return this.href;
	}

	public void setAnchor(String anchor)
	{
		this.anchor = anchor;
	}

	public void setHref(String href)
	{
		this.href = href;
	}

}
