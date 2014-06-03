package guang.crawler.crawlWorker.parser;

import guang.crawler.crawlWorker.util.LinkElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 这个类用来从HTML中抽取URL列表的了。
 * 
 * @author yang
 */
public class HtmlContentHandler extends DefaultHandler
{
	
	/**
	 * 锚点文字信息的长度，避免过长的文字造成的影响
	 */
	private final int	                 MAX_ANCHOR_LENGTH	= 100;
	
	private String	                     base;
	private String	                     metaRefresh;
	private String	                     metaLocation;
	
	/**
	 * 目前是否在Body元素之中
	 */
	private boolean	                     isWithinBodyElement;
	/**
	 * body正文内容
	 */
	private StringBuilder	             bodyText;
	
	/**
	 * 对外的链接
	 */
	private List<ExtractedUrlAnchorPair>	outgoingUrls;
	
	/**
	 * 目前正在处理的URL
	 */
	private ExtractedUrlAnchorPair	     curUrl	           = null;
	private boolean	                     anchorFlag	       = false;
	/**
	 * 锚点的内容
	 */
	private StringBuilder	             anchorText	       = new StringBuilder();
	
	public HtmlContentHandler()
	{
		this.isWithinBodyElement = false;
		this.bodyText = new StringBuilder();
		this.outgoingUrls = new ArrayList<ExtractedUrlAnchorPair>();
	}
	
	@Override
	public void characters(char ch[], int start, int length)
	        throws SAXException
	{
		if (this.isWithinBodyElement)
		{
			this.bodyText.append(ch, start, length);
			
			if (this.anchorFlag)
			{
				this.anchorText.append(new String(ch, start, length));
			}
		}
	}
	
	@Override
	public void endDocument() throws SAXException
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
	        throws SAXException
	{
		LinkElement element = LinkElement.getElement(localName);
		// 在这里只有Element.A，Element.AREA，Element.LINK三个元素，是因为需要采集它们的锚点文字信息，并不是说其他元素的链接地址不会被处理，它们同样也是被处理的。
		if ((element == LinkElement.A) || (element == LinkElement.AREA)
		        || (element == LinkElement.LINK))
		{
			this.anchorFlag = false;
			if (this.curUrl != null)
			{
				// 去除换行和table字符，替换成空格
				String anchor = this.anchorText.toString()
				        .replaceAll("\n", " ").replaceAll("\t", " ").trim();
				if (!anchor.isEmpty())
				{
					if (anchor.length() > this.MAX_ANCHOR_LENGTH)
					{
						anchor = anchor.substring(0, this.MAX_ANCHOR_LENGTH)
						        + "...";
					}
					this.curUrl.setAnchor(anchor);
				}
				this.anchorText.delete(0, this.anchorText.length());
			}
			// 由于锚点已经结束了，因此需要将其置为null。
			this.curUrl = null;
		}
		if (element == LinkElement.BODY)
		{
			this.isWithinBodyElement = false;
		}
	}
	
	@Override
	public void endPrefixMapping(String prefix) throws SAXException
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void error(SAXParseException exception) throws SAXException
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void fatalError(SAXParseException exception) throws SAXException
	{
		// TODO Auto-generated method stub
		
	}
	
	public String getBaseUrl()
	{
		return this.base;
	}
	
	public String getBodyText()
	{
		return this.bodyText.toString();
	}
	
	public List<ExtractedUrlAnchorPair> getOutgoingUrls()
	{
		return this.outgoingUrls;
	}
	
	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
	        throws SAXException
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void notationDecl(String name, String publicId, String systemId)
	        throws SAXException
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void processingInstruction(String target, String data)
	        throws SAXException
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public InputSource resolveEntity(String publicId, String systemId)
	        throws SAXException, IOException
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setDocumentLocator(Locator locator)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void skippedEntity(String name) throws SAXException
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void startDocument() throws SAXException
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
	        Attributes attributes) throws SAXException
	{
		LinkElement element = LinkElement.getElement(localName);
		
		if ((element == LinkElement.A) || (element == LinkElement.AREA)
		        || (element == LinkElement.LINK))
		{
			String href = attributes.getValue("href");
			if (href != null)
			{
				this.anchorFlag = true;
				this.curUrl = new ExtractedUrlAnchorPair();
				this.curUrl.setHref(href);
				this.outgoingUrls.add(this.curUrl);
			}
			return;
		}
		
		if (element == LinkElement.IMG)
		{
			String imgSrc = attributes.getValue("src");
			if (imgSrc != null)
			{
				this.curUrl = new ExtractedUrlAnchorPair();
				this.curUrl.setHref(imgSrc);
				this.outgoingUrls.add(this.curUrl);
			}
			return;
		}
		
		if ((element == LinkElement.IFRAME) || (element == LinkElement.FRAME)
		        || (element == LinkElement.EMBED))
		{
			String src = attributes.getValue("src");
			if (src != null)
			{
				this.curUrl = new ExtractedUrlAnchorPair();
				this.curUrl.setHref(src);
				this.outgoingUrls.add(this.curUrl);
			}
			return;
		}
		
		if (element == LinkElement.BASE)
		{
			if (this.base != null)
			{ // We only consider the first occurrence of the
				// Base element.
				String href = attributes.getValue("href");
				if (href != null)
				{
					this.base = href;
				}
			}
			return;
		}
		
		if (element == LinkElement.META)
		{
			String equiv = attributes.getValue("http-equiv");
			String content = attributes.getValue("content");
			if ((equiv != null) && (content != null))
			{
				equiv = equiv.toLowerCase();
				
				// http-equiv="refresh" content="0;URL=http://foo.bar/..."
				if (equiv.equals("refresh") && (this.metaRefresh == null))
				{
					int pos = content.toLowerCase().indexOf("url=");
					if (pos != -1)
					{
						this.metaRefresh = content.substring(pos + 4);
					}
					this.curUrl = new ExtractedUrlAnchorPair();
					this.curUrl.setHref(this.metaRefresh);
					this.outgoingUrls.add(this.curUrl);
				}
				
				// http-equiv="location" content="http://foo.bar/..."
				if (equiv.equals("location") && (this.metaLocation == null))
				{
					this.metaLocation = content;
					this.curUrl = new ExtractedUrlAnchorPair();
					this.curUrl.setHref(this.metaRefresh);
					this.outgoingUrls.add(this.curUrl);
				}
			}
			return;
		}
		
		if (element == LinkElement.BODY)
		{
			this.isWithinBodyElement = true;
		}
		
	}
	
	@Override
	public void startPrefixMapping(String prefix, String uri)
	        throws SAXException
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void unparsedEntityDecl(String name, String publicId,
	        String systemId, String notationName) throws SAXException
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void warning(SAXParseException exception) throws SAXException
	{
		// TODO Auto-generated method stub
		
	}
}
