package guang.crawler.centerConfig;

import java.io.IOException;

public class ClearCenterConfig
{
	public static void main(String[] args) throws IOException,
	        InterruptedException
	{
		CenterConfig.me().init("ubuntu-3,ubuntu-6,ubuntu-8").clear();
		CenterConfig.me().initPath();
		CenterConfig.me().shutdown();
	}
}
