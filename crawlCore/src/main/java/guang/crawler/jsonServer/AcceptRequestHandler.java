package guang.crawler.jsonServer;

import guang.crawler.core.DataPacket;
import guang.crawler.util.StreamHelper;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

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
		System.out.println("Connection will be shut down.");
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
		while (this.acceptThreadController.getType() == AcceptThreadController.TYPE_START)
		{
			DataPacket data = null;
			try
			{
				data = StreamHelper.readObject(this.client.getInputStream(),
				        DataPacket.class);
			} catch (SocketTimeoutException e)
			{
				// TODO 这里应当添加其他的处理方式
				continue;
			}
			
			catch (Exception e)
			{
				data = null;
			}
			if (data == null)
			{
				this.doExit();
				return;
			}
			String title = data.getTitle();
			if (title.equals(DataPacket.EXIT_DATA_PACKET.getTitle()))
			{
				this.doExit();
				break;
			} else
			{
				Commandlet commandlet = this.commandletLoader
				        .getCommandlet(data.getTitle());
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
						this.doExit();
					}
				}
				
			}
		}
		if (this.acceptThreadController.getType() == AcceptThreadController.TYPE_SHUTDOWN_NOW)
		{
			this.doExit();
		} else if (this.acceptThreadController.getType() == AcceptThreadController.TYPE_SHUTDOWN_GRACEFULLY)
		{
			// TODO 这里应当设计良好的停止机制
			this.doExit();
		}
	}
}
