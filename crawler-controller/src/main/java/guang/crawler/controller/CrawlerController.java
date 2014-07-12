package guang.crawler.controller;

import guang.crawler.centerConfig.CenterConfig;
import guang.crawler.commons.GenericState;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

/**
 * 爬虫控制器类,用来启动和关闭爬虫控制器类
 *
 * @author sun
 *
 */
public class CrawlerController {

	public static CrawlerController	controller;

	/**
	 * 单例模式,获取爬虫控制器
	 *
	 * @return
	 */
	public static CrawlerController me() {
		if (CrawlerController.controller == null) {
			CrawlerController.controller = new CrawlerController();
		}
		return CrawlerController.controller;
	}

	/**
	 * 本地配置属性
	 */
	private ControllerConfig	controllerConfig;
	/**
	 * 当前控制器的状态
	 */
	private GenericState	 controllerState;

	private CrawlerController() {
		this.controllerState = GenericState.created;
	}

	/**
	 * 初始化控制器
	 *
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public CrawlerController init() throws IOException, InterruptedException {
		if (this.controllerState != GenericState.created) {
			return this;
		}
		this.controllerConfig = ControllerConfig.me()
		                                        .init();
		CenterConfig.me()
		            .init(this.controllerConfig.getZookeeperQuorum());
		this.controllerState = GenericState.inited;
		return this;
	}

	/**
	 * 启动控制器
	 * 
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	public void start() throws InterruptedException, KeeperException {
		// 首先检查控制器当前的状态
		if (this.controllerState == GenericState.started) {
			System.out.println("Controller Already started...");
			return;
		} else if (this.controllerState.getState() < GenericState.inited.getState()) {
			throw new IllegalStateException(
			        "Controller Should be inited first!");
		}
		// 尝试监听控制器的角色，尝试竞争该角色，如果竞争成功，就启动工作线程，结束当前线程。
		ControllerManagerWatcher.me()
		                        .start();
		// 即使没有获得角色，也应当设置为started状态，因为后续的过程是通过中断来引导的了。
		this.controllerState = GenericState.started;
	}
}
