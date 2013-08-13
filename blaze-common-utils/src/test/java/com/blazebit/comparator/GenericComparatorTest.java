/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.comparator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Locale;

import org.junit.Test;

/**
 *
 * @author Thomas Herzog
 */
public class GenericComparatorTest {

    public class TestObject {

        private String stringValue;
        private int intValue;
        private TestObject member;

        public TestObject(TestObject member) {
            this.member = member;
        }

        public TestObject(int intValue) {
            this.intValue = intValue;
        }

        public TestObject(int intValue, TestObject member) {
            this.intValue = intValue;
            this.member = member;
        }

        public TestObject(String stringValue, TestObject member) {
            this.stringValue = stringValue;
            this.member = member;
        }

        public TestObject(String stringValue, int intValue, TestObject member) {
            this.stringValue = stringValue;
            this.intValue = intValue;
            this.member = member;
        }

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

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        public TestObject getMember() {
            return member;
        }

        public void setMember(TestObject member) {
            this.member = member;
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
            if ((this.stringValue == null) ? (other.stringValue != null)
                    : !this.stringValue.equals(other.stringValue)) {
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
        List<TestObject> values = Arrays.asList(new TestObject("4"),
                new TestObject("3"), new TestObject("2"), new TestObject("1"));
        List<TestObject> expResult = Arrays.asList(new TestObject("1"),
                new TestObject("2"), new TestObject("3"), new TestObject("4"));
        Collections.sort(values, new GenericComparator<Object>("stringValue"));
        assertEquals(expResult, values);
    }

    @Test
    public void testGenericComparatorWithInt() {
        List<TestObject> values = Arrays.asList(new TestObject(4),
                new TestObject(3), new TestObject(2), new TestObject(1));
        List<TestObject> expResult = Arrays.asList(new TestObject(1),
                new TestObject(2), new TestObject(3), new TestObject(4));
        Collections.sort(values, new GenericComparator<Object>("intValue"));
        assertEquals(expResult, values);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGenericCollectionComparatorWithList() {
        List<List<TestObject>> values = Arrays.asList(
                Arrays.asList(new TestObject(3), new TestObject(1)),
                Arrays.asList(new TestObject(2), new TestObject(2)),
                Arrays.asList(new TestObject(1), new TestObject(3)));
        List<List<TestObject>> expResult1 = Arrays.asList(
                Arrays.asList(new TestObject(1), new TestObject(3)),
                Arrays.asList(new TestObject(2), new TestObject(2)),
                Arrays.asList(new TestObject(3), new TestObject(1)));
        List<List<TestObject>> expResult2 = Arrays.asList(
                Arrays.asList(new TestObject(3), new TestObject(1)),
                Arrays.asList(new TestObject(2), new TestObject(2)),
                Arrays.asList(new TestObject(1), new TestObject(3)));
        Collections.sort(values, new GenericCollectionComparator<Object>(
                "intValue", 0));
        assertEquals(expResult1, values);
        Collections.sort(values, new GenericCollectionComparator<Object>(
                "intValue", 1));
        assertEquals(expResult2, values);
    }

    @Test
    public void testGenericCollectionComparatorWithArray1() {
        List<TestObject[]> values = Arrays.asList(new TestObject[]{
                    new TestObject(3), new TestObject(1)}, new TestObject[]{
                    new TestObject(2), new TestObject(2)}, new TestObject[]{
                    new TestObject(1), new TestObject(3)});
        List<TestObject[]> expected = Arrays.asList(new TestObject[]{
                    new TestObject(1), new TestObject(3)}, new TestObject[]{
                    new TestObject(2), new TestObject(2)}, new TestObject[]{
                    new TestObject(3), new TestObject(1)});
        Collections.sort(values, new GenericCollectionComparator<Object>(
                "intValue", 0));
        assertArrayEquals(expected.toArray(), values.toArray());
    }

    @Test
    public void testGenericCollectionComparatorWithArray2() {
        List<TestObject[]> values = Arrays.asList(new TestObject[]{
                    new TestObject(3), new TestObject(3)}, new TestObject[]{
                    new TestObject(2), new TestObject(2)}, new TestObject[]{
                    new TestObject(1), new TestObject(1)});
        List<TestObject[]> expected = Arrays.asList(new TestObject[]{
                    new TestObject(1), new TestObject(1)}, new TestObject[]{
                    new TestObject(2), new TestObject(2)}, new TestObject[]{
                    new TestObject(3), new TestObject(3)});
        Collections.sort(values, new GenericCollectionComparator<Object>(
                "intValue", 1));
        assertArrayEquals(expected.toArray(), values.toArray());
    }

    @Test
    public void testGenericCompareViaPropertyPath() {
        List<GenericComparatorTest.TestObject> actual = Arrays.asList(
                new TestObject(new TestObject("4")), new TestObject(
                new TestObject("3")), new TestObject(
                new TestObject("2")), new TestObject(
                new TestObject("1")));
        List<GenericComparatorTest.TestObject> expected = Arrays.asList(
                new TestObject(new TestObject("1")), new TestObject(
                new TestObject("2")), new TestObject(
                new TestObject("3")), new TestObject(
                new TestObject("4")));
        Collections.sort(actual,
                new GenericComparator<GenericComparatorTest.TestObject>(
                "member.stringValue"));
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenericCompareWithInvalidPropertyPathMemeberDoesReturnNull() {
        List<GenericComparatorTest.TestObject> actual = Arrays.asList(
                new TestObject(new TestObject("4")), new TestObject(
                new TestObject("3")), new TestObject(
                new TestObject("2")), new TestObject(
                new TestObject("1")));
        List<GenericComparatorTest.TestObject> expected = Arrays.asList(
                new TestObject(new TestObject("1")), new TestObject(
                new TestObject("2")), new TestObject(
                new TestObject("3")), new TestObject(
                new TestObject("4")));
        Collections.sort(actual,
                new GenericComparator<GenericComparatorTest.TestObject>(
                "member.memeber.stringValue"));
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenericCompareWithInvalidPropertyPathMemberDoesNotExist() {
        List<GenericComparatorTest.TestObject> actual = Arrays.asList(
                new TestObject(new TestObject("4")), new TestObject(
                new TestObject("3")), new TestObject(
                new TestObject("2")), new TestObject(
                new TestObject("1")));
        List<GenericComparatorTest.TestObject> expected = Arrays.asList(
                new TestObject(new TestObject("1")), new TestObject(
                new TestObject("2")), new TestObject(
                new TestObject("3")), new TestObject(
                new TestObject("4")));
        Collections.sort(actual,
                new GenericComparator<GenericComparatorTest.TestObject>(
                "member.stringValuesssssss"));
        assertEquals(expected, actual);
    }
}
