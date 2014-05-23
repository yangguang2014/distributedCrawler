package guang.crawler.jsonServer;

import java.net.InetAddress;

public interface JsonServer
{
	
	public InetAddress getAddress();
	
	public int getPort();
	
	public boolean isShutdown();
	
	public void shutdown();
	
	public boolean start();
	
	public void waitForStop();
	
}