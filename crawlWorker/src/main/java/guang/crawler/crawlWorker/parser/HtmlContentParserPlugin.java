package guang.crawler.crawlWorker.parser;

import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public abstract class HtmlContentParserPlugin implements EntityResolver,
		DTDHandler, ContentHandler, ErrorHandler
{

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException
	{
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

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
			Attributes atts) throws SAXException
	{
		// TODO Auto-generated method stub

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
