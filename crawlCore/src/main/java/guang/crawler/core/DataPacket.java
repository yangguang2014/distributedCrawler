package guang.crawler.core;

import java.util.HashMap;

public class DataPacket
{
	public static final DataPacket	EXIT_DATA_PACKET	= new DataPacket(
	                                                         "/exit", null,
	                                                         null);
	public static final DataPacket	NOT_FOUND_PACKET	= new DataPacket(
	                                                         "/notFound", null,
	                                                         null);
	
	/**
	 * 传输的主题
	 */
	private String	                title;
	/**
	 * 传输的参数
	 */
	private HashMap<String, String>	arguments;
	/**
	 * 传输的数据
	 */
	private HashMap<String, String>	data;
	
	public DataPacket()
	{
		// 默认构造函数
	}
	
	public DataPacket(String title, HashMap<String, String> arguments,
	        HashMap<String, String> data)
	{
		this.title = title;
		this.arguments = arguments;
		this.data = data;
	}
	
	public HashMap<String, String> getArguments()
	{
		return this.arguments;
	}
	
	public HashMap<String, String> getData()
	{
		return this.data;
	}
	
	public String getTitle()
	{
		return this.title;
	}
	
	public void setArguments(HashMap<String, String> arguments)
	{
		this.arguments = arguments;
	}
	
	public void setData(HashMap<String, String> data)
	{
		this.data = data;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
}
