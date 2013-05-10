package com.blazebit.regex.node;

public abstract class AbstractNode implements Node {

	private Node next;

	@Override
	public Node getNext() {
		return next;
	}

	@Override
	public void setNext(Node next) {
		this.next = next;
	}
}
