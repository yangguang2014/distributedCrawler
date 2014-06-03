package guang.crawler.siteManager.commandlet;

import guang.crawler.commons.WebURL;
import guang.crawler.jsonServer.Commandlet;
import guang.crawler.jsonServer.DataPacket;
import guang.crawler.siteManager.SiteConfig;
import guang.crawler.siteManager.SiteManager;
import guang.crawler.siteManager.jobQueue.MapQueue;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;

public class URLsGetter implements Commandlet
{
	private static final String	KEY_COUNT	 = "COUNT";
	private static final String	KEY_URL_LIST	= "URL_LIST";
	
	@Override
	public DataPacket doCommand(DataPacket request)
	{
		HashMap<String, String> data = request.getData();
		if (SiteConfig.me().isBackupTime())// 如果当前正在进行相关文件的备份，那么就暂时不提供url了。
		{
			DataPacket result = new DataPacket();
			result.setTitle("ERROR");
			result.setData(data);
			data.put(URLsGetter.KEY_COUNT, "0");
			return result;
		}
		String count = data.get(URLsGetter.KEY_COUNT);
		// 这里暂时只获取一个
		int num = 1;
		if (count != null)
		{
			try
			{
				num = Integer.parseInt(count);
			} catch (NumberFormatException e)
			{
				num = 1;
			}
		}
		SiteManager siteManager = SiteManager.me();
		MapQueue<WebURL> todoList = siteManager.getToDoTaskList();
		List<WebURL> urls = todoList.get(num);
		
		DataPacket result = new DataPacket();
		result.setTitle("OK");
		result.setData(data);
		data.put(URLsGetter.KEY_COUNT, String.valueOf(urls.size()));
		int i = 0;
		long currentTime = new Date().getTime();
		for (WebURL url : urls)
		{
			url.startTime(currentTime).increaseTryTime();
			String urlString = JSON.toJSONString(url);
			data.put(URLsGetter.KEY_URL_LIST + i++, urlString);
			siteManager.getWorkingTaskList().put(url);
		}
		return result;
		
	}
	
}
