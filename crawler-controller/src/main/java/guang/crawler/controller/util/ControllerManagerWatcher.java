package guang.crawler.controller.util;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class ControllerManagerWatcher implements Watcher {

	@Override
	public void process(WatchedEvent event) {
		if (event.getType() == Watcher.Event.EventType.NodeDeleted) { // 如果上一个竞争者已经退出，那么当前节点重新进行竞争

		}

	}

}
