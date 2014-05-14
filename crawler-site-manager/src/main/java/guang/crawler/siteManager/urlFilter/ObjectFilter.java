package guang.crawler.siteManager.urlFilter;

public interface ObjectFilter
{
	
	public abstract boolean contains(Object object);
	
	public abstract boolean containsAndSet(Object object);
	
}