package guang.crawler.commons.service;

/**
 * 当前采集点的状态
 * 
 * @author sun
 *
 */
public enum SiteStatus {
	/**
	 * 当前采集点器处于enabled状态
	 */
	enabled,
	/**
	 * 当前采集点被关闭了
	 */
	disabled,
	/**
	 * 当前采集点正在被爬取
	 */
	running,
	/**
	 * 采集点不存在
	 */
	notexist,
	/**
	 * 其他的错误状态.
	 */
	error
}
