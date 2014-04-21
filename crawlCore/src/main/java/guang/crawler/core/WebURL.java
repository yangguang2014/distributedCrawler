package guang.crawler.core;

/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */

public class WebURL
{
	
	private String	url;
	private int	   docid;
	private int	   parentDocid;
	private short	depth;
	private String	anchor;
	private byte	priority;
	private int	   childNum;
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass()))
		{
			return false;
		}
		
		WebURL otherUrl = (WebURL) o;
		return (this.url != null) && this.url.equals(otherUrl.getURL());
		
	}
	
	/**
	 * Returns the anchor string. For example, in <a href="example.com">A sample
	 * anchor</a> the anchor string is 'A sample anchor'
	 */
	public String getAnchor()
	{
		return this.anchor;
	}
	
	public int getChildNum()
	{
		return this.childNum;
	}
	
	/**
	 * Returns the crawl depth at which this Url is first observed. Seed Urls
	 * are at depth 0. Urls that are extracted from seed Urls are at depth 1,
	 * etc.
	 */
	public short getDepth()
	{
		return this.depth;
	}
	
	/**
	 * Returns the unique document id assigned to this Url.
	 */
	public int getDocid()
	{
		return this.docid;
	}
	
	/**
	 * Returns the unique document id of the parent page. The parent page is the
	 * page in which the Url of this page is first observed.
	 */
	public int getParentDocid()
	{
		return this.parentDocid;
	}
	
	/**
	 * Returns the priority for crawling this URL. A lower number results in
	 * higher priority.
	 */
	public byte getPriority()
	{
		return this.priority;
	}
	
	/**
	 * Returns the Url string
	 */
	public String getURL()
	{
		return this.url;
	}
	
	@Override
	public int hashCode()
	{
		return this.url.hashCode();
	}
	
	public void setAnchor(String anchor)
	{
		this.anchor = anchor;
	}
	
	public void setChildNum(int childNum)
	{
		this.childNum = childNum;
	}
	
	public void setDepth(short depth)
	{
		this.depth = depth;
	}
	
	public void setDocid(int docid)
	{
		this.docid = docid;
	}
	
	public void setParentDocid(int parentDocid)
	{
		this.parentDocid = parentDocid;
	}
	
	public void setPriority(byte priority)
	{
		this.priority = priority;
	}
	
	public void setURL(String url)
	{
		this.url = url;
	}
	
	@Override
	public String toString()
	{
		return this.url;
	}
	
}
