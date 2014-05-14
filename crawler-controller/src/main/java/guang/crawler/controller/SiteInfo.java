package guang.crawler.controller;

import guang.crawler.connector.ZookeeperConnector;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Transaction;

public class SiteInfo extends ZookeeperElement
{
private static final String KEY_SEED = "seedSite";
private static final String KEY_HANDLED = "handled";
private static final String KEY_SITE_MANAGER = "siteManager";
private final String name;

public SiteInfo(String path, String name, ZookeeperConnector connector)
{
    super(path, connector);
    this.name = name;
}

public String getName() throws InterruptedException
{
    return this.name;
}

public String getSeedSite() throws InterruptedException
{
    return this.get(SiteInfo.KEY_SEED, true);
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

public void setSeedSite(String seedSite) throws InterruptedException
{
    this.put(SiteInfo.KEY_SEED, seedSite, true);
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
