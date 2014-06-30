package guang.crawler.extension.urlExtractor;

import guang.crawler.commons.Page;

/**
 * 用来抽取页面中的URL
 *
 * @author sun
 *
 */
public interface URLsExtractor {
	public void extractURLs(Page page);
}
