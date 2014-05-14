package guang.crawler.siteManager.commandlet;

import guang.crawler.core.DataPacket;
import guang.crawler.siteManager.SiteManager;
import guang.crawler.siteManager.SiteManagerException;
import guang.crawler.siteManager.jsonServer.Commandlet;

import java.util.HashMap;

public class StatisticsGetter implements Commandlet
{
	
	@Override
	public DataPacket doCommand(DataPacket request)
	{
		
		SiteManager siteManager = null;
		try
		{
			siteManager = SiteManager.getSiteManager();
		} catch (SiteManagerException e)
		{
			return null;
		}
		if (!siteManager.isShutdown())
		{
			DataPacket response = new DataPacket("/statistics/get", null, null);
			HashMap<String, String> data = new HashMap<>();
			long toDoSize = siteManager.getToDoTaskList().getLength();
			long workingSize = siteManager.getWorkingTaskList().getLength();
			long finishedSize = siteManager.getFinishedTaskList().getLength();
			data.put("TODO", String.valueOf(toDoSize));
			data.put("WORKING", String.valueOf(workingSize));
			data.put("FINISHED", String.valueOf(finishedSize));
			response.setData(data);
			return response;
		} else
		{
			return null;
		}
		
	}
	
}
