package guang.crawler.siteManager.jobQueue;

import guang.crawler.commons.WebURL;

import com.alibaba.fastjson.JSON;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.DatabaseEntry;

public class WebURLTransfer extends JEQueueElementTransfer<WebURL> {

	@Override
	public WebURL entryToObject(TupleInput input) {

		String jsonString = input.readString();
		WebURL webURL = JSON.parseObject(jsonString, WebURL.class);
		return webURL;
	}

	@Override
	public DatabaseEntry getDatabaseEntryKey(WebURL data) {
		byte[] keyData = data.getDocid().getBytes();
		return new DatabaseEntry(keyData);
	}

	@Override
	public void objectToEntry(WebURL url, TupleOutput output) {
		String jsonString = JSON.toJSONString(url);
		output.writeString(jsonString);

	}

}
