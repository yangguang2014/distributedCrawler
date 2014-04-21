package guang.crawler.jsonServer;

public interface JsonServer
{
	
	public abstract boolean isShutdown();
	
	public abstract void shutdown();
	
	public abstract boolean start();
	
	public abstract void waitForStop();
	
}