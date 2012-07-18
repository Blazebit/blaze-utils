/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.comparator;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Thomas Herzog
 */
public class GenericComparatorTest {

    private class TestObject {

        private String stringValue;
        private int intValue;

        public TestObject(String stringValue) {
            this.stringValue = stringValue;
        }

        public String getStringValue() {
            return stringValue;
        }

        public int getIntValue() {
            return intValue;
        }

        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }

        public TestObject(int intValue) {
            this.intValue = intValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TestObject other = (TestObject) obj;
            if ((this.stringValue == null) ? (other.stringValue != null) : !this.stringValue.equals(other.stringValue)) {
                return false;
            }
            if (this.intValue != other.intValue) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            return hash;
        }
    }

    @Test
    public void testGenericComparatorWithString() {
        List<TestObject> values = Arrays.asList(new TestObject("4"), new TestObject("3"), new TestObject("2"), new TestObject("1"));
        List<TestObject> expResult = Arrays.asList(new TestObject("1"), new TestObject("2"), new TestObject("3"), new TestObject("4"));
        Collections.sort(values, new GenericComparator("stringValue"));
        assertEquals(expResult, values);
    }

    @Test
    public void testGenericComparatorWithInt() {
        List<TestObject> values = Arrays.asList(new TestObject(4), new TestObject(3), new TestObject(2), new TestObject(1));
        List<TestObject> expResult = Arrays.asList(new TestObject(1), new TestObject(2), new TestObject(3), new TestObject(4));
        Collections.sort(values, new GenericComparator("intValue"));
        assertEquals(expResult, values);
    }

    @Test
    public void testGenericCollectionComparatorWithList() {
        List<List<TestObject>> values = Arrays.asList(Arrays.asList(new TestObject(3), new TestObject(1)), Arrays.asList(new TestObject(2), new TestObject(2)), Arrays.asList(new TestObject(1), new TestObject(3)));
        List<List<TestObject>> expResult1 = Arrays.asList(Arrays.asList(new TestObject(1), new TestObject(3)), Arrays.asList(new TestObject(2), new TestObject(2)), Arrays.asList(new TestObject(3), new TestObject(1)));
        List<List<TestObject>> expResult2 = Arrays.asList(Arrays.asList(new TestObject(3), new TestObject(1)), Arrays.asList(new TestObject(2), new TestObject(2)), Arrays.asList(new TestObject(1), new TestObject(3)));
        Collections.sort(values, new GenericCollectionComparator("intValue", 0));
        assertEquals(expResult1, values);
        Collections.sort(values, new GenericCollectionComparator("intValue", 1));
        assertEquals(expResult2, values);
    }

    @Test
    public void testGenericCollectionComparatorWithArray() {
        List<TestObject[]> values = Arrays.asList(new TestObject[]{new TestObject(3), new TestObject(1)}, new TestObject[]{new TestObject(2), new TestObject(2)}, new TestObject[]{new TestObject(1), new TestObject(3)});
        List<TestObject[]> expResult1 = Arrays.asList(new TestObject[]{new TestObject(1), new TestObject(3)}, new TestObject[]{new TestObject(2), new TestObject(2)}, new TestObject[]{new TestObject(3), new TestObject(1)});
        List<TestObject[]> expResult2 = Arrays.asList(new TestObject[]{new TestObject(3), new TestObject(1)}, new TestObject[]{new TestObject(2), new TestObject(2)}, new TestObject[]{new TestObject(1), new TestObject(3)});
        Collections.sort(values, new GenericCollectionComparator("intValue", 0));
        assertArrayEquals(expResult1.toArray(), values.toArray());
        Collections.sort(values, new GenericCollectionComparator("intValue", 1));
        assertEquals(expResult2.toArray(), values.toArray());
    }
}
