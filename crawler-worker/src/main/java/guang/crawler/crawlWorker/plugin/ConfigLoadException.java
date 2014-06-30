package guang.crawler.crawlWorker.plugin;

/**
 * 加载配置信息抛出异常.
 * 
 * @author sun
 * 
 */
public class ConfigLoadException extends Exception {
	private static final long	serialVersionUID	= 1L;
	
	public ConfigLoadException() {
	}
	
	public ConfigLoadException(final String message) {
		super(message);
	}
	
	public ConfigLoadException(final String message, final Throwable exception) {
		super(message, exception);
	}
	
	public ConfigLoadException(final Throwable exception) {
		super(exception);
	}
}
