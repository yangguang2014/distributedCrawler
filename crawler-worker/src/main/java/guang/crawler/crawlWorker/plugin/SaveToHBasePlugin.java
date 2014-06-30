package guang.crawler.crawlWorker.plugin;

import guang.crawler.commons.Page;
import guang.crawler.commons.WebURL;
import guang.crawler.connector.WebDataTableConnector;

import java.io.IOException;

public class SaveToHBasePlugin implements DownloadPlugin {
	private WebDataTableConnector	webDataTableConnector;
	
	public SaveToHBasePlugin(final WebDataTableConnector webDataTableConnector) {
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
