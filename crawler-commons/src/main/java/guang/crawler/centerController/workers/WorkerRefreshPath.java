package guang.crawler.centerController.workers;

import guang.crawler.centerController.CenterConfigElement;
import guang.crawler.connector.CenterConfigConnector;

import java.io.IOException;
import java.util.Date;

import org.apache.zookeeper.KeeperException;

public class WorkerRefreshPath extends CenterConfigElement
{
	
	public WorkerRefreshPath(String path, CenterConfigConnector connector)
	{
		super(path, connector);
	}
	
	public void setChanged() throws InterruptedException, IOException,
	        KeeperException
	{
		this.setProperty("changed", new Date().toString(), true);
	}
	
}
