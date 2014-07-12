package guang.crawler.controller;

import guang.crawler.centerConfig.CenterConfig;
import guang.crawler.controller.webservice.WebServiceDaemon;

import java.io.IOException;
import java.util.Date;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * 监控中央配置器,根据manager的配置情况决定控制器的工作.
 *
 * @author sun
 *
 */
public class ControllerManagerWatcher extends Thread implements Watcher {
	/**
	 * 事件发生的时间
	 */
	private Date	                        eventTime	= new Date();
	/**
	 * 当前类的单例对象
	 */
	private static ControllerManagerWatcher	watcher;

	public static ControllerManagerWatcher me() {
		if (ControllerManagerWatcher.watcher == null) {
			ControllerManagerWatcher.watcher = new ControllerManagerWatcher();
		}
		return ControllerManagerWatcher.watcher;
	}

	private ControllerManagerWatcher() {
		this.setName("CONTROLLER-MANAGER-WATCHER");
	}

	/**
	 * 处理manager节点的事件
	 */
	@Override
	public void process(final WatchedEvent event) {
		// 应当持续不断的监控该事件，然后不断的处理
		try {
			CenterConfig.me()
			            .getControllerInfo()
			            .getControllerManagerInfo()
			            .watchNode(ControllerManagerWatcher.watcher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 然后开始处理事件
		switch (event.getType()) {
		case NodeDeleted: // 之前的监控者已经退出了，那么想办法取代该节点
			synchronized (this.eventTime) {
				this.eventTime.setTime(System.currentTimeMillis());
				this.eventTime.notifyAll();
			}
			break;
		default:
			break;

		}

	}
	
	/**
	 * 系统的主线程.当控制器获得了manager节点的控制权之后,才开始真正进行调度工作.
	 */
	@Override
	public void run() {
		// 应当持续不断的监控该事件，然后不断的处理
		try {
			CenterConfig.me()
			            .getControllerInfo()
			            .getControllerManagerInfo()
			            .watchNode(ControllerManagerWatcher.watcher);
		} catch (Exception e) {
			e.printStackTrace();
		}

		while (true) {
			boolean success = false;
			try {
				// 处理之前获取当前时间
				Date now = new Date();
				// 竞争controller角色
				try {
					success = CenterConfig.me()
					                      .getControllerInfo()
					                      .competeForController();
				} catch (KeeperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 如果竞争成功,那么启动控制器的工作线程,并发布web服务
				if (success) {
					ControllerWorkThread.me()
					                    .start();
					WebServiceDaemon.me()
					                .start();
				}
				// 检查在上面处理过程中有没有新的事件产生,如果没有,那么休眠,否则再次检查.
				synchronized (this.eventTime) {
					if (now.after(this.eventTime)) {
						this.eventTime.wait();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (KeeperException e) {
				return;
			}
		}

	}
}
