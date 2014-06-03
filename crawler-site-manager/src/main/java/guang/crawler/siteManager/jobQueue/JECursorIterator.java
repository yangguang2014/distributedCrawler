package guang.crawler.siteManager.jobQueue;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.OperationStatus;

public class JECursorIterator<T> implements MapQueueIterator<T> {
	private final Cursor cursor;
	private DatabaseEntry key = null;
	private DatabaseEntry value = null;
	private final JEQueueElementTransfer<T> transfer;

	public JECursorIterator(Cursor cursor, JEQueueElementTransfer<T> transfer) {
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
			@SuppressWarnings("unused")
			DatabaseEntry key = new DatabaseEntry();
			this.value = new DatabaseEntry();
			OperationStatus result = this.cursor.getNext(this.key, this.value,
					null);
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
		this.key = this.value = null;
		return result;
	}

	@Override
	public void remove() {
		this.cursor.delete();

	}

}
