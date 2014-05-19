package guang.crawler.launcher;

import guang.crawler.crawlWorker.WorkerMain;
import guang.crawler.siteManager.SiteManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main
{
	public static void main(String[] args)
	{
		String userDir = System.getenv("CRAWLER_HOME");
		if (userDir == null)
		{
			userDir = System.getProperty("user.dir");
			System.getenv().put("CRAWLER_HOME", userDir);
		}
		System.setProperty("crawler.home", userDir);
		File propertyFile = new File(userDir + "/conf/launcher.ini");
		Properties initProperties = new Properties();
		if (propertyFile.exists())
		{
			try
			{
				initProperties.load(new FileInputStream(propertyFile));
			} catch (IOException e)
			{
				// ignore this exception
				System.out
				.println("[Warning] failed to load launcher init file: "
						+ e.getMessage());
			}
		}
		String roleP = initProperties.getProperty("crawler.roles");
		if (roleP == null)
		{
			roleP = "site,worker";
		}
		String roles[] = roleP.split(",");
		for (String role : roles)
		{
			if ("site".equals(role))
			{
				// 这里启动站点管理器
				new Thread()
				{
					@Override
					public void run()
					{
						try
						{
							SiteManager.me().init().start();
						} catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();
			} else if ("worker".equals(role))
			{
				// 这里启动爬虫工作者
				new Thread()
				{
					@Override
					public void run()
					{
						try
						{
							WorkerMain.main(null);
						} catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();
			}
		}

	}

}
