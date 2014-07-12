package guang.crawler.commons;

import java.util.HashMap;

/**
 * 表示一个URL.由于在爬虫爬取过程中,URL需要关联很多数据,因此创建了一个类专门用来表示一个用来爬取的URL.
 *
 * @author sun
 *
 */
public class WebURL {
	
	/**
	 * 创建一个新的URL
	 *
	 * @return
	 */
	public static WebURL newWebURL() {
		return new WebURL();
	}

	/**
	 * 实际的URL字符串
	 */
	private String	                url;
	/**
	 * 当前WebURL的文档ID，全局唯一
	 */
	private String	                docid;
	/**
	 * 当前URL的父URL的文档ID，如果没有，为NULL
	 */
	private String	                parentDocid;
	/**
	 * 当前URL的采集深度
	 */
	private short	                depth	            = -1;
	/**
	 * 当前URL如果是在<a>这种锚点中设置的，那么它的锚点信息存放在该变量中
	 */
	private String	                anchor;
	/**
	 * 该URL的采集优先级，暂时未被使用
	 */
	private byte	                priority;
	/**
	 * 该URL的子URL的数目，暂时未被使用
	 */
	private int	                    childNum;
	/**
	 * 当前URL所对应的站点管理器的ID。
	 */
	private String	                siteManagerId;
	/**
	 * 该URL开始爬行的时间
	 */
	private long	                startTime	        = -1;
	/**
	 * 该URL被尝试爬取的次数
	 */
	private int	                    tryTime	            = 0;
	/**
	 * 当前URL所对应的站点ID
	 */
	private String	                siteId;

	/**
	 * 当前URL的深度是否应当增加。这种情况出现在页面加载的是JS中的链接，如评论内容等深度不应当增加的情况。默认应当增加，可以设置为false
	 */
	private boolean	                shouldDepthIncrease	= true;

	/**
	 * 在处理该URL过程中设置的一些属性
	 */
	private HashMap<String, Object>	properties;

