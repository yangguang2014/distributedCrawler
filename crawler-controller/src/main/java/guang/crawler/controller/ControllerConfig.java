package guang.crawler.controller;

import guang.crawler.localConfig.LocalConfig;

public class ControllerConfig extends LocalConfig {

	private static ControllerConfig config;

	public static ControllerConfig me() {
		if (ControllerConfig.config == null) {
			ControllerConfig.config = new ControllerConfig();
		}
		return ControllerConfig.config;
	}

	private boolean inited = false;

	private ControllerConfig() {
	}

	@Override
	protected String[] getConfigResources() {
		return new String[] { "/conf/crawler-controller/controller.conf" };
	}

	public synchronized ControllerConfig init() {
		if (this.inited) {
			return this;
		}
		this.inited = true;
		return this;
	}

}
