package guang.crawler.statistics;

import guang.crawler.localConfig.LocalConfig;
import guang.crawler.util.PropertiesHelper;

public class StatisticsConfig extends LocalConfig {

	private static StatisticsConfig statisticsConfig;

	public static StatisticsConfig me() {
		if (StatisticsConfig.statisticsConfig == null) {
			StatisticsConfig.statisticsConfig = new StatisticsConfig();
		}
		return StatisticsConfig.statisticsConfig;
	}

	private long timeInterval = 1000;

	private StatisticsConfig() {
	}

	@Override
	protected String[] getConfigResources() {
		return new String[] { "/conf/crawler-statistics/crawler-statistics.config" };
	}

	public long getTimeInterval() {
		return this.timeInterval;
	}

	public StatisticsConfig init() {
		return this;
	}

	@Override
	protected void initProperties() {
		super.initProperties();
		this.timeInterval = PropertiesHelper.readLong(this.configProperties,
				"crawler.statistics.timeInterval", this.timeInterval);
	}

}
