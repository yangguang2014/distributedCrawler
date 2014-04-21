package guang.crawler.jsonServer;

import guang.crawler.core.DataPacket;

public interface Commandlet
{
	public DataPacket doCommand(DataPacket request);
	
}
