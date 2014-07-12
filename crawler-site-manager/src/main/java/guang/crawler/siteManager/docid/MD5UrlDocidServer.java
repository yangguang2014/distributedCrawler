package guang.crawler.siteManager.docid;

import guang.crawler.commons.WebURL;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

/**
 * 采用MD5+BASE64的方式生成DocID。
 * <p>
 * 这样做是因为需要每个URL的文档ID都不同,并且同一个URL的文档ID都相同.
 *
 * @author yang
 *
 */
public class MD5UrlDocidServer implements DocidServer {
	private final String	DIGESTALG	= "MD5";
	private MessageDigest	digest;

	public MD5UrlDocidServer() throws NoSuchAlgorithmException {
		this.digest = MessageDigest.getInstance(this.DIGESTALG);
	}

	@Override
	public String next(final WebURL webUrl) {
		// 获取URL的MD5表示
		byte[] digestData = this.digest.digest(webUrl.getURL()
		                                             .getBytes());
		// 使用Base64编码
		digestData = Base64.encodeBase64(digestData);
		// 返回最终的码值对应的字符串
		return new String(digestData);
	}

}
