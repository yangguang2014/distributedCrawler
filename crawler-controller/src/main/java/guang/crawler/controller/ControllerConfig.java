package guang.crawler.controller;

import guang.crawler.localConfig.LocalConfig;
import guang.crawler.util.PropertiesHelper;

public class ControllerConfig extends LocalConfig {

	private static ControllerConfig config;

	public static ControllerConfig me() {
		if (ControllerConfig.config == null) {
			ControllerConfig.config = new ControllerConfig();
		}
		return ControllerConfig.config;
	}

	private boolean inited = false;
	/**
	 * 启动webservice建议的端口号
	 */
	private int webserviceSuggestPort = 9876;

	private static String KEY_WEBSERVICE_SUGGEST_PORT = "crawler.controller.webservice.port.suggest";

	private ControllerConfig() {
	}

	@Override
	protected String[] getConfigResources() {
		return new String[] { "/conf/crawler-controller/controller.conf" };
	}

	public int getWebserviceSuggestPort() {
		return this.webserviceSuggestPort;
	}

	public synchronized ControllerConfig init() {
		if (this.inited) {
			return this;
		}
		this.inited = true;
		return this;
	}

	@Override
	protected void initProperties() {
		super.initProperties();
		this.webserviceSuggestPort = PropertiesHelper.readInt(
				this.configProperties,
				ControllerConfig.KEY_WEBSERVICE_SUGGEST_PORT,
				this.webserviceSuggestPort);
	}

}
