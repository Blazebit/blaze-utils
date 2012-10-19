package com.blazebit.regex.node;

import java.util.ArrayList;
import java.util.List;

public class OrNode extends AbstractNode {

	private final List<Node> nodes = new ArrayList<Node>();
	
	public void add(Node node){
		nodes.add(node);
	}
	
	public List<Node> getNodes(){
		return nodes;
	}
}
