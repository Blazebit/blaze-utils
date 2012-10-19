package com.blazebit.regex.node;

public abstract class DecoratorNode extends AbstractNode{

	private final Node decorated;

	public DecoratorNode(Node decorated) {
		this.decorated = decorated;
	}

	public Node getDecorated() {
		return decorated;
	}
}
