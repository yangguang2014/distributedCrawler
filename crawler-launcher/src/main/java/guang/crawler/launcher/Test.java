package guang.crawler.launcher;

import java.io.IOException;
import java.util.Properties;

public class Test
{
	public static void main(String[] args) throws IOException
	{
		Properties p = new Properties();
		p.load(Test.class.getResourceAsStream("/conf/launcher.ini"));
		System.out.println(p.getProperty("crawler.role"));
	}
}
