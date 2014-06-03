package guang.crawler.crawlWorker.parser;

import guang.crawler.commons.WebURL;
import guang.crawler.crawlWorker.WorkerConfig;
import guang.crawler.crawlWorker.fetcher.Page;
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
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */
public class Parser
{
	
	protected static final Logger	logger	= Logger.getLogger(Parser.class
	                                               .getName());
	private HtmlParser	          htmlParser;
	private ParseContext	      parseContext;
	
	public Parser()
	{
		this.htmlParser = new HtmlParser();
		this.parseContext = new ParseContext();
	}
	
	public boolean parse(Page page, String contextURL)
	{
		
		if (Util.hasBinaryContent(page.getContentType()))
		{
			if (!WorkerConfig.me().isIncludeBinaryContentInCrawling())
			{
				return false;
			}
			
			page.setParseData(BinaryParseData.getInstance());
			return true;
			
		} else if (Util.hasPlainTextContent(page.getContentType()))
		{ // 如果只是一般的文本，而不是HTML页面，那么就没有什么好处理的了
			try
			{
				TextParseData parseData = new TextParseData();
				if (page.getContentCharset() == null)
				{
					parseData.setTextContent(new String(page.getContentData()));
				} else
				{
					parseData.setTextContent(new String(page.getContentData(),
					        page.getContentCharset()));
				}
				page.setParseData(parseData);
				return true;
			} catch (Exception e)
			{
				Parser.logger.error(e.getMessage() + ", while parsing: "
				        + page.getWebURL().getURL());
			}
			return false;
		}
		
		Metadata metadata = new Metadata();
		HtmlContentHandler contentHandler = new HtmlContentHandler();
		InputStream inputStream = null;
		try
		{
			inputStream = new ByteArrayInputStream(page.getContentData());
			this.htmlParser.parse(inputStream, contentHandler, metadata,
			        this.parseContext);
		} catch (Exception e)
		{
			Parser.logger.error(e.getMessage() + ", while parsing: "
			        + page.getWebURL().getURL());
		} finally
		{
			try
			{
				if (inputStream != null)
				{
					inputStream.close();
				}
			} catch (IOException e)
			{
				Parser.logger.error(e.getMessage() + ", while parsing: "
				        + page.getWebURL().getURL());
			}
		}
		
		if (page.getContentCharset() == null)
		{
			page.setContentCharset(metadata.get("Content-Encoding"));
		}
		
		HtmlParseData parseData = new HtmlParseData();
		parseData.setText(contentHandler.getBodyText().trim());
		parseData.setTitle(metadata.get(DublinCore.TITLE));
		
		List<WebURL> outgoingUrls = new ArrayList<WebURL>();
		
		String baseURL = contentHandler.getBaseUrl();
		if (baseURL != null)
		{
			contextURL = baseURL;
		}
		
		int urlCount = 0;
		for (ExtractedUrlAnchorPair urlAnchorPair : contentHandler
		        .getOutgoingUrls())
		{
			String href = urlAnchorPair.getHref();
			href = href.trim();
			if (href.length() == 0)
			{
				continue;
			}
			String hrefWithoutProtocol = href.toLowerCase();
			if (href.startsWith("http://"))
			{
				hrefWithoutProtocol = href.substring(7);
			}
			if (!hrefWithoutProtocol.contains("javascript:")
			        && !hrefWithoutProtocol.contains("mailto:")
			        && !hrefWithoutProtocol.contains("@"))
			{
				String url = URLCanonicalizer.getCanonicalURL(href, contextURL);
				if (url != null)
				{
					WebURL webURL = new WebURL();
					webURL.setURL(url);
					webURL.setAnchor(urlAnchorPair.getAnchor());
					outgoingUrls.add(webURL);
					urlCount++;
					if (urlCount > WorkerConfig.me()
					        .getMaxOutgoingLinksToFollow())
					{
						break;
					}
				}
			}
		}
		
		parseData.setOutgoingUrls(outgoingUrls);
		
		try
		{
			if (page.getContentCharset() == null)
			{
				parseData.setHtml(new String(page.getContentData()));
			} else
			{
				parseData.setHtml(new String(page.getContentData(), page
				        .getContentCharset()));
			}
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return false;
		}
		
		page.setParseData(parseData);
		return true;
		
	}
	
}
