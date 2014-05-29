package guang.crawler.siteManager;

import guang.crawler.core.WebURL;
import guang.crawler.siteManager.jobQueue.MapQueue;
import guang.crawler.siteManager.jobQueue.MapQueueIteraotr;

import java.io.EOFException;
import java.io.IOException;
import java.net.URI;
import java.util.TimerTask;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.alibaba.fastjson.JSON;

public class SiteBackuper extends TimerTask {
	private static SiteBackuper defaultSiteBackuper;

	public static SiteBackuper me() {
		if (SiteBackuper.defaultSiteBackuper == null) {
			SiteBackuper.defaultSiteBackuper = new SiteBackuper();
		}
		return SiteBackuper.defaultSiteBackuper;
	}

	private FileSystem fileSystem;

	private SiteBackuper() {
	}

	private void backupList(MapQueue<WebURL> listToBackup, String path)
			throws IOException {
		try (MapQueueIteraotr<WebURL> iteraor = listToBackup.iterator();
				FSDataOutputStream fsout = this.fileSystem
						.create(new Path(path))) {
			while (iteraor.hasNext()) {
				WebURL url = iteraor.next();

				fsout.writeUTF(JSON.toJSONString(url));
			}
		}
	}

	public SiteBackuper init() throws IOException {
		Configuration configuration = new Configuration();
		this.fileSystem = FileSystem.get(
				URI.create(SiteConfig.me().getHadoopURL()), configuration);
		return this;
	}

	/**
	 * 加载备份的数据
	 * 
	 * @throws IOException
	 */
	public boolean loadBackupData() throws IOException {
		SiteConfig config = SiteConfig.me();
		String rootDir = config.getHadoopPath() + "/"
				+ config.getSiteManagerId() + "/backup";
		Path maxVersionFilePath = new Path(rootDir + "/max-version");
		boolean exists;
		try {
			exists = this.fileSystem.exists(maxVersionFilePath);
		} catch (IOException e) {
			return false;
		}
		if (exists) {
			int version = -1;
			try (FSDataInputStream fsin = this.fileSystem
					.open(maxVersionFilePath)) {
				version = fsin.readInt();
			}

			String backDir = rootDir + "/" + version;
			SiteManager siteManager = SiteManager.me();
			this.readBackupList(siteManager.getToDoTaskList(), backDir
					+ "/todoList");
			this.readBackupList(siteManager.getWorkingTaskList(), backDir
					+ "/workingList");
			this.readBackupList(siteManager.getFailedTaskList(), backDir
					+ "/failedList");
			config.setBackupVersion(version);
			return true;
		} else {
			return false;
		}
	}

	public void readBackupList(MapQueue<WebURL> list, String backupFilePath)
			throws IOException {
		try (FSDataInputStream fsin = this.fileSystem.open(new Path(
				backupFilePath))) {
			while (true) {
				try {

					String urlJSON = fsin.readUTF();
					WebURL weburl = JSON.parseObject(urlJSON, WebURL.class);
					list.put(weburl);
				} catch (EOFException e) {
					break;

				}
			}

		}

	}

	@Override
	public void run() {
		SiteConfig config = SiteConfig.me();
		// 首先设置系统为backup time，从而让用户不再获取新的任务，也暂停清理线程的工作
		config.setBackTime(true);

		// 找到备份的目录
		String rootDir = config.getHadoopPath() + "/"
				+ config.getSiteManagerId() + "/backup";
		try {
			this.fileSystem.mkdirs(new Path(rootDir));
		} catch (IllegalArgumentException | IOException e) {
			return;
		}
		// 找到当前最大的备份版本号
		Path maxVersionFilePath = new Path(rootDir + "/max-version");
		boolean exists;
		try {
			exists = this.fileSystem.exists(maxVersionFilePath);
		} catch (IOException e) {
			return;
		}
		int maxVersion = config.getBackupVersion();
		if (exists) {
			try (FSDataInputStream fsin = this.fileSystem
					.open(maxVersionFilePath)) {
				int version = fsin.readInt();
				maxVersion = maxVersion < version ? version : maxVersion;
				maxVersion++;
			} catch (IOException e) {
				return;
			}
		}

		String backDir = rootDir + "/" + maxVersion;
		Path backDirPath = new Path(backDir);
		try {
			if (this.fileSystem.exists(backDirPath)) {
				this.fileSystem.delete(backDirPath, true);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			this.fileSystem.mkdirs(backDirPath);
		} catch (IllegalArgumentException | IOException e) {
			return;
		}
		// 备份三个队列的数据
		SiteManager siteManager = SiteManager.me();
		try {
			this.backupList(siteManager.getToDoTaskList(), backDir
					+ "/todoList");
			this.backupList(siteManager.getWorkingTaskList(), backDir
					+ "/workingList");
			this.backupList(siteManager.getFailedTaskList(), backDir
					+ "/failedList");
		} catch (IOException e) {
			return;
		}
		try {
			try (FSDataOutputStream fsout = this.fileSystem.create(
					maxVersionFilePath, true)) {
				fsout.writeInt(maxVersion);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 到此为止，备份结束了
		config.setBackupVersion(maxVersion);
		config.setBackTime(false);
	}
}
