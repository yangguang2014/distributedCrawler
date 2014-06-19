package guang.crawler.siteManager.urlFilter;

public interface ObjectFilter
{
	/**
	 * 检查是否包含某个对象
	 * @param object
	 * @return
	 */
	public boolean contains(Object object);
	/**
	 * 检查是否包含某个对象，如果没有包含，那么添加该对象
	 * @param object
	 * @return
	 */
	public boolean containsAndSet(Object object);
	/**
	 * 从备份字符串中恢复过滤器
	 * @param data
	 */
	public void fromBackupString(String data);
	/**
	 * 将当前对象序列化为一个字符串
	 * @return
	 */
	public String toBackupString();
	
}