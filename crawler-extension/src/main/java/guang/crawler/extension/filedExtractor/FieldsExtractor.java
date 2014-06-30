package guang.crawler.extension.filedExtractor;

import guang.crawler.commons.Page;

public interface FieldsExtractor {
	/**
	 * 从页中抽取所需的数据存储到HBase中
	 *
	 * @param page
	 *            当前正在处理的页面
	 */
	public void extractFields(Page page);
}
