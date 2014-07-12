package guang.crawler.launcher;

import guang.crawler.localConfig.LocalConfig;
import guang.crawler.util.PropertiesHelper;

/**
 * 启动起的配置
 * 
 * @author sun
 *
 */
public class LauncherConfig extends LocalConfig {
	
	private static LauncherConfig	config;
	
	public static LauncherConfig me() {
		if (LauncherConfig.config == null) {
			LauncherConfig.config = new LauncherConfig();
		}
		return LauncherConfig.config;
	}
	
	/**
	 * 配置文件中读取的需要启动的角色
	 */
	private String	roles[];
	
	private LauncherConfig() {
	}
	
	@Override
	protected String[] getConfigResources() {
		return new String[] { "/conf/crawler-launcher/launcher.conf" };
	}
	
	public String[] getRoles() {
		return this.roles;
	}
	
	public LauncherConfig init() {
		return this;
	}
	
	@Override
	protected void initProperties() {
		super.initProperties();
		String roleP = PropertiesHelper.readString(this.configProperties,
		                                           "crawler.roles",
		                                           "site,worker");
		this.roles = roleP.split(",");
	}
}
