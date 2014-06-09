package guang.crawler.jsonServer;

import guang.crawler.util.StreamHelper;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * 这里就只支持短连接的工作方式了。
 * 
 * @author yang
 * 
 */
public class AcceptRequestHandler implements Runnable
{
	private Socket	                     client;
	private CommandletLoader	         commandletLoader;
	private final AcceptThreadController	acceptThreadController;
	
	public AcceptRequestHandler(Socket client,
	        CommandletLoader commandletLoader,
	        AcceptThreadController acceptThreadController) throws IOException
	{
		this.client = client;
		this.commandletLoader = commandletLoader;
		this.acceptThreadController = acceptThreadController;
	}
	
	private void doExit()
	{
		try
		{
			this.client.close();
		} catch (IOException e)
		{
			return;
		}
	}
	
	@Override
	public void run()
	{
		// 如果当前已经控制要结束了，那么就直接结束。
		if (this.acceptThreadController.getType() != AcceptThreadController.TYPE_START)
		{
			this.doExit();
			return;
		}
		try
		{
			DataPacket data = null;
			try
			{
				data = StreamHelper.readObject(this.client.getInputStream(),
				        DataPacket.class);
			} catch (SocketTimeoutException e)
			{
				return;
			} catch (Exception e)
			{
				data = null;
			}
			if (data == null)
			{
				return;
			}
			Commandlet commandlet = this.commandletLoader.getCommandlet(data
			        .getTitle());
			DataPacket result = null;
			if (commandlet == null)
			{
				result = DataPacket.NOT_FOUND_PACKET;
			} else
			{
				result = commandlet.doCommand(data);
			}
			if (result != null)
			{
				try
				{
					StreamHelper.writeObject(this.client.getOutputStream(),
					        result);
				} catch (IOException e)
				{
					return;
				}
			}
		} finally
		{
			this.doExit();
		}
		
	}
}
