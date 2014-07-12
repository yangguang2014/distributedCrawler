package guang.crawler.commons;

/**
 * 通用的一些状态.由于很多部分都需要设置状态,而这些状态通常都是字符串或者数字,因此就想把他们都统一起来.但是时间有限,暂时没有做.
 * 
 * @author sun
 *
 */
public enum GenericState {
	
	/**
	 * 处于刚刚创建的状态
	 */
	created(0),
	
	/**
	 * 在中心控制器上注册了
	 */
	registed(1),
	/**
	 * 处于已经初始化的状态
	 */
	inited(2),
	/**
	 * 处于已经启动状态
	 */
	started(3),
	/**
	 * 处于已经停止状态
	 */
	stopped(4);
	private final int	state;
	
	private GenericState(final int state) {
		this.state = state;
	}
	
	public int getState() {
		return this.state;
	}
}
