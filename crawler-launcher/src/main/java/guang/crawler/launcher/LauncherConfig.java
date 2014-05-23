package guang.crawler.launcher;

import guang.crawler.util.PropertiesHelper;

import java.io.File;
import java.util.Properties;

public class LauncherConfig {

	private static LauncherConfig config;

	public static LauncherConfig me() {
		if (LauncherConfig.config == null) {
			LauncherConfig.config = new LauncherConfig();
		}
		return LauncherConfig.config;
	}

	private String crawlerHome;
	private Properties configProperties;
	private String zookeeperQuorum;

	private String roles[];

	private LauncherConfig() {
	}

	public String[] getRoles() {
		return this.roles;
	}

	public String getZookeeperQuorum() {
		return this.zookeeperQuorum;
	}

	public LauncherConfig init() {
		this.crawlerHome = System.getenv("CRAWLER_HOME");
		if (this.crawlerHome == null) {
			this.crawlerHome = System.getProperty("user.dir");
			System.getenv().put("CRAWLER_HOME", this.crawlerHome);
		}
		System.setProperty("crawler.home", this.crawlerHome);
		this.initProperties();
		return this;
	}

	private void initProperties() {
		this.configProperties = new Properties();
		PropertiesHelper.loadConfigFile(new File(this.crawlerHome
				+ "/conf/crawler.config"), this.configProperties);
		PropertiesHelper.loadConfigFile(new File(this.crawlerHome
				+ "/conf/crawler-launcher/launcher.conf"),
				this.configProperties);
		this.zookeeperQuorum = PropertiesHelper.readString(
				this.configProperties, "crawler.zookeeper.quorum", null);
		String roleP = PropertiesHelper.readString(this.configProperties,
				"crawler.roles", "site,worker");
		this.roles = roleP.split(",");
	}
}
