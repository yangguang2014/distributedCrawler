package guang.crawler.extension.filedExtractor;

import guang.crawler.commons.DataFields;
import guang.crawler.commons.Page;
import guang.crawler.commons.WebURL;
import guang.crawler.commons.parserData.HtmlParseData;
import guang.crawler.commons.parserData.ParseData;
import guang.crawler.commons.parserData.TextParseData;
import guang.crawler.connector.WebDataTableConnector;

/**
 * 默认的域抽取器，将页面内容以及必要的WebURL的信息存储到HBase中
 *
 * @author sun
 *
 */
public class DefaultFieldExtractor implements FieldsExtractor {
	
	@Override
	public void extractFields(final Page page, final DataFields fileds) {
		WebURL webURL = page.getWebURL();
		String docID = webURL.getDocid();
		fileds.addFiled(docID, WebDataTableConnector.FAMILY_MAIN_DATA, "depth",
		        String.valueOf(webURL.getDepth()));
		fileds.addFiled(docID, WebDataTableConnector.FAMILY_MAIN_DATA, "url",
		        String.valueOf(webURL.getURL()));
		ParseData parseData = page.getParseData();
		if (parseData instanceof HtmlParseData) {
			HtmlParseData data = (HtmlParseData) parseData;
			fileds.addFiled(docID, WebDataTableConnector.FAMILY_MAIN_DATA, "page",
			        data.getHtml());
		} else if (page.getParseData() instanceof TextParseData) {
			TextParseData textParseData = (TextParseData) page.getParseData();
			String text = textParseData.getTextContent();
			fileds.addFiled(docID, WebDataTableConnector.FAMILY_MAIN_DATA, "page",
			        text);
		}
	}
	
}
