package com.blazebit.regex;

import com.blazebit.regex.node.Node;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class PatternTest {

    @Test
    public void test() {
        Node node = Pattern.parse("[a-zA-Z]-vector");
        assertNotNull(node);
    }

}
