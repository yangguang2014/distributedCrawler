package guang.crawler.localConfig;

import guang.crawler.util.PropertiesHelper;

import java.io.File;
import java.util.Properties;

/**
 * 从本地配置文件中加载属性
 *
 * @author sun
 *
 */
public abstract class LocalConfig {
	/**
	 * 爬虫项目的home目录
	 */
	private String	     crawlerHome;
	/**
	 * 从配置文件中加载的属性
	 */
	protected Properties	configProperties;
	/**
	 * 爬虫系统使用的Zookeeper的地址
	 */
	private String	     zookeeperQuorum;
	/**
	 * 爬虫系统使用hadoop系统时的用户名称
	 */
	private String	     hadoopUser;
	/**
	 * 爬虫系统使用的hadoop系统的连接URL
	 */
	private String	     hadoopURL;
	/**
	 * 爬虫系统使用的hadoop系统中文件系统的根目录.
	 */
	private String	     hadoopPath	= "/home/crawler";

	/**
	 * 创建一个本地配置对象,从配置文件中加载属性
	 */
	public LocalConfig() {
		this.initConfigResources();
		this.initProperties();
	}

	/**
	 * 用户实现该方法,返回一个数组,数组中是用户需要额外加载的配置属性.
	 *
	 * @return
	 */
	protected abstract String[] getConfigResources();

	/**
	 * 获取系统的home目录
	 *
	 * @return
	 */
	public String getCrawlerHome() {
		return this.crawlerHome;
	}

	/**
	 * 获取系统在hadoop中的根目录
	 *
	 * @return
	 */
	public String getHadoopPath() {
		return this.hadoopPath;
	}

	/**
	 * 获取hadoop连接URL
	 *
	 * @return
	 */
	public String getHadoopURL() {
		return this.hadoopURL;
	}

	/**
	 * 获取连接hadoop的用户名称
	 *
	 * @return
	 */
	public String getHadoopUser() {
		return this.hadoopUser;
	}

	/**
	 * 获取连接Zookeeper的连接地址
	 *
	 * @return
	 */
	public String getZookeeperQuorum() {
		return this.zookeeperQuorum;
	}

	/**
	 * 初始化配置信息,从配置文件中加载属性.默认会加载${crawler home}/conf/crawler.config中的配置信息.
	 */
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

	/**
	 * 设置当前类中的一些属性值.
	 */
	protected void initProperties() {

		this.hadoopUser = PropertiesHelper.readString(this.configProperties,
		                                              "crawler.hadoop.user",
		                                              System.getProperty("user.name"));
		System.setProperty("HADOOP_USER_NAME", this.hadoopUser);
		this.hadoopURL = PropertiesHelper.readString(this.configProperties,
		                                             "crawler.hadoop.url", null);
		this.hadoopPath = PropertiesHelper.readString(this.configProperties,
		                                              "crawler.hadoop.path",
		                                              this.hadoopPath);
		this.zookeeperQuorum = PropertiesHelper.readString(this.configProperties,
		                                                   "crawler.zookeeper.quorum",
		                                                   null);
	}
}
