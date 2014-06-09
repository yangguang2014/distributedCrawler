package guang.crawler.controller;

import guang.crawler.centerController.CenterConfig;
import guang.crawler.controller.webservice.WebServiceDaemon;

import java.io.IOException;
import java.util.Date;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class ControllerManagerWatcher extends Thread implements Watcher
{
	
	private Date	                        eventTime	= new Date();
	private static ControllerManagerWatcher	watcher;
	
	public static ControllerManagerWatcher me()
	{
		if (ControllerManagerWatcher.watcher == null)
		{
			ControllerManagerWatcher.watcher = new ControllerManagerWatcher();
		}
		return ControllerManagerWatcher.watcher;
	}
	
	private ControllerManagerWatcher()
	{
		this.setName("CONTROLLER-MANAGER-WATCHER");
	}
	
	@Override
	public void process(WatchedEvent event)
	{
		// 应当持续不断的监控该事件，然后不断的处理
		try
		{
			CenterConfig.me().getControllerInfo().getControllerManagerInfo()
			        .watchNode(ControllerManagerWatcher.watcher);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// 然后开始处理事件
		switch (event.getType())
		{
		case NodeDeleted: // 之前的监控者已经退出了，那么想办法取代该节点
			synchronized (this.eventTime)
			{
				this.eventTime.setTime(System.currentTimeMillis());
				this.eventTime.notifyAll();
			}
			break;
		default:
			break;
		
		}
		
	}
	
	@Override
	public void run()
	{
		// 应当持续不断的监控该事件，然后不断的处理
		try
		{
			CenterConfig.me().getControllerInfo().getControllerManagerInfo()
			        .watchNode(ControllerManagerWatcher.watcher);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		while (true)
		{
			boolean success = false;
			try
			{
				Date now = new Date();
				try
				{
					success = CenterConfig.me().getControllerInfo()
					        .competeForController();
				} catch (KeeperException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (success)
				{
					ControllerWorkThread.me().start();
					WebServiceDaemon.me().start();
				}
				synchronized (this.eventTime)
				{
					if (now.after(this.eventTime))
					{
						this.eventTime.wait();
					}
				}
			} catch (InterruptedException e)
			{
				e.printStackTrace();
				return;
			} catch (IOException e)
			{
				e.printStackTrace();
			} catch (KeeperException e)
			{
				return;
			}
		}
		
	}
}
