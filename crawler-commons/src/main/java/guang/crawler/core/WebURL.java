package guang.crawler.core;

public class WebURL
{
	
	private String	url;
	private String	docid;
	private int	   parentDocid;
	private short	depth;
	private String	anchor;
	private byte	priority;
	private int	   childNum;
	private String	siteManagerName;
	
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
	
	public String getSiteManagerName()
	{
		return this.siteManagerName;
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
	
	public void setSiteManagerName(String siteManagerName)
	{
		this.siteManagerName = siteManagerName;
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
