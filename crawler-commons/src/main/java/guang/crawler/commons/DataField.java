package guang.crawler.commons;

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

	public void setColumnName(final String columnName) {
		this.columnName = columnName;
	}
	
	public void setData(final String data) {
		this.data = data;
	}
	
	public void setDataFamily(final String dataFamily) {
		this.dataFamily = dataFamily;
	}

}
