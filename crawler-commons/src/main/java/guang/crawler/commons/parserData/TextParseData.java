package guang.crawler.commons.parserData;

/**
 * 如果是纯文本数据，那么解析的结果就是这种数据类型
 *
 * @author yang
 */
public class TextParseData implements ParseData {
	/**
	 * 文字内容
	 */
	private String	textContent;
	
	/**
	 * 获取文字内容
	 * 
	 * @return
	 */
	public String getTextContent() {
		return this.textContent;
	}
	
	/**
	 * 设置文字内容
	 * 
	 * @param textContent
	 */
	public void setTextContent(final String textContent) {
		this.textContent = textContent;
	}
	
	@Override
	public String toString() {
		return this.textContent;
	}
	
}
