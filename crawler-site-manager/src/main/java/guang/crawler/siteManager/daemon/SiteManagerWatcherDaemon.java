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
			siteManagerInfo.watch(this);
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
		SitesConfigInfo sitesConfigInfo;
		try
		{
			sitesConfigInfo = CenterConfig.me().getSitesConfigInfo();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		try
		{
			siteManagerInfo.watch(this);
		} catch (KeeperException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (true)
		{
			// 作第一次的处理
			Date now = new Date();
			try
			{
				siteManagerInfo.load();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			boolean dispatched = siteManagerInfo.isDispatched();
			String siteToHandle = siteManagerInfo.getSiteToHandle();
			if (dispatched && siteConfig.isDispatched())
			{
				if (!siteToHandle.equals(siteConfig.getSiteToHandle()
				        .getSiteId()))
				{
					// 这样导致该站点被重新配置了
					
					// 首先关闭当前已经运行的站点管理器
					SiteManager.me().stopSiteManager();
					try
					{
						CenterConfig.me().getWorkersInfo().getOnlineWorkers()
						        .getWorkerRefreshPath().setChanged();
					} catch (InterruptedException e2)
					{
						e2.printStackTrace();
						// this should not happen
					} catch (IOException e2)
					{
						e2.printStackTrace();
						// this should not happen
					} catch (KeeperException e2)
					{
						e2.printStackTrace();
						// this should not happen
					}
					// 然后使用新的配置
					try
					{
						siteConfig.setSiteToHandle(sitesConfigInfo
						        .getSite(siteManagerInfo.getSiteToHandle()));
					} catch (InterruptedException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return;
					} catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return;
					} catch (KeeperException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return;
					}
					// 使用新的配置启动系统
					try
					{
						SiteManager.me().startSiteManager();
					} catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
					try
					{
						CenterConfig.me().getWorkersInfo().getOnlineWorkers()
						        .getWorkerRefreshPath().setChanged();
					} catch (KeeperException e2)
					{
						e2.printStackTrace();
						// this should not happen
						
					} catch (IOException e2)
					{
						e2.printStackTrace();
						// this should not happen
						
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (!dispatched && siteConfig.isDispatched())
			{
				// 如果是这种情况，那么说明该站点被终止了
				SiteManager.me().stopSiteManager();
				// 更新配置信息
				siteConfig.setDispatched(false);
				siteConfig.setSiteToHandle(null);
				try
				{
					CenterConfig.me().getWorkersInfo().getOnlineWorkers()
					        .getWorkerRefreshPath().setChanged();
				} catch (KeeperException e2)
				{
					e2.printStackTrace();
					// this should not happen
					
				} catch (IOException e2)
				{
					e2.printStackTrace();
					// this should not happen
					
				} catch (InterruptedException e2)
				{
					e2.printStackTrace();
					// this should not happen
					
				}
			} else if (dispatched && !siteConfig.isDispatched())
			{
				// 如果是这种情况，那么说明该站点被分配了一个新的站点
				siteConfig.setDispatched(true);
				try
				{
					siteConfig.setSiteToHandle(sitesConfigInfo
					        .getSite(siteManagerInfo.getSiteToHandle()));
				} catch (InterruptedException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				} catch (KeeperException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				}
				// 启动相关部分
				try
				{
					SiteManager.me().startSiteManager();
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				try
				{
					CenterConfig.me().getWorkersInfo().getOnlineWorkers()
					        .getWorkerRefreshPath().setChanged();
				} catch (InterruptedException e2)
				{
					e2.printStackTrace();
					// this should not happen
					
				} catch (IOException e2)
				{
					e2.printStackTrace();
					// this should not happen
					
				} catch (KeeperException e2)
				{
					e2.printStackTrace();
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
