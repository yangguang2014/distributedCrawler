package guang.crawler.siteManager.jobQueue.je;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.DatabaseEntry;

/**
 * 所有的队列元素都应当实现其与数据库元素之间的转换。
 * 
 * @author yang
 * @param <T>
 */
public abstract class JEQueueElementTransfer<T> extends TupleBinding<T>
{
	public abstract DatabaseEntry getDatabaseEntryKey(T data);
}
