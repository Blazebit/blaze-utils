package com.blazebit.regex.node;


public class RepeatNode extends DecoratorNode {

	private final int min;
	private final int max;

	public RepeatNode(Node node) {
		this(node, 0);
	}
	
	public RepeatNode(Node node, int min){
		this(node, min, Integer.MAX_VALUE);
	}

	public RepeatNode(Node node, int min, int max){
		super(node);
		this.min = min;
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}
}
