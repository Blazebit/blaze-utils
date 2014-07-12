package com.blazebit.regex;

import com.blazebit.regex.node.Node;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class PatternTest {

	@Test
	public void test() {
		Node node = Pattern.parse("[a-zA-Z]-vector");
		assertNotNull(node);
	}

}
