package guang.crawler.siteManager.jobQueue;

import java.util.List;

/**
 * 结合Map和Queue特点的数据结构。该数据结构拥有自己的key,能够通过键值来删除指定的元素;另外,能够通过队列的操作,在数据结构的一端插入和获取元素.
 *
 * @author yang
 * @param <T>
 */
public abstract class MapQueue<T> {
	/**
	 * 工作队列一旦关闭，就不允许再对其进行其他的操作
	 */
	public abstract void close();

	/**
	 * 删除队列中指定数量的若干元素，返回实际删除的元素的个数
	 */
	public abstract boolean delete(T element);

	/**
	 * 获取最多max个元素
	 */
	public abstract List<T> get(int max);

	/**
	 * 获取队列的长度
	 */
	public abstract long getLength();

	/**
	 * 依次遍历队列中的资源
	 *
	 * @return
	 */
	public abstract MapQueueIterator<T> iterator();

	/**
	 * 向队列中添加一个元素
	 */
	public abstract void put(T element);

}
