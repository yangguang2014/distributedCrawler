package guang.crawler.localConfig;

import guang.crawler.util.PropertiesHelper;

import java.io.File;
import java.util.Properties;

public abstract class LocalConfig {
	private String crawlerHome;
	protected Properties configProperties;
	private String zookeeperQuorum;
	private String hadoopUser;
	private String hadoopURL;
	private String hadoopPath = "/home/crawler";

	public LocalConfig() {
		this.initConfigResources();
		this.initProperties();
	}

	protected abstract String[] getConfigResources();

	public String getCrawlerHome() {
		return this.crawlerHome;
	}

	public String getHadoopPath() {
		return this.hadoopPath;
	}

	public String getHadoopURL() {
		return this.hadoopURL;
	}

	public String getHadoopUser() {
		return this.hadoopUser;
	}

	public String getZookeeperQuorum() {
		return this.zookeeperQuorum;
	}

	private void initConfigResources() {
		this.crawlerHome = System.getenv("CRAWLER_HOME");
		if (this.crawlerHome == null) {
			this.crawlerHome = System.getProperty("crawler.home");
			if (this.crawlerHome == null) {
				this.crawlerHome = System.getProperty("user.dir");
			}
		}
		System.setProperty("crawler.home", this.crawlerHome);
		this.configProperties = new Properties();
		PropertiesHelper.loadConfigFile(new File(this.crawlerHome
				+ "/conf/crawler.config"), this.configProperties);
		String[] configResources = this.getConfigResources();
		if ((configResources == null) || (configResources.length == 0)) {
			return;
		}
		for (String configResource : configResources) {
			PropertiesHelper.loadConfigFile(new File(this.crawlerHome
					+ configResource), this.configProperties);
		}
	}

	protected void initProperties() {

		this.hadoopUser = PropertiesHelper.readString(this.configProperties,
				"crawler.hadoop.user", System.getProperty("user.name"));
		System.setProperty("HADOOP_USER_NAME", this.hadoopUser);
		this.hadoopURL = PropertiesHelper.readString(this.configProperties,
				"crawler.hadoop.url", null);
		this.hadoopPath = PropertiesHelper.readString(this.configProperties,
				"crawler.hadoop.path", this.hadoopPath);
		this.zookeeperQuorum = PropertiesHelper.readString(
				this.configProperties, "crawler.zookeeper.quorum", null);
	}
}
