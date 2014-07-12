package guang.crawler.crawlWorker.pageProcessor;

import guang.crawler.commons.Page;
import guang.crawler.commons.WebURL;
import guang.crawler.crawlWorker.daemon.SiteManagerConnectorManager;

import java.io.IOException;
import java.util.List;

/**
 * 将采集得到的所有新的URL发送到站点管理器中
 *
 * @author sun
 *
 */
public class UploadExtractedLinksPlugin implements DownloadPlugin {
	/**
	 * 站点管理器连接器的管理器
	 */
	private final SiteManagerConnectorManager	siteManagerConnectHelper;

	public UploadExtractedLinksPlugin() {
		this.siteManagerConnectHelper = SiteManagerConnectorManager.me();
	}
	
	@Override
	public boolean work(final Page page) {
		List<WebURL> resultURLs = page.getLinksToFollow();
		// 最终将结果返回到站点管理器中.
		try {
			this.siteManagerConnectHelper.putData(page.getWebURL(), resultURLs);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
}
