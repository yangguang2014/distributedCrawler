package guang.crawler.siteManager.jobQueue;

import guang.crawler.commons.WebURL;

import com.alibaba.fastjson.JSON;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.DatabaseEntry;

/**
 * WebURL类的转换器.将WebURL对象转换成BerkelyDB中可以使用的数据类型
 *
 * @author sun
 *
 */
public class WebURLTransfer extends JEQueueElementTransfer<WebURL> {
	
	/**
	 * 将数据库中的一个输入数据转换成WebURL对象
	 */
	@Override
	public WebURL entryToObject(final TupleInput input) {
		
		String jsonString = input.readString();
		WebURL webURL = JSON.parseObject(jsonString, WebURL.class);
		return webURL;
	}
	
	/**
	 * 从WebURL对象中获取主键,并转化为BerkeleyDB中的数据对象.
	 */
	@Override
	public DatabaseEntry getDatabaseEntryKey(final WebURL data) {
		byte[] keyData = data.getDocid()
		                     .getBytes();
		return new DatabaseEntry(keyData);
	}
	
	/**
	 * 将一个WebURL对象输出到数据库中的数据对象中.
	 */
	@Override
	public void objectToEntry(final WebURL url, final TupleOutput output) {
		String jsonString = JSON.toJSONString(url);
		output.writeString(jsonString);
		
	}
	
}
