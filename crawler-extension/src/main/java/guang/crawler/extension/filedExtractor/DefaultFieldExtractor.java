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

	/**
	 * 将整个页面内容作为域来存储.
	 * 
	 * @param page
	 */
	public static void extractPageBody(final Page page) {
		WebURL webURL = page.getWebURL();
		DataFields fields = page.getDataToSave();
		String docID = webURL.getDocid();
		fields.addFiled(docID, WebDataTableConnector.FAMILY_MAIN_DATA, "depth",
		                String.valueOf(webURL.getDepth()))
		      .addFiled(docID, WebDataTableConnector.FAMILY_MAIN_DATA, "url",
		                String.valueOf(webURL.getURL()));
		ParseData parseData = page.getParseData();
		if (parseData instanceof HtmlParseData) {
			HtmlParseData data = (HtmlParseData) parseData;
			fields.addFiled(docID, WebDataTableConnector.FAMILY_MAIN_DATA,
			                "page", data.getHtml());

		} else if (page.getParseData() instanceof TextParseData) {
			TextParseData textParseData = (TextParseData) page.getParseData();
			String text = textParseData.getTextContent();
			fields.addFiled(docID, WebDataTableConnector.FAMILY_MAIN_DATA,
			                "page", text);
		}
	}

	@Override
	public void extractFields(final Page page) {
		DefaultFieldExtractor.extractPageBody(page);
	}
	
}
