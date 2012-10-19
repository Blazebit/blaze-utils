package com.blazebit.regex;

import static org.junit.Assert.*;

import org.junit.Test;

import com.blazebit.regex.node.Node;

public class PatternTest {

	@Test
	public void test() {
		Node node = Pattern.parse("[a-zA-Z]-vector");
		assertNotNull(node);
	}

}
