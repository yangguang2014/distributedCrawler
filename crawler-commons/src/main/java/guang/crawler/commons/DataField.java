package guang.crawler.commons;

/**
 * 存储在HBase中的某列数据,称为一个数据域,其中包含着要存入的列簇,列名和将要存入的数据.
 * 
 * @author sun
 *
 */
public class DataField {
	/**
	 * 存储位置的簇
	 */
	private String	dataFamily;
	/**
	 * 存储位置的列名
	 */
	private String	columnName;
	/**
	 * 实际存储的数据
	 */
	private String	data;
	
	public DataField() {
	}
	
	public DataField(final String dataFamily, final String columnName,
	        final String data) {
		this.dataFamily = dataFamily;
		this.columnName = columnName;
		this.data = data;
	}

	public String getColumnName() {
		return this.columnName;
	}

	public String getData() {
		return this.data;
	}

	public String getDataFamily() {
		return this.dataFamily;
	}

	public DataField setColumnName(final String columnName) {
		this.columnName = columnName;
		return this;
	}
	
	public DataField setData(final String data) {
		this.data = data;
		return this;
	}
	
	public DataField setDataFamily(final String dataFamily) {
		this.dataFamily = dataFamily;
		return this;
	}

}
