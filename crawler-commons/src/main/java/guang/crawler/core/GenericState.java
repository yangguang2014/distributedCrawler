package guang.crawler.core;

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
	private final int state;

	private GenericState(int state) {
		this.state = state;
	}

	public int getState() {
		return this.state;
	}
}
