package guang.crawler.siteManager.jobQueue;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.OperationStatus;

/**
 * 在JEQueue中遍历元素
 *
 * @author sun
 *
 * @param <T>
 */
public class JECursorIterator<T> implements MapQueueIterator<T> {
	/**
	 * JEQueue的游标
	 */
	private final Cursor	                cursor;
	/**
	 * 下一个需要获取的元素
	 */
	private DatabaseEntry	                value	= null;
	/**
	 * 数据元素转换器
	 */
	private final JEQueueElementTransfer<T>	transfer;
	
	public JECursorIterator(final Cursor cursor,
	        final JEQueueElementTransfer<T> transfer) {
		this.cursor = cursor;
		this.transfer = transfer;
	}
	
	@Override
	public void close() {
		this.cursor.close();
		
	}
	
	/**
	 * 一直返回true，因此该方法是不被支持的
	 */
	@Override
	public boolean hasNext() {
		if (this.value != null) {
			return true;
		} else {
			DatabaseEntry key = new DatabaseEntry();
			this.value = new DatabaseEntry();
			OperationStatus result = this.cursor.getNext(key, this.value, null);
			if (result == OperationStatus.SUCCESS) {
				return true;
			} else {
				this.value = null;
				return false;
			}
		}
	}
	
	@Override
	public T next() {
		T result = null;
		if (this.hasNext()) {
			result = this.transfer.entryToObject(this.value);
		}
		this.value = null;
		return result;
	}
	
	@Override
	public void remove() {
		this.cursor.delete();
		
	}
	
}
