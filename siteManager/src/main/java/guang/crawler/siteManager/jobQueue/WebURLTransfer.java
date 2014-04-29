package guang.crawler.siteManager.jobQueue;

import guang.crawler.core.WebURL;
import guang.crawler.siteManager.util.Util;

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
		webURL.setDocid(input.readInt());
		webURL.setParentDocid(input.readInt());
		webURL.setDepth(input.readShort());
		webURL.setPriority(input.readByte());
		webURL.setSiteManagerName(input.readString());
		return webURL;
	}
	
	@Override
	public DatabaseEntry getDatabaseEntryKey(WebURL data)
	{
		byte[] keyData = new byte[6];
		keyData[0] = data.getPriority();
		keyData[1] = (data.getDepth() > Byte.MAX_VALUE ? Byte.MAX_VALUE
		        : (byte) data.getDepth());
		Util.putIntInByteArray(data.getDocid(), keyData, 2);
		return new DatabaseEntry(keyData);
	}
	
	@Override
	public void objectToEntry(WebURL url, TupleOutput output)
	{
		output.writeString(url.getURL());
		output.writeInt(url.getDocid());
		output.writeInt(url.getParentDocid());
		output.writeShort(url.getDepth());
		output.writeByte(url.getPriority());
		output.writeString(url.getSiteManagerName());
		
	}
	
}
