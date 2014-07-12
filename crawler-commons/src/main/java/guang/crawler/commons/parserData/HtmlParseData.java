package guang.crawler.commons.parserData;

import guang.crawler.commons.WebURL;

import java.util.List;

/**
 * 如果解析得到的数据类型是HTML，那么使用这种数据类型
 *
 * @author yang
 */
public class HtmlParseData implements ParseData {
	/**
	 * HTML页面字符串
	 */
	private String	     html;
	/**
	 * 当前页面的标题
	 */
	private String	     title;
	/**
	 * 当前页面中去除各种标签之后的文字信息
	 */
	private String	     text;
	/**
	 * 当前静态页面中抽取的URL列表
	 */
	private List<WebURL>	outgoingUrls;

	/**
	 * 获取HTML字符串
	 * 
	 * @return
	 */
	public String getHtml() {
		return this.html;
	}

	/**
	 * 获取从html页面静态内容中抽取的URL列表.
	 * 
	 * @return
	 */
	public List<WebURL> getOutgoingUrls() {
		return this.outgoingUrls;
	}

	/**
	 * 获取html页面中去除了标签等信息之后的纯文本信息.
	 * 
	 * @return
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * 获取当前html页面的标题.
	 * 
	 * @return
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * 设置html字符串.
	 * 
	 * @param html
	 */
	public void setHtml(final String html) {
		this.html = html;
	}

	/**
	 * 设置从当前页面中抽取的静态URL列表.
	 * 
	 * @param outgoingUrls
	 */
	public void setOutgoingUrls(final List<WebURL> outgoingUrls) {
		this.outgoingUrls = outgoingUrls;
	}

	/**
	 * 设置当前页面中去除了标签之后的纯文本信息.
	 * 
	 * @param text
	 */
	public void setText(final String text) {
		this.text = text;
	}

	/**
	 * 设置当前页面的标题
	 * 
	 * @param title
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return this.html;
	}

}
