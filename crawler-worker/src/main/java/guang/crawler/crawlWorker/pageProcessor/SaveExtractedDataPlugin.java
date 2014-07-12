package guang.crawler.crawlWorker.pageProcessor;

import guang.crawler.commons.Page;
import guang.crawler.commons.WebURL;
import guang.crawler.connector.WebDataTableConnector;

import java.io.IOException;

/**
 * 将抽取的数据存储到HBase的插件
 *
 * @author sun
 *
 */
public class SaveExtractedDataPlugin implements DownloadPlugin {
	/**
	 * HBase连接器
	 */
	private WebDataTableConnector	webDataTableConnector;
	
	public SaveExtractedDataPlugin(
	        final WebDataTableConnector webDataTableConnector) {
		this.webDataTableConnector = webDataTableConnector;
	}
	
	@Override
	public boolean work(final Page page) {
		WebURL webURL = page.getWebURL();
		try {
			this.webDataTableConnector.addDataFields(webURL,
			                                         page.getDataToSave());
			System.out.println("[OK] save url success:" + webURL.getURL());
			return true;
		} catch (IOException e) {
			System.out.println("[FAILED] save url failed:" + webURL.getURL());
			return false;
		}

	}
	
}
