package guang.crawler.crawlWorker.parser;

/**
 * 抽取链接时，获得的链接的锚点信息和链接对。有时候需要这些信息的。
 *
 * @author yang
 */
public class ExtractedUrlAnchorPair {
	/**
	 * 连接URL
	 */
	private String	href;
	/**
	 * 锚点字符串
	 */
	private String	anchor;
	
	public String getAnchor() {
		return this.anchor;
	}
	
	public String getHref() {
		return this.href;
	}
	
	public void setAnchor(final String anchor) {
		this.anchor = anchor;
	}
	
	public void setHref(final String href) {
		this.href = href;
	}
	
}
