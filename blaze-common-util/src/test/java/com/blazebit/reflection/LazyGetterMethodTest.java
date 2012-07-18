/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.reflection;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Christian Beikov
 */
public class LazyGetterMethodTest {
    
    private class A{
        private B b;

        public A(B b) {
            this.b = b;
        }

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }
    }
    private class B{
        private String s;

        public B(String s) {
            this.s = s;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }
        
    }
    
    private A a;
    
    @Before
    public void setUp() {
        this.a = new A(new B("value"));
    }

    /**
     * Test of invoke method, of class LazyGetterMethod.
     */
    @Test
    public void testInvoke() throws Exception {
        assertEquals(new LazyGetterMethod(a, "b").invoke(), a.getB());
        assertEquals(new LazyGetterMethod(a, "b.s").invoke(), a.getB().getS());
    }
}
