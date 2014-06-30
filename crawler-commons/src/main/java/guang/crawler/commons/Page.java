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
 * This class contains the data for a fetched and parsed page. 该类用意表示下载的页面
 *
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */
public class Page {
	protected static final Logger	 logger	= Logger.getLogger(Page.class.getName());
	/**
	 * The URL of this page.
	 */
	protected final WebURL	         url;
	
	/**
	 * The content of this page in binary format.
	 */
	protected byte[]	             contentData;
	
	/**
	 * The ContentType of this page. For example: "text/html; charset=UTF-8"
	 */
	protected String	             contentType;

	/**
	 * The encoding of the content. For example: "gzip"
	 */
	protected String	             contentEncoding;

	/**
	 * The charset of the content. For example: "UTF-8"
	 */
	protected String	             contentCharset;

	/**
	 * Headers which were present in the response of the fetch request
	 */
	protected Header[]	             fetchResponseHeaders;

	/**
	 * The parsed data populated by parsers
	 */
	protected ParseData	             parseData;

	/**
	 * 从页面中抽取的需要保存的数据
	 */
	private final DataFields	     dataToSave;
	/**
	 * 下一步将要爬取的网页
	 */
	private final LinkedList<WebURL>	linksToFollow;

	public Page(final WebURL url) {
		this.url = url;
		this.dataToSave = new DataFields();
		this.linksToFollow = new LinkedList<WebURL>();

	}

	/**
	 * Returns the charset of the content. For example: "UTF-8"
	 */
	public String getContentCharset() {
		return this.contentCharset;
	}
	
	/**
	 * Returns the content of this page in binary format.
	 */
	public byte[] getContentData() {
		return this.contentData;
	}

	/**
	 * Returns the encoding of the content. For example: "gzip"
	 */
	public String getContentEncoding() {
		return this.contentEncoding;
	}

	/**
	 * Returns the ContentType of this page. For example:
	 * "text/html; charset=UTF-8"
	 */
	public String getContentType() {
		return this.contentType;
	}

	public DataFields getDataToSave() {
		return this.dataToSave;
	}

	/**
	 * Returns headers which were present in the response of the fetch request
	 */
	public Header[] getFetchResponseHeaders() {
		return this.fetchResponseHeaders;
	}

	public LinkedList<WebURL> getLinksToFollow() {
		return this.linksToFollow;
	}

	/**
	 * Returns the parsed data generated for this page by parsers
	 */
	public ParseData getParseData() {
		return this.parseData;
	}

	public WebURL getWebURL() {
		return this.url;
	}

	/**
	 * Loads the content of this page from a fetched HttpEntity.
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

	public void setContentCharset(final String contentCharset) {
		this.contentCharset = contentCharset;
	}

	public void setContentData(final byte[] contentData) {
		this.contentData = contentData;
	}

	public void setContentEncoding(final String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}
	
	public void setContentType(final String contentType) {
		this.contentType = contentType;
	}

	public void setFetchResponseHeaders(final Header[] headers) {
		this.fetchResponseHeaders = headers;
	}
	
	public void setParseData(final ParseData parseData) {
		this.parseData = parseData;
	}

}
