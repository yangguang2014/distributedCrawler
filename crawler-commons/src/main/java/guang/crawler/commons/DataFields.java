package guang.crawler.commons;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * 需要存储在HBase中的数据域的集合
 *
 * @author sun
 *
 */
public class DataFields {
	/**
	 * key是应当存储在HBase中的数据的主键，value是该行中的相关数据。
	 */
	private HashMap<String, LinkedList<DataField>>	fileds;

	public DataFields() {
		this.fileds = new HashMap<String, LinkedList<DataField>>();
	}

	/**
	 * 添加一个需要存储的数据域.
	 *
	 * @param key
	 * @param dataFamily
	 * @param columnName
	 * @param data
	 */
	public DataFields addFiled(final String key, final String dataFamily,
	        final String columnName, final String data) {
		LinkedList<DataField> filedList = this.fileds.get(key);
		if (filedList == null) {
			filedList = new LinkedList<DataField>();
			this.fileds.put(key, filedList);
		}
		DataField filed = new DataField(dataFamily, columnName, data);
		filedList.add(filed);
		return this;
	}
	
	/**
	 * 获取当前对象中存储的所有数据域.
	 * 
	 * @return
	 */
	public HashMap<String, LinkedList<DataField>> getAllFileds() {
		return this.fileds;
	}
	
}