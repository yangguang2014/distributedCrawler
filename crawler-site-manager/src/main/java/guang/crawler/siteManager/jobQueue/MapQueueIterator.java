package guang.crawler.siteManager.jobQueue;

import java.io.Closeable;
import java.util.Iterator;

/**
 * 在MapQueue中进行遍历的遍历器,其中提供了close接口,用于在遍历完成之后关闭相关资源.
 *
 * @author sun
 *
 * @param <T>
 */
public interface MapQueueIterator<T> extends Iterator<T>, Closeable {
	/**
	 * 可能有些遍历器的实现是需要关闭某些资源的.在这里进行关闭.
	 */
	@Override
	public void close();
}
