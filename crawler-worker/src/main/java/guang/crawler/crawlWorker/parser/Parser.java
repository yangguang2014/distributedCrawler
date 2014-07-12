package guang.crawler.crawlWorker.parser;

import guang.crawler.commons.Page;
import guang.crawler.commons.WebURL;
import guang.crawler.commons.parserData.BinaryParseData;
import guang.crawler.commons.parserData.HtmlParseData;
import guang.crawler.commons.parserData.TextParseData;
import guang.crawler.crawlWorker.WorkerConfig;
import guang.crawler.crawlWorker.url.URLCanonicalizer;
import guang.crawler.crawlWorker.util.Util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.tika.metadata.DublinCore;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;

/**
 * 解析器,用来对下载的页面进行解析
 *
 * @author sun
 *
 */
public class Parser {
	
	protected static final Logger	logger	= Logger.getLogger(Parser.class.getName());
	/**
	 * HTML页面的解析器
	 */
	private HtmlParser	          htmlParser;
	/**
	 * 解析上下文
	 */
	private ParseContext	      parseContext;
	
	public Parser() {
		this.htmlParser = new HtmlParser();
		this.parseContext = new ParseContext();
	}
	
	/**
	 * 对页面进行解析
	 *
	 * @param page
	 * @param contextURL
	 * @return
	 */
	public boolean parse(final Page page, final String contextURL) {
		
		// 如果页面中含有二进制页面内容.
		if (Util.hasBinaryContent(page.getContentType())) {
			if (!WorkerConfig.me()
			                 .isIncludeBinaryContentInCrawling()) {
				return false;
			}
			
			page.setParseData(BinaryParseData.getInstance());
			return true;
			
		}
		// 如果页面中含有文本内容(txt,javascript,css)
		else if (Util.hasPlainTextContent(page.getContentType())) { // 如果只是一般的文本，而不是HTML页面，那么就没有什么好处理的了
			try {
				TextParseData parseData = new TextParseData();
				if (page.getContentCharset() == null) {
					parseData.setTextContent(new String(page.getContentData()));
				} else {
					parseData.setTextContent(new String(page.getContentData(),
					        page.getContentCharset()));
				}
				page.setParseData(parseData);
				return true;
			} catch (Exception e) {
				Parser.logger.error(e.getMessage() + ", while parsing: "
				        + page.getWebURL()
				              .getURL());
			}
			return false;
		} else { // 否则,其他类型都被算作HTML页面类型
			// 将其当作HTML页面进行解析
			Metadata metadata = new Metadata();
			HtmlContentHandler contentHandler = new HtmlContentHandler();
			InputStream inputStream = null;
			try {
				inputStream = new ByteArrayInputStream(page.getContentData());
				this.htmlParser.parse(inputStream, contentHandler, metadata,
				                      this.parseContext);
			} catch (Exception e) {
				Parser.logger.error(e.getMessage() + ", while parsing: "
				        + page.getWebURL()
				              .getURL());
			} finally {
				try {
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (IOException e) {
					Parser.logger.error(e.getMessage() + ", while parsing: "
					        + page.getWebURL()
					              .getURL());
				}
			}
			// 将解析的结果设置到HtmlParseData以及Page中
			if (page.getContentCharset() == null) {
				page.setContentCharset(metadata.get("Content-Encoding"));
			}
			
			HtmlParseData parseData = new HtmlParseData();
			parseData.setText(contentHandler.getBodyText()
			                                .trim());
			parseData.setTitle(metadata.get(DublinCore.TITLE));
			// 处理获取的URL连接
			List<WebURL> outgoingUrls = this.parseURLs(contextURL,
			                                           contentHandler);
			parseData.setOutgoingUrls(outgoingUrls);
			
			try {
				if (page.getContentCharset() == null) {
					parseData.setHtml(new String(page.getContentData()));
				} else {
					parseData.setHtml(new String(page.getContentData(),
					        page.getContentCharset()));
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return false;
			}
			
			page.setParseData(parseData);
			return true;
		}
		
	}

	/**
	 * 对URL进行解析,根据页面的&lt;base&gt;的设置,确定那些相对URL的路径.
	 * 
	 * @param contextURL
	 * @param contentHandler
	 * @return
	 */
	private List<WebURL> parseURLs(String contextURL,
	        final HtmlContentHandler contentHandler) {
		List<WebURL> outgoingUrls = new ArrayList<WebURL>();
		String baseURL = contentHandler.getBaseUrl();
		if (baseURL != null) {
			contextURL = baseURL;
		}

		int urlCount = 0;
		for (ExtractedUrlAnchorPair urlAnchorPair : contentHandler.getOutgoingUrls()) {
			String href = urlAnchorPair.getHref();
			href = href.trim();
			if (href.length() == 0) {
				continue;
			}
			String hrefWithoutProtocol = href.toLowerCase();
			if (href.startsWith("http://")) {
				hrefWithoutProtocol = href.substring(7);
			}
			if (!hrefWithoutProtocol.contains("javascript:")
			        && !hrefWithoutProtocol.contains("mailto:")
			        && !hrefWithoutProtocol.contains("@")) {
				String url = URLCanonicalizer.getCanonicalURL(href, contextURL);
				if (url != null) {
					WebURL webURL = WebURL.newWebURL()
					                      .setURL(url)
					                      .setAnchor(urlAnchorPair.getAnchor());
					outgoingUrls.add(webURL);
					urlCount++;
					if (urlCount > WorkerConfig.me()
					                           .getMaxOutgoingLinksToFollow()) {
						break;
					}
				}
			}
		}
		return outgoingUrls;
	}
	
}
