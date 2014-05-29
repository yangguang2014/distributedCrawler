package guang.crawler.centerController.siteManagers;

import guang.crawler.centerController.CenterConfig;
import guang.crawler.centerController.CenterConfigElement;
import guang.crawler.connector.CenterConfigConnector;

public class SiteManagersConfigInfo extends CenterConfigElement {

	private OnlineSiteManagers onlineSiteManagers;

	public SiteManagersConfigInfo(String path, CenterConfigConnector connector) {
		super(path, connector);
	}

	public OnlineSiteManagers getOnlineSiteManagers() {
		if (this.onlineSiteManagers == null) {
			this.onlineSiteManagers = new OnlineSiteManagers(this.path
					+ CenterConfig.ONLINE_SITEMANAGERS_PATH, this.connector);
		}
		return this.onlineSiteManagers;
	}

}
