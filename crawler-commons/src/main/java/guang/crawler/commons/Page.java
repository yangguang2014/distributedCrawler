package guang.crawler.commons;

import guang.crawler.commons.parserData.ParseData;

import java.nio.charset.Charset;
import java.util.LinkedList;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * 该类用以表示下载的页面
 *
 * @author sun
 *
 */
public class Page {
	protected static final Logger	 logger	= Logger.getLogger(Page.class.getName());
	/**
	 * 当前页面的URL
	 */
	protected final WebURL	         url;

	/**
	 * 当前页面所对应的最原始的数据内容,表示为byte数组
	 */
	protected byte[]	             contentData;

	/**
	 * 当前页面的MIME类型,例如: "text/html; charset=UTF-8"
	 */
	protected String	             contentType;
	
	/**
	 * 当前页面的编码方式,例如: "gzip"
	 */
	protected String	             contentEncoding;
	
	/**
	 * 当前页面的字符集,例如: "UTF-8"
	 */
	protected String	             contentCharset;
	
	/**
	 * 页面HTTP响应的报头信息.
	 */
	protected Header[]	             fetchResponseHeaders;
	
	/**
	 * 原始的数据解析之后的数据.
	 */
	protected ParseData	             parseData;
	
	/**
	 * 从页面中抽取的需要保存的数据
	 */
	private final DataFields	     dataToSave;
	/**
	 * 从页面中抽取的需要爬取的网页
	 */
	private final LinkedList<WebURL>	linksToFollow;
	
	public Page(final WebURL url) {
		this.url = url;
		this.dataToSave = new DataFields();
		this.linksToFollow = new LinkedList<WebURL>();
		
	}
	
	/**
	 * 获取当前页面的字符集
	 *
	 * @return
	 */
	public String getContentCharset() {
		return this.contentCharset;
	}

	/**
	 * 获取当前页面的原始数据内容
	 *
	 * @return
	 */
	public byte[] getContentData() {
		return this.contentData;
	}
	
	/**
	 * 获取当前页面的编码类型,例如:"gzip"
	 *
	 * @return
	 */
	public String getContentEncoding() {
		return this.contentEncoding;
	}
	
	/**
	 * 获取当前页面的MIME类型,例如: "text/html; charset=UTF-8"
	 */
	public String getContentType() {
		return this.contentType;
	}
	
	/**
	 * 获取当前页面中抽取的需要保存的数据
	 *
	 * @return
	 */
	public DataFields getDataToSave() {
		return this.dataToSave;
	}
	
	/**
	 * 获取当前页面的HTTP响应报头信息
	 */
	public Header[] getFetchResponseHeaders() {
		return this.fetchResponseHeaders;
	}
	
	/**
	 * 获取当前页面抽取的需要爬取的网页URL列表
	 *
	 * @return
	 */
	public LinkedList<WebURL> getLinksToFollow() {
		return this.linksToFollow;
	}
	
	/**
	 * 获取当前页面经过解析后的数据.
	 *
	 * @return
	 */
	public ParseData getParseData() {
		return this.parseData;
	}
	
	/**
	 * 获取当前页面的URL
	 *
	 * @return
	 */
	public WebURL getWebURL() {
		return this.url;
	}
	
	/**
	 * 从抓取的HttpEntity中加载当前页面.
	 */
	public void load(final HttpEntity entity) throws Exception {
		
		this.contentType = null;
		Header type = entity.getContentType();
		if (type != null) {
			this.contentType = type.getValue();
		}
		
		this.contentEncoding = null;
		Header encoding = entity.getContentEncoding();
		if (encoding != null) {
			this.contentEncoding = encoding.getValue();
		}
		
		Charset charset = ContentType.getOrDefault(entity)
		                             .getCharset();
		if (charset != null) {
			this.contentCharset = charset.displayName();
		}
		
		this.contentData = EntityUtils.toByteArray(entity);
		
	}
	
	/**
	 * 设置当前页面的字符集
	 *
	 * @param contentCharset
	 */
	public void setContentCharset(final String contentCharset) {
		this.contentCharset = contentCharset;
	}
	
	/**
	 * 设置当前页面的原始数据
	 *
	 * @param contentData
	 */
	public void setContentData(final byte[] contentData) {
		this.contentData = contentData;
	}
	
	/**
	 * 设置当前页面的编码方式
	 *
	 * @param contentEncoding
	 */
	public void setContentEncoding(final String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}

	/**
	 * 设置当前页面的MIME类型.
	 *
	 * @param contentType
	 */
	public void setContentType(final String contentType) {
		this.contentType = contentType;
	}
	
	/**
	 * 设置当前页面的HTTP响应报头信息
	 *
	 * @param headers
	 */
	public void setFetchResponseHeaders(final Header[] headers) {
		this.fetchResponseHeaders = headers;
	}

	/**
	 * 设置当前页面解析后的数据
	 *
	 * @param parseData
	 */
	public void setParseData(final ParseData parseData) {
		this.parseData = parseData;
	}
	
}
