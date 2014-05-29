package guang.crawler.core;

public class StateManagement {

	public static StateManagement newStateManagement(GenericState beginState) {
		StateManagement management = new StateManagement(beginState);
		return management;

	}

	private GenericState currentState;

	private StateManagement() {
	}

	private StateManagement(GenericState beginState) {
		this.currentState = beginState;
	}

	public GenericState getCurrentState() {
		return this.currentState;
	}
}
