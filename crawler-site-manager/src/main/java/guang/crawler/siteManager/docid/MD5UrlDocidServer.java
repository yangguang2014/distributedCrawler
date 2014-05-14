package guang.crawler.siteManager.docid;

import guang.crawler.core.WebURL;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

/**
 * 采用MD5+BASE64的方式生成DocID。
 * 
 * @author yang
 * 
 */
public class MD5UrlDocidServer implements DocidServer
{
	private final String	DIGESTALG	= "MD5";
	private MessageDigest	digest;
	
	public MD5UrlDocidServer() throws NoSuchAlgorithmException
	{
		this.digest = MessageDigest.getInstance(this.DIGESTALG);
	}
	
	@Override
	public String next(WebURL webUrl)
	{
		byte[] digestData = this.digest.digest(webUrl.getURL().getBytes());
		digestData = Base64.encodeBase64(digestData);
		return new String(digestData);
	}
	
}
