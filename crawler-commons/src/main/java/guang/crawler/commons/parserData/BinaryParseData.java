package guang.crawler.commons.parserData;

/**
 * 如果遇到的是二进制的数据类型，那么使用这种数据格式。
 * 
 * @author yang
 */
public class BinaryParseData implements ParseData
{

	private static BinaryParseData	instance	= new BinaryParseData();

	public static BinaryParseData getInstance()
	{
		return BinaryParseData.instance;
	}

	@Override
	public String toString()
	{
		return "[Binary parse data can not be dumped as string]";
	}
}
