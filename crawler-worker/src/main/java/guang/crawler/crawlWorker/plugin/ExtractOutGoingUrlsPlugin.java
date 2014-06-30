package guang.crawler.crawlWorker.plugin;

import guang.crawler.commons.Page;
import guang.crawler.commons.WebURL;
import guang.crawler.crawlWorker.WorkerConfig;
import guang.crawler.crawlWorker.daemon.SiteManagerConnectorManager;
import guang.crawler.extension.urlExtractor.URLsExtractor;
import guang.crawler.localConfig.ComponentLoader;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 从Page页面中抽取URL的插件.这里应该重点研究一下究竟应当以怎样的方式进行.
 *
 * @author sun
 *
 */
public class ExtractOutGoingUrlsPlugin implements DownloadPlugin {
	private SiteManagerConnectorManager	  siteManagerConnectHelper;
	private Pattern	                      filter	= Pattern
	                                                     .compile(".*(\\.(css|js|bmp|gif|jpe?g"
	                                                             + "|png|tiff?|mid|mp2|mp3|mp4"
	                                                             + "|wav|avi|mov|mpeg|ram|m4v|pdf"
	                                                             + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	private ComponentLoader<URLsExtractor>	extractorLoader;
	
	public ExtractOutGoingUrlsPlugin() throws ConfigLoadException {
		this.siteManagerConnectHelper = SiteManagerConnectorManager.me();
		String configFileName = WorkerConfig.me().getCrawlerHome()
		        + "/conf/crawler-worker/url-extractors.xml";
		File configFile = new File(configFileName);
		String schemaFileName = WorkerConfig.me().getCrawlerHome()
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
		List<WebURL> resultURLs = new LinkedList<WebURL>();
		if (page != null) {
			// 获取URLExtractor
			URLsExtractor extractor = this.extractorLoader.getComponent(page
			        .getWebURL().getURL());
			if (extractor != null) {
				// 利用URLExtractor抽取URL列表
				extractor.extractURLs(page, resultURLs);
			}
			// 过滤掉那些不需要的页面
			if (resultURLs != null) {
				Iterator<WebURL> it = resultURLs.iterator();
				while (it.hasNext()) {
					if (this.filter.matcher(it.next().getURL()).matches()) {
						it.remove();
					}
				}
			}
			// 最终将结果返回到站点管理器中.
			try {
				this.siteManagerConnectHelper.putData(page.getWebURL(),
				        resultURLs);
				return true;
			} catch (IOException e) {
				return false;
			}
		}
		return false;
	}
	
}
