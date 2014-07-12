package guang.crawler.siteManager.jobQueue;

/**
 * 同步接口.有些数据结构是需要将内存中的数据与磁盘中的数据同步的.
 * 
 * @author sun
 *
 */
public interface Sync {
	/**
	 * 将内存中的数据与磁盘中的数据做同步.
	 */
	public void sync();
}