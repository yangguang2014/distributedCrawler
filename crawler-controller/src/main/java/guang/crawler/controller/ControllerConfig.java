package guang.crawler.controller;

import guang.crawler.localConfig.LocalConfig;
import guang.crawler.util.PropertiesHelper;

/**
 * 控制器的配置类.除了使用默认配置之外,它还添加了自己的配置文件和属性.
 *
 * @author sun
 *
 */
public class ControllerConfig extends LocalConfig {

	private static ControllerConfig	config;

	public static ControllerConfig me() {
		if (ControllerConfig.config == null) {
			ControllerConfig.config = new ControllerConfig();
		}
		return ControllerConfig.config;
	}
	
	/**
	 * 当前类是否被初始化了.
	 */
	private boolean	      inited	                    = false;
	/**
	 * 启动webservice建议的端口号
	 */
	private int	          webserviceSuggestPort	        = 9876;

	/**
	 * 属性的key,表示启动webservice时建议的端口号.
	 */
	private static String	KEY_WEBSERVICE_SUGGEST_PORT	= "crawler.controller.webservice.port.suggest";

	private ControllerConfig() {
	}

	/**
	 * 添加了自己的配置文件
	 */
	@Override
	protected String[] getConfigResources() {
		return new String[] { "/conf/crawler-controller/controller.conf" };
	}

	/**
	 * 获取建议的配置webservice的端口.
	 *
	 * @return
	 */
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
		this.webserviceSuggestPort = PropertiesHelper.readInt(this.configProperties,
		                                                      ControllerConfig.KEY_WEBSERVICE_SUGGEST_PORT,
		                                                      this.webserviceSuggestPort);
	}

}
