package guang.crawler.commons;

public class WebURL
{
	
	private String	url;
	private String	docid;
	private int	   parentDocid;
	private short	depth;
	private String	anchor;
	private byte	priority;
	private int	   childNum;
	private String	siteManagerId;
	/**
	 * 该URL开始爬行的时间
	 */
	private long	startTime	= -1;
	/**
	 * 该URL被尝试爬取的次数
	 */
	private int	   tryTime	  = 0;
	
	private String	siteId;
	
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
	
	public String getAnchor()
	{
		return this.anchor;
	}
	
	public int getChildNum()
	{
		return this.childNum;
	}
	
	public short getDepth()
	{
		return this.depth;
	}
	
	public String getDocid()
	{
		return this.docid;
	}
	
	public int getParentDocid()
	{
		return this.parentDocid;
	}
	
	public byte getPriority()
	{
		return this.priority;
	}
	
	public String getSiteId()
	{
		return this.siteId;
	}
	
	public String getSiteManagerId()
	{
		return this.siteManagerId;
	}
	
	public long getStartTime()
	{
		return this.startTime;
	}
	
	public int getTryTime()
	{
		return this.tryTime;
	}
	
	public String getURL()
	{
		return this.url;
	}
	
	@Override
	public int hashCode()
	{
		return this.url.hashCode();
	}
	
	public WebURL increaseTryTime()
	{
		this.tryTime++;
		return this;
	}
	
	public WebURL resetTryTime()
	{
		this.tryTime = 0;
		return this;
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
	
	public void setDocid(String docid)
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
	
	public void setSiteId(String siteId)
	{
		this.siteId = siteId;
	}
	
	public void setSiteManagerId(String siteManagerId)
	{
		this.siteManagerId = siteManagerId;
	}
	
	public void setURL(String url)
	{
		this.url = url;
	}
	
	public WebURL startTime(long startTime)
	{
		this.startTime = startTime;
		return this;
	}
	
	@Override
	public String toString()
	{
		return this.url;
	}
	
}
