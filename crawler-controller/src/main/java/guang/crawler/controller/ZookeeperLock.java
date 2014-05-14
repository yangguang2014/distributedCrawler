package guang.crawler.controller;

//TODO 这个类应当仔细设计，以满足实际需要。
public class ZookeeperLock
{
	public static void main(String[] args)
	{
	}
	
	private String	lockPath;
	
	public ZookeeperLock(String lockPath)
	{
		this.lockPath = lockPath;
		
	}
	
	public boolean lock()
	{
		return false;
	}
	
	public boolean unlock()
	{
		return false;
	}
	
}
