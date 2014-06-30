package guang.crawler.extension.urlExtractor;

import guang.crawler.commons.Page;
import guang.crawler.commons.WebURL;
import guang.crawler.commons.parserData.HtmlParseData;
import guang.crawler.commons.parserData.ParseData;

import java.util.List;

/**
 * 默认抽取URL的类.当没有为该站点配置特别的抽取方式的时候,就使用该类,抽取静态页面中的所有URL.
 *
 * @author sun
 *
 */
public class DefaultURLExtractor implements URLsExtractor {
	
	@Override
	public void extractURLs(final Page page) {
		List<WebURL> urlList = page.getLinksToFollow();
		ParseData data = page.getParseData();
		if (data instanceof HtmlParseData) {
			HtmlParseData htmlData = (HtmlParseData) data;
			urlList.addAll(htmlData.getOutgoingUrls());
		}
	}
	
}
