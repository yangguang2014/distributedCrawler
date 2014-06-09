package guang.crawler.siteManager.daemon;

import guang.crawler.centerController.CenterConfig;
import guang.crawler.centerController.siteManagers.SiteManagerInfo;
import guang.crawler.centerController.sitesConfig.SitesConfigInfo;
import guang.crawler.siteManager.SiteConfig;
import guang.crawler.siteManager.SiteManager;

import java.io.IOException;
import java.util.Date;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * 监控中央配置中自己节点的信息，并根据这些信息决定自己的工作。
 * 
 * @author sun
 * 
 */
public class SiteManagerWatcherDaemon implements Watcher, Runnable
{
	
	private Date	eventTime	= new Date();
	
	public SiteManagerWatcherDaemon()
	{
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void process(WatchedEvent event)
	{
		// 首先，立即注册一个事件监听
		SiteManagerInfo siteManagerInfo = SiteConfig.me().getSiteManagerInfo();
		try
		{
			siteManagerInfo.watchNode(this);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeeperException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (event.getType() == Watcher.Event.EventType.NodeDataChanged)
		{
			// 如果节点信息更改了
			try
			{
				siteManagerInfo.load();
				synchronized (this.eventTime)
				{
					this.eventTime.setTime(System.currentTimeMillis());
					this.eventTime.notifyAll();
				}
			} catch (InterruptedException e)
			{
				return;
			} catch (IOException e)
			{
				return;
			}
			
		}
	}
	
	@Override
	public void run()
	{
		// 首先，立即注册一个事件监听
		SiteConfig siteConfig = SiteConfig.me();
		SiteManagerInfo siteManagerInfo = siteConfig.getSiteManagerInfo();
		try
		{
			siteManagerInfo.watchNode(this);
		} catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		
		SitesConfigInfo sitesConfigInfo;
		try
		{
			sitesConfigInfo = CenterConfig.me().getSitesConfigInfo();
		} catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		
		while (true)
		{
			Date now = new Date();
			try
			{
				siteManagerInfo.load();
			} catch (Exception e)
			{
				e.printStackTrace();
				return;
			}
			boolean dispatched = siteManagerInfo.isDispatched();
			String siteToHandle = siteManagerInfo.getSiteToHandle();
			// 判断是否需要停止
			if (siteConfig.isDispatched()
			        && ((!dispatched) || (!siteToHandle.equals(siteConfig
			                .getSiteToHandle().getSiteId()))))
			{
				// 这种情况需要关闭当前已经运行的站点管理器
				SiteManager.me().stopSiteManager();
				siteConfig.setDispatched(false);
				try
				{
					CenterConfig.me().getWorkersInfo().getOnlineWorkers()
					        .notifyChanged();
				} catch (Exception e)
				{
					e.printStackTrace();
					// this should not happen
				}
			}
			// 判断是否需要开启
			if (dispatched && (!siteConfig.isDispatched()))
			{
				// 如果需要启动，而当前没有启动，那么启动之
				// 然后使用新的配置
				try
				{
					siteConfig.setSiteToHandle(sitesConfigInfo.getSitesInfo()
					        .getSite(siteManagerInfo.getSiteToHandle()));
				} catch (Exception e)
				{
					e.printStackTrace();
					return;
				}
				// 使用新的配置启动系统
				try
				{
					SiteManager.me().startSiteManager();
				} catch (Exception e)
				{
					e.printStackTrace();
					return;
				}
				siteConfig.setDispatched(true);
				try
				{
					CenterConfig.me().getWorkersInfo().getOnlineWorkers()
					        .notifyChanged();
				} catch (Exception e)
				{
					e.printStackTrace();
					// this should not happen
					
				}
			}
			
			synchronized (this.eventTime)
			{
				if (now.after(this.eventTime))
				{
					try
					{
						this.eventTime.wait();
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
				}
			}
		}
		
	}
}
