package guang.crawler.commons.parserData;

/**
 * 如果Page的页面数据类型是二进制的数据,如图片视频等,那么将被解析为当前类.实际上,目前不支持二进制的数据.
 *
 * @author yang
 */
public class BinaryParseData implements ParseData {

	private static BinaryParseData	instance	= new BinaryParseData();

	public static BinaryParseData getInstance() {
		return BinaryParseData.instance;
	}

	@Override
	public String toString() {
		return "[Binary parse data can not be dumped as string]";
	}
}