	private WebURL() {
		this.properties = new HashMap<String, Object>();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass())) {
			return false;
		}
		
		WebURL otherUrl = (WebURL) o;
		return (this.url != null) && this.url.equals(otherUrl.getURL());
		
	}

	/**
	 * 获取当前URL在页面中的锚点信息
	 *
	 * @return
	 */
	public String getAnchor() {
		return this.anchor;
	}

	/**
	 * 从当前URL所对应的页面中获取的需要进一步爬取的子URL的数量
	 *
	 * @return
	 */
	public int getChildNum() {
		return this.childNum;
	}

	/**
	 * 获取当前URL的深度
	 *
	 * @return
	 */
	public short getDepth() {
		return this.depth;
	}

	/**
	 * 获取当前URL的文档ID.
	 *
	 * @return
	 */
	public String getDocid() {
		return this.docid;
	}
	
	/**
	 * 获取当前URL父URL的文档ID.
	 *
	 * @return
	 */
	public String getParentDocid() {
		return this.parentDocid;
	}

	/**
	 * 获取当前URL的优先级.
	 *
	 * @return
	 */
	public byte getPriority() {
		return this.priority;
	}
	
	/**
	 * 获取当前URL关联的其他的一些属性
	 *
	 * @return
	 */
	public HashMap<String, Object> getProperties() {
		return this.properties;
	}
	
	/**
	 * 获取当前URL关联的某个属性
	 *
	 * @param key
	 * @return
	 */
	public Object getProperty(final String key) {
		return this.properties.get(key);
	}
	
	/**
	 * 获取当前URL对应的采集点的ID
	 *
	 * @return
	 */
	public String getSiteId() {
		return this.siteId;
	}
	
	/**
	 * 获取当前URL对应的站点管理器的ID.
	 *
	 * @return
	 */
	public String getSiteManagerId() {
		return this.siteManagerId;
	}
	
	/**
	 * 获取当前URL被采集的时间
	 *
	 * @return
	 */
	public long getStartTime() {
		return this.startTime;
	}
	
	/**
	 * 获取当前URL被尝试采集的次数.
	 *
	 * @return
	 */
	public int getTryTime() {
		return this.tryTime;
	}

	/**
	 * 获取当前URL对应的URL字符串值
	 *
	 * @return
	 */
	public String getURL() {
		return this.url;
	}
	
	@Override
	public int hashCode() {
		return this.url.hashCode();
	}
	
	/**
	 * 增加当前URL的尝试次数.
	 *
	 * @return
	 */
	public WebURL increaseTryTime() {
		this.tryTime++;
		return this;
	}
	
	/**
	 * 当前URL的深度是否应当增加.默认是需要增加的,但是某些情况下,例如页面的JSON数据,那么它是附属于当前页面的,那么就不应当增加深度.
	 *
	 * @return
	 */
	public boolean isShouldDepthIncrease() {
		return this.shouldDepthIncrease;
	}
	
	/**
	 * 重置当前URL的尝试次数.
	 * 
	 * @return
	 */
	public WebURL resetTryTime() {
		this.tryTime = 0;
		return this;
	}
	
	/**
	 * 设置当前URL的锚点信息
	 * 
	 * @param anchor
	 * @return
	 */
	public WebURL setAnchor(final String anchor) {
		this.anchor = anchor;
		return this;
	}
	
	/**
	 * 设置当前URL的子URL的数量.
	 * 
	 * @param childNum
	 * @return
	 */
	public WebURL setChildNum(final int childNum) {
		this.childNum = childNum;
		return this;
	}
	
	/**
	 * 设置当前URL的深度
	 * 
	 * @param depth
	 * @return
	 */
	public WebURL setDepth(final short depth) {
		this.depth = depth;
		return this;
	}
	
	/**
	 * 设置当前URL的文档ID.
	 * 
	 * @param docid
	 * @return
	 */
	public WebURL setDocid(final String docid) {
		this.docid = docid;
		return this;
	}
	
	/**
	 * 设置当前URL的父URL的文档ID.
	 * 
	 * @param parentDocid
	 * @return
	 */
	public WebURL setParentDocid(final String parentDocid) {
		this.parentDocid = parentDocid;
		return this;
	}
	
	/**
	 * 设置当前URL的优先级.
	 * 
	 * @param priority
	 * @return
	 */
	public WebURL setPriority(final byte priority) {
		this.priority = priority;
		return this;
	}
	
	/**
	 * 给当前URL设置一些属性
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public WebURL setProperty(final String key, final Object value) {
		this.properties.put(key, value);
		return this;
	}
	
	/**
	 * 设置当前URL是否应当增加深度
	 * 
	 * @param shouldDepthIncrease
	 * @return
	 */
	public WebURL setShouldDepthIncrease(final boolean shouldDepthIncrease) {
		this.shouldDepthIncrease = shouldDepthIncrease;
		return this;
	}
	
	/**
	 * 设置当前URL的采集点的ID.
	 * 
	 * @param siteId
	 * @return
	 */
	public WebURL setSiteId(final String siteId) {
		this.siteId = siteId;
		return this;
	}
	
	/**
	 * 设置当前采集点的站点管理器的ID.
	 * 
	 * @param siteManagerId
	 * @return
	 */
	public WebURL setSiteManagerId(final String siteManagerId) {
		this.siteManagerId = siteManagerId;
		return this;
	}
	
	/**
	 * 设置当前WebURL的URL字符串.
	 * 
	 * @param url
	 * @return
	 */
	public WebURL setURL(final String url) {
		this.url = url;
		return this;
	}
	
	/**
	 * 设置当前URL开始采集的时间.
	 */
	public WebURL startTime(final long startTime) {
		this.startTime = startTime;
		return this;
	}
	
	@Override
	public String toString() {
		return this.url;
	}
	
}
