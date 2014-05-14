package guang.crawler.siteManager.urlFilter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;

/**
 * 使用位图方式过滤所有的URL。可能会有误报的情况存在。
 * 
 * @author yang
 * 
 */
public class BitMapFilter implements ObjectFilter
{
	private final int	  FILTER_SIZE	= 16 * 1024 * 1024;
	private final String	DIGESTALG	= "MD5";
	private BitSet	      filterData;
	private MessageDigest	digest;
	
	public BitMapFilter() throws NoSuchAlgorithmException
	{
		this.filterData = new BitSet(this.FILTER_SIZE);
		this.digest = MessageDigest.getInstance(this.DIGESTALG);
	}
	
	@Override
	public boolean contains(Object object)
	{
		if (object == null)
		{
			return false;
		}
		int bitIdx = this.hashObject(object);
		synchronized (this.filterData)
		{
			return this.filterData.get(bitIdx);
			
		}
	}
	
	@Override
	public boolean containsAndSet(Object object)
	{
		if (object == null)
		{
			return false;
		}
		int bitIdx = this.hashObject(object);
		synchronized (this.filterData)
		{
			if (this.filterData.get(bitIdx))
			{
				return true;
			} else
			{
				this.filterData.set(bitIdx);
				return false;
			}
			
		}
	}
	
	private int hashObject(Object object)
	{
		byte[] digestData = this.digest.digest(object.toString().getBytes());
		int bitIdx = 0;
		bitIdx |= (digestData[7] & 0xff);
		bitIdx <<= 8;
		bitIdx |= (digestData[8] & 0xff);
		bitIdx <<= 8;
		bitIdx |= (digestData[9] & 0xff);
		return bitIdx;
	}
	
}
