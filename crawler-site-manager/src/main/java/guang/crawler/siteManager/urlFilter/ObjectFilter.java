package guang.crawler.siteManager.urlFilter;

public interface ObjectFilter
{
	
	public boolean contains(Object object);
	
	public boolean containsAndSet(Object object);
	
	public void fromBackupString(String data);
	
	public String toBackupString();
	
}