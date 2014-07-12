package guang.crawler.siteManager.commandlet;

import guang.crawler.jsonServer.Commandlet;
import guang.crawler.jsonServer.DataPacket;
import guang.crawler.siteManager.SiteManager;

import java.util.HashMap;

/**
 * 用来获取相关统计信息的Commandlet.当前类已经禁用.
 *
 * @author sun
 *
 */
@Deprecated
public class StatisticsGetter implements Commandlet {
	
	/**
	 * 这里获取了三个列表的数量
	 */
	@Override
	public DataPacket doCommand(final DataPacket request) {
		
		SiteManager siteManager = SiteManager.me();
		if (!siteManager.isShutdown()) {
			DataPacket response = new DataPacket("/statistics/get", null, null);
			HashMap<String, String> data = new HashMap<String, String>();
			long toDoSize = siteManager.getToDoTaskList()
			                           .getLength();
			long workingSize = siteManager.getWorkingTaskList()
			                              .getLength();
			long failedSize = siteManager.getFailedTaskList()
			                             .getLength();
			data.put("TODO", String.valueOf(toDoSize));
			data.put("WORKING", String.valueOf(workingSize));
			data.put("FAILED", String.valueOf(failedSize));
			response.setData(data);
			return response;
		} else {
			return null;
		}
		
	}
	
}
