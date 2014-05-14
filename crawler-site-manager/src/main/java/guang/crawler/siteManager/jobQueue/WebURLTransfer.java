package guang.crawler.siteManager.jobQueue;

import guang.crawler.core.WebURL;

import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.DatabaseEntry;

public class WebURLTransfer extends JEQueueElementTransfer<WebURL>
{
	
	@Override
	public WebURL entryToObject(TupleInput input)
	{
		
		WebURL webURL = new WebURL();
		webURL.setURL(input.readString());
		webURL.setDocid(input.readString());
		webURL.setParentDocid(input.readInt());
		webURL.setDepth(input.readShort());
		webURL.setPriority(input.readByte());
		webURL.setSiteManagerName(input.readString());
		return webURL;
	}
	
	@Override
	public DatabaseEntry getDatabaseEntryKey(WebURL data)
	{
		byte[] keyData = data.getDocid().getBytes();
		return new DatabaseEntry(keyData);
	}
	
	@Override
	public void objectToEntry(WebURL url, TupleOutput output)
	{
		output.writeString(url.getURL());
		output.writeString(url.getDocid());
		output.writeInt(url.getParentDocid());
		output.writeShort(url.getDepth());
		output.writeByte(url.getPriority());
		output.writeString(url.getSiteManagerName());
		
	}
	
}
