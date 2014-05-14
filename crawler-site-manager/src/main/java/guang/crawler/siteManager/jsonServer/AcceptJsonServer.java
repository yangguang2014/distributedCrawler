package guang.crawler.siteManager.jsonServer;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * 该类代表使用Accept机制的Socket服务器，使用JSON协议进行通信。
 * 
 * @author yang
 */
public class AcceptJsonServer implements Runnable, JsonServer
{
	
	private final ServerSocket	   server;
	private final ExecutorService	threadPool;
	private CommandletLoader	   commandletLoader;
	private Thread	               serverThread;
	private AcceptThreadController	acceptThreadController;
	
	public AcceptJsonServer(int threadNum, File configFile, File schemaFile)
	        throws ServerStartException
	{
		
		this.acceptThreadController = new AcceptThreadController();
		this.commandletLoader = new CommandletLoader(configFile, schemaFile);
		try
		{
			this.commandletLoader.load();
		} catch (InstantiationException | IllegalAccessException
		        | ClassNotFoundException | SAXException | IOException
		        | ParserConfigurationException e)
		{
			throw new ServerStartException("Load config file failed!", e);
		}
		try
		{
			this.server = new ServerSocket();
		} catch (IOException e)
		{
			throw new ServerStartException("Can not open socket!", e);
		}
		this.threadPool = Executors.newFixedThreadPool(threadNum);
		
	}
	
	public AcceptJsonServer(int port, int backlog, int threadNum,
	        File configFile, File schemaFile) throws ServerStartException
	{
		this.acceptThreadController = new AcceptThreadController();
		this.commandletLoader = new CommandletLoader(configFile, schemaFile);
		try
		{
			this.commandletLoader.load();
		} catch (InstantiationException | IllegalAccessException
		        | ClassNotFoundException | SAXException | IOException
		        | ParserConfigurationException e)
		{
			throw new ServerStartException("Load config file failed!", e);
		}
		try
		{
			this.server = new ServerSocket(port, backlog);
		} catch (IOException e)
		{
			throw new ServerStartException("Can not open socket!", e);
		}
		this.threadPool = Executors.newFixedThreadPool(threadNum);
		
	}
	
	@Override
	public InetAddress getAddress()
	{
		if (this.server != null)
		{
			return this.server.getInetAddress();
		}
		return null;
	}
	
	@Override
	public int getPort()
	{
		if (this.server != null)
		{
			return this.server.getLocalPort();
		}
		return 0;
	}
	
	@Override
	public boolean isShutdown()
	{
		if (this.acceptThreadController.getType() != AcceptThreadController.TYPE_START)
		{
			return true;
		} else if (this.server.isClosed() && this.threadPool.isTerminated())
		{
			return true;
		}
		return false;
	}
	
	@Override
	public void run()
	{
		while (this.acceptThreadController.getType() == AcceptThreadController.TYPE_START)
		{
			Socket client;
			try
			{
				client = this.server.accept();
				AcceptRequestHandler command = new AcceptRequestHandler(client,
				        this.commandletLoader, this.acceptThreadController);
				this.threadPool.submit(command);
			} catch (IOException ex)
			{
				// 在accept的时候断掉了，说明是系统要求线程停止了。
				break;
			}
		}
		// 在这里已经结束了，被要求停止
		try
		{
			this.server.close();
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
		this.threadPool.shutdownNow();
		
	}
	
	@Override
	public void shutdown()
	{
		this.acceptThreadController
		        .setType(AcceptThreadController.TYPE_SHUTDOWN_NOW);
		try
		{
			this.server.close();
		} catch (IOException e)
		{
			// Should not come here.
			e.printStackTrace();
		}
		
	}
	
	@Override
	public boolean start()
	{
		if (this.serverThread == null)
		{
			this.serverThread = new Thread(this);
		}
		if ((this.serverThread != null) && !this.serverThread.isAlive())
		{
			try
			{
				this.serverThread.start();
				return true;
			} catch (IllegalThreadStateException e)
			{
				return false;
			}
		}
		return false;
	}
	
	@Override
	public void waitForStop()
	{
		if (this.serverThread.isAlive())
		{
			try
			{
				this.serverThread.join();
				this.threadPool.awaitTermination(1, TimeUnit.HOURS);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
