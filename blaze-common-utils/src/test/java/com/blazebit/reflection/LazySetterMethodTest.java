/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.reflection;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Christian Beikov
 */
public class LazySetterMethodTest {

    private class A {
        private B b;

        public A(B b) {
            this.b = b;
        }

        public B getB() {
            return b;
        }

        @SuppressWarnings("unused")
        public void setB(B b) {
            this.b = b;
        }
    }

    private class B {
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
        this.a = new A(new B(""));
    }

    /**
     * Test of invoke method, of class LazySetterMethod.
     */
    @Test
    public void testInvoke() throws Exception {
        new LazySetterMethod(a, "b.s", "value").invoke();
        assertEquals(a.getB().getS(), "value");
        a.getB().setS("test");
        assertEquals(a.getB().getS(), "test");

        new LazySetterMethod(a, "b.s", new LazyGetterMethod(new B("lazyValue"),
                "s")).invoke();
        assertEquals(a.getB().getS(), "lazyValue");
        a.getB().setS(new B("lazyValue").getS());
        assertEquals(a.getB().getS(), "lazyValue");
    }
}
