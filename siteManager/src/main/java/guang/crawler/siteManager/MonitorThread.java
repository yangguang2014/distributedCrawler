package guang.crawler.siteManager;

import java.util.Scanner;

public class MonitorThread extends Thread
{
	private SiteManager	server;
	
	public MonitorThread(SiteManager server)
	{
		this.server = server;
	}
	
	@Override
	public void run()
	{
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNextLine())
		{
			String line = scanner.nextLine().trim();
			if ("exit".equalsIgnoreCase(line))
			{
				System.out.println("[INFO] System will exit!");
				this.server.shutdown();
				scanner.close();
				System.out.println("[INFO] System exited!");
				break;
			}
		}
	}
}
