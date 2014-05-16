package guang.crawler.siteManager.commandlet;

import guang.crawler.core.DataPacket;
import guang.crawler.siteManager.SiteManager;
import guang.crawler.siteManager.jsonServer.Commandlet;

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
			HashMap<String, String> data = new HashMap<>();
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
