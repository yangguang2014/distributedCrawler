package guang.crawler.siteManager.jobQueue;

import java.io.Closeable;
import java.util.Iterator;

public interface MapQueueIterator<T> extends Iterator<T>, Closeable
{
	@Override
	public void close();
}
