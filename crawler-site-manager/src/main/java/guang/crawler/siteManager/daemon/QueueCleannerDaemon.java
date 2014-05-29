package guang.crawler.siteManager.daemon;

import guang.crawler.core.WebURL;
import guang.crawler.siteManager.SiteConfig;
import guang.crawler.siteManager.SiteManager;
import guang.crawler.siteManager.jobQueue.MapQueue;
import guang.crawler.siteManager.jobQueue.MapQueueIteraotr;

import java.util.Date;
import java.util.TimerTask;

/**
 * 该类用来定时的清理working list中过时的URL
 *
 * @author yang
 *
 */
public class QueueCleannerDaemon extends TimerTask
{
	private final MapQueue<WebURL>	workingList;
	private final MapQueue<WebURL>	todoList;
	private final MapQueue<WebURL>	failedList;
	private static QueueCleannerDaemon	cleaner;
	
	public static QueueCleannerDaemon me()
	{
		if (QueueCleannerDaemon.cleaner == null)
		{
			SiteManager siteManager = SiteManager.me();
			SiteConfig siteConfig = SiteConfig.me();
			QueueCleannerDaemon.cleaner = new QueueCleannerDaemon(
			        siteManager.getWorkingTaskList(),
			        siteManager.getToDoTaskList(),
			        siteManager.getFailedTaskList(),
			        siteConfig.getJobTimeout(), siteConfig.getJobTryTime());
		}
		return QueueCleannerDaemon.cleaner;
	}
	
	/**
	 * 超时的时间，以毫秒计算，默认5分钟
	 */
	private long	timeout	= 300000;
	private int	 tryTime	= 3;

	/**
	 * 启动一个清理任务
	 */
	private QueueCleannerDaemon(MapQueue<WebURL> workingList,
			MapQueue<WebURL> todoList, MapQueue<WebURL> failedList,
			long timeout, int tryTime)
	{
		this.workingList = workingList;
		this.todoList = todoList;
		this.failedList = failedList;
		if (timeout > 0)
		{
			this.timeout = timeout;
		}
		if (tryTime > 0)
		{
			this.tryTime = tryTime;
		}
	}

	@Override
	public void run()
	{
		if (SiteConfig.me().isBackupTime()) // 如果当前正在进行备份，那么停止清理
		{
			return;
		}
		long current = new Date().getTime();
		try (MapQueueIteraotr<WebURL> it = this.workingList.iterator())
		{
			while (it.hasNext())
			{
				WebURL webURL = it.next();
				if ((current - webURL.getStartTime()) > this.timeout) // 如果已经超时了
				{
					if (webURL.getTryTime() > this.tryTime) // 如果已经超过了重复尝试的次数，那么就应当将其放在失败列表中
					{
						this.failedList.put(webURL);
						it.remove();
					} else
						// 如果还可以继续尝试，那么就将其放在准备爬取的列表中
					{
						this.todoList.put(webURL);
						it.remove();
					}
				}
			}
		}

	}
}
