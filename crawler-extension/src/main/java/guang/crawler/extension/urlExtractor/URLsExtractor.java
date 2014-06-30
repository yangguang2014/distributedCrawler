package guang.crawler.extension.urlExtractor;

import guang.crawler.commons.Page;
import guang.crawler.commons.WebURL;

import java.util.List;

/**
 * 用来抽取页面中的URL
 * 
 * @author sun
 *
 */
public interface URLsExtractor {
	public void extractURLs(Page page, List<WebURL> urlList);
}
