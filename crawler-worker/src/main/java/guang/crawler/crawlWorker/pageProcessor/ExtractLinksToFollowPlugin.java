package guang.crawler.crawlWorker.pageProcessor;

import guang.crawler.commons.Page;
import guang.crawler.commons.WebURL;
import guang.crawler.crawlWorker.WorkerConfig;
import guang.crawler.extension.urlExtractor.URLsExtractor;
import guang.crawler.localConfig.ComponentLoader;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 从Page页面中抽取URL的插件.这里应该重点研究一下究竟应当以怎样的方式进行.
 *
 * @author sun
 *
 */
public class ExtractLinksToFollowPlugin implements DownloadPlugin {
	/**
	 * 需要过滤掉的URL的正则表达式样式
	 */
	private Pattern	                       filter	= Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
	                                                      + "|png|tiff?|mid|mp2|mp3|mp4"
	                                                      + "|wav|avi|mov|mpeg|ram|m4v|pdf"
	                                                      + "|rm|smil|wmv|swf|wma|zip|rar|gz|ico))$");
	/**
	 * URLsExtractor的加载器,用来从指定配置文件中加载URLsExtractor.
	 */
	private ComponentLoader<URLsExtractor>	extractorLoader;

	public ExtractLinksToFollowPlugin() throws ConfigLoadException {
		String configFileName = WorkerConfig.me()
		                                    .getCrawlerHome()
		        + "/conf/crawler-worker/url-extractors.xml";
		File configFile = new File(configFileName);
		String schemaFileName = WorkerConfig.me()
		                                    .getCrawlerHome()
		        + "/etc/xsd/components.xsd";
		File schemaFile = new File(schemaFileName);
		this.extractorLoader = new ComponentLoader<URLsExtractor>(configFile,
		        schemaFile);
		try {
			this.extractorLoader.load();
		} catch (Exception e) {
			throw new ConfigLoadException(
			        "load url-extractors.xml file failed!", e);
		}

	}

	@Override
	public boolean work(final Page page) {

		if (page != null) {
			// 获取URLExtractor
			URLsExtractor extractor = this.extractorLoader.getComponent(page.getWebURL()
			                                                                .getURL());
			if (extractor != null) {
				// 利用URLExtractor抽取URL列表
				extractor.extractURLs(page);
			}
			List<WebURL> resultURLs = page.getLinksToFollow();
			// 过滤掉那些不需要的页面
			if (resultURLs != null) {
				Iterator<WebURL> it = resultURLs.iterator();
				while (it.hasNext()) {
					if (this.filter.matcher(it.next()
					                          .getURL())
					               .matches()) {
						it.remove();
					}
				}
			}
			return true;
		}
		return false;
	}

}
