package guang.crawler.controller;

import guang.crawler.centerConfig.CenterConfig;
import guang.crawler.commons.GenericState;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

public class CrawlerController {

	public static CrawlerController controller;

	public static CrawlerController me() {
		if (CrawlerController.controller == null) {
			CrawlerController.controller = new CrawlerController();
		}
		return CrawlerController.controller;
	}

	private ControllerConfig controllerConfig;

	private GenericState controllerState;

	private CrawlerController() {
		this.controllerState = GenericState.created;
	}

	public CrawlerController init() throws IOException, InterruptedException {
		if (this.controllerState != GenericState.created) {
			return this;
		}
		this.controllerConfig = ControllerConfig.me().init();
		CenterConfig.me().init(this.controllerConfig.getZookeeperQuorum());
		this.controllerState = GenericState.inited;
		return this;
	}

	public void start() throws InterruptedException, KeeperException {
		// 首先检查控制器当前的状态
		if (this.controllerState == GenericState.started) {
			System.out.println("Controller Already started...");
			return;
		} else if (this.controllerState.getState() < GenericState.inited
				.getState()) {
			throw new IllegalStateException(
					"Controller Should be inited first!");
		}
		// 尝试监听控制器的角色，尝试竞争该角色，如果竞争成功，就启动工作线程，结束当前线程。
		ControllerManagerWatcher.me().start();
		// 即使没有获得角色，也应当设置为started状态，因为后续的过程是通过中断来引导的了。
		this.controllerState = GenericState.started;
	}
}
