package guang.crawler.centerController;

import guang.crawler.connector.CenterConfigConnector;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Transaction;

public class SiteInfo extends CenterConfigElement
{
private static final String KEY_SEED = "seedSite";
private static final String KEY_HANDLED = "handled";
private static final String KEY_SITE_MANAGER = "siteManager";
private final String name;

public SiteInfo(String path, String name, CenterConfigConnector connector)
{
    super(path, connector);
    this.name = name;
}

public String getName() throws InterruptedException
{
    return this.name;
}

public String[] getSeedSites() throws InterruptedException
{
    String seeds= this.get(SiteInfo.KEY_SEED, true);
    return seeds.split(",");
}

public String getSiteManager() throws InterruptedException
{
    return this.get(SiteInfo.KEY_SITE_MANAGER, true);
}

public boolean isHandled() throws InterruptedException
{
    return Boolean.parseBoolean(this.get(SiteInfo.KEY_HANDLED, true));
}

public void setHandled(boolean isHandled) throws InterruptedException
{
    this.put(SiteInfo.KEY_HANDLED, Boolean.toString(isHandled), true);
}

public void setSeedSites(String[] seedSites) throws InterruptedException
{
	StringBuilder result=new StringBuilder();
	for(int i=0;i<seedSites.length-1;i++){
		result.append(seedSites);
		result.append(",");
	}
	result.append(seedSites[seedSites.length]);
    this.put(SiteInfo.KEY_SEED, result.toString(), true);
}

public void setSiteManager(String addr) throws InterruptedException
{
    this.put(SiteInfo.KEY_SITE_MANAGER, addr, true);
}

@Override
public boolean update(String key, Transaction transaction)
        throws InterruptedException
{
    if (SiteInfo.KEY_SITE_MANAGER.equals(key))
    {
        return this.connector.createNode(this.path + "/"
                                                 + SiteInfo.KEY_SITE_MANAGER, CreateMode.EPHEMERAL, this
                                                                                                            .get(key, false).getBytes()) != null;
    }
    if (SiteInfo.KEY_HANDLED.equals(key))
    {
        return this.connector.createNode(this.path + "/"
                                                 + SiteInfo.KEY_HANDLED, CreateMode.EPHEMERAL,
                                                this.get(key, false).getBytes()
        ) != null;
    }
    return super.update(key, transaction);
}
}
