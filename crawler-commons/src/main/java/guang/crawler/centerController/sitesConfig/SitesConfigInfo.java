package guang.crawler.centerController.sitesConfig;

import guang.crawler.centerController.CenterConfig;
import guang.crawler.centerController.CenterConfigElement;
import guang.crawler.connector.CenterConfigConnector;

import java.io.IOException;

public class SitesConfigInfo extends CenterConfigElement
{
	
	private SitesInfo	sitesInfo;
	
	public SitesConfigInfo(String path, CenterConfigConnector connector)
	{
		super(path, connector);
	}
	
	public SitesInfo getSitesInfo() throws InterruptedException, IOException
	{
		if (this.sitesInfo == null)
		{
			this.sitesInfo = new SitesInfo(this.path + CenterConfig.SITES_PATH,
			        this.connector);
			this.sitesInfo.load();
		}
		return this.sitesInfo;
	}
	
}
