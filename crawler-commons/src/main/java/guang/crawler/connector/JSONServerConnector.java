package guang.crawler.connector;

import guang.crawler.jsonServer.DataPacket;
import guang.crawler.util.StreamHelper;

import java.io.IOException;
import java.net.Socket;

/**
 * 
 * @author yang
 */
public class JSONServerConnector
{
	
	private Socket	socket;
	private String	host;
	private int	   port;
	
	public JSONServerConnector(String host, int port)
	{
		this.host = host;
		this.port = port;
		
	}
	
	public boolean open()
	{
		try
		{
			this.socket = new Socket(this.host, this.port);
			return true;
		} catch (Exception e)
		{
			return false;
		}
	}
	
	public DataPacket read() throws IOException
	{
		return StreamHelper.readObject(this.socket.getInputStream(),
		        DataPacket.class);
	}
	
	public void send(DataPacket packet) throws IOException
	{
		StreamHelper.writeObject(this.socket.getOutputStream(), packet);
	}
	
	public void shutdown()
	{
		if (this.socket != null)
		{
			try
			{
				this.socket.close();
			} catch (IOException e)
			{
				// skip
			}
		}
	}
	
}
