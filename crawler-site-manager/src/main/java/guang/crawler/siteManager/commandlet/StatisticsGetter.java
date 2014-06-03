package guang.crawler.siteManager.commandlet;

import guang.crawler.jsonServer.Commandlet;
import guang.crawler.jsonServer.DataPacket;
import guang.crawler.siteManager.SiteManager;

import java.util.HashMap;

public class StatisticsGetter implements Commandlet
{
	
	@Override
	public DataPacket doCommand(DataPacket request)
	{
		
		SiteManager siteManager = SiteManager.me();
		if (!siteManager.isShutdown())
		{
			DataPacket response = new DataPacket("/statistics/get", null, null);
			HashMap<String, String> data = new HashMap<String, String>();
			long toDoSize = siteManager.getToDoTaskList().getLength();
			long workingSize = siteManager.getWorkingTaskList().getLength();
			long failedSize = siteManager.getFailedTaskList().getLength();
			data.put("TODO", String.valueOf(toDoSize));
			data.put("WORKING", String.valueOf(workingSize));
			data.put("FAILED", String.valueOf(failedSize));
			response.setData(data);
			return response;
		} else
		{
			return null;
		}
		
	}
	
}
