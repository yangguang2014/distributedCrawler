package guang.crawler.siteManager.commandlet;

import guang.crawler.centerConfig.CenterConfig;
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

/**
 * 爬虫工作者向站点管理器请求URL,该Commandlet将负责处理该请求.
 *
 * @author sun
 *
 */
public class URLsGetter implements Commandlet {
	/**
	 * 属性的key,表示需要获取的数量
	 */
	private static final String	KEY_COUNT	 = "COUNT";
	/**
	 * 属性的key,表示URL列表
	 */
	private static final String	KEY_URL_LIST	= "URL_LIST";
	
	@Override
	public DataPacket doCommand(final DataPacket request) {
		HashMap<String, String> data = request.getData();
		if (SiteConfig.me()
		              .isBackupTime())// 如果当前正在进行相关文件的备份，那么就暂时不提供url了。
		{
			return this.noOps(data);
		}
		
		SiteManager siteManager = SiteManager.me();
		MapQueue<WebURL> todoList = siteManager.getToDoTaskList();
		MapQueue<WebURL> workingList = siteManager.getWorkingTaskList();
		if ((todoList.getLength() == 0) && (workingList.getLength() == 0)) {
			// 当前站点没有什么需要做的，那么就设置当前站点为完成状态
			try {
				SiteConfig.me()
				          .getSiteToHandle()
				          .setFinished(true, true);
				SiteManager.me()
				           .stopGathering();
				SiteManager.me()
				           .getBackuperDaemon()
				           .clearBackups();
				CenterConfig.me()
				            .getSiteManagersConfigInfo()
				            .getOnlineSiteManagers()
				            .notifyChanged();
				return null;
			} catch (Exception e) {
				return this.noOps(data);
			}
		}
		// 如果一切正常，那么就正常的取值处理
		String count = data.get(URLsGetter.KEY_COUNT);
		// 这里暂时只获取一个
		int num = 1;
		if (count != null) {
			try {
				num = Integer.parseInt(count);
			} catch (NumberFormatException e) {
				num = 1;
			}
		}
		
		List<WebURL> urls = todoList.get(num);
		DataPacket result = new DataPacket();
		result.setTitle("OK");
		result.setData(data);
		data.put(URLsGetter.KEY_COUNT, String.valueOf(urls.size()));
		int i = 0;
		long currentTime = new Date().getTime();
		for (WebURL url : urls) {
			url.startTime(currentTime)
			   .increaseTryTime();
			String urlString = JSON.toJSONString(url);
			data.put(URLsGetter.KEY_URL_LIST + i++, urlString);
			siteManager.getWorkingTaskList()
			           .put(url);
		}
		return result;
		
	}
	
	private DataPacket noOps(final HashMap<String, String> data) {
		DataPacket result = new DataPacket();
		result.setTitle("ERROR");
		result.setData(data);
		data.put(URLsGetter.KEY_COUNT, "0");
		return result;
	}
	
}
