/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.persistence;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public class FetchUtilsTest {
    
    @Test
    public void testSimple() {
        String query = "FROM TesClass t ";
        String alias = "t";
        String expResult = query + " LEFT OUTER JOIN FETCH t.testComplexClass _0_testComplexClass_element_0";
        query += FetchUtils.getFetchProfilePlaceholder(TestClass.class);
        FetchProfile<TestClass> f = new FetchProfile<TestClass>(TestClass.class, "testComplexClass");
        String result = FetchUtils.query(query, alias, f);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testNested() {
        String query = "FROM TesClass t ";
        String alias = "t";
        String expResult = query + " LEFT OUTER JOIN FETCH t.testComplexClass _0_testComplexClass_element_0 LEFT OUTER JOIN FETCH _0_testComplexClass_element_0.testComplexClass _0_testComplexClass_element_1";
        query += FetchUtils.getFetchProfilePlaceholder(TestClass.class);
        FetchProfile<TestClass> f = new FetchProfile<TestClass>(TestClass.class, "testComplexClass.testComplexClass");
        String result = FetchUtils.query(query, alias, f);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testMultipleNested() {
        String query = "FROM TesClass t ";
        String alias = "t";
        String expResult = query + " LEFT OUTER JOIN FETCH t.testComplexClass _0_testComplexClass_element_0 LEFT OUTER JOIN FETCH _0_testComplexClass_element_0.testComplexClass _0_testComplexClass_element_1"
                                 + " LEFT OUTER JOIN FETCH t.testComplexClass2 _1_testComplexClass2_element_0 LEFT OUTER JOIN FETCH _1_testComplexClass2_element_0.testComplexClass _1_testComplexClass_element_1"
                                 + " LEFT OUTER JOIN FETCH _1_testComplexClass2_element_0.testComplexClass2 _2_testComplexClass2_element_1";
        query += FetchUtils.getFetchProfilePlaceholder(TestClass.class);
        FetchProfile<TestClass> f = new FetchProfile<TestClass>(TestClass.class, "testComplexClass.testComplexClass", "testComplexClass2.testComplexClass", "testComplexClass2.testComplexClass2");
        String result = FetchUtils.query(query, alias, f);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testMultipleDeepNested() {
        String query = "FROM TesClass t ";
        String alias = "t";
        String expResult = query + " LEFT OUTER JOIN FETCH t.testComplexClass _0_testComplexClass_element_0 LEFT OUTER JOIN FETCH _0_testComplexClass_element_0.testComplexClass _0_testComplexClass_element_1 LEFT OUTER JOIN FETCH _0_testComplexClass_element_1.testComplexClass3 _0_testComplexClass3_element_2"
                                 + " LEFT OUTER JOIN FETCH t.testComplexClass2 _1_testComplexClass2_element_0 LEFT OUTER JOIN FETCH _1_testComplexClass2_element_0.testComplexClass _1_testComplexClass_element_1 LEFT OUTER JOIN FETCH _1_testComplexClass_element_1.testComplexClass3 _1_testComplexClass3_element_2"
                                 + " LEFT OUTER JOIN FETCH _1_testComplexClass2_element_0.testComplexClass2 _2_testComplexClass2_element_1"
                                 + " LEFT OUTER JOIN FETCH _1_testComplexClass_element_1.testComplexClass _3_testComplexClass_element_2";
        query += FetchUtils.getFetchProfilePlaceholder(TestClass.class);
        FetchProfile<TestClass> f = new FetchProfile<TestClass>(TestClass.class, "testComplexClass.testComplexClass.testComplexClass3", "testComplexClass2.testComplexClass.testComplexClass3", "testComplexClass2.testComplexClass2", "testComplexClass2.testComplexClass.testComplexClass");
        String result = FetchUtils.query(query, alias, f);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testHibernateBug(){
        String query = "FROM TesClass t ";
        String alias = "t";
        String expResult = query + " LEFT OUTER JOIN FETCH t.testComplexClass4 LEFT OUTER JOIN FETCH t.testComplexClass4.testComplexClass _0_testComplexClass_element_1";
        query += FetchUtils.getFetchProfilePlaceholder(TestClass.class);
        FetchProfile<TestClass> f = new FetchProfile<TestClass>(TestClass.class, "testComplexClass4.testComplexClass");
        String result = FetchUtils.query(query, alias, f);
        assertEquals(expResult, result);
    }
    
    private static class TestClass{
        private String testString;
        private Set<TestClass> testComplexClass;
        private List<TestClass> testComplexClass2;
        private Collection<TestClass> testComplexClass3;
        private TestClass testComplexClass4;

        @SuppressWarnings("unused")
		public Set<TestClass> getTestComplexClass() {
            return testComplexClass;
        }

        @SuppressWarnings("unused")
        public void setTestComplexClass(Set<TestClass> testComplexClass) {
            this.testComplexClass = testComplexClass;
        }

        @SuppressWarnings("unused")
        public List<TestClass> getTestComplexClass2() {
            return testComplexClass2;
        }

        @SuppressWarnings("unused")
        public void setTestComplexClass2(List<TestClass> testComplexClass2) {
            this.testComplexClass2 = testComplexClass2;
        }

        @SuppressWarnings("unused")
        public Collection<TestClass> getTestComplexClass3() {
            return testComplexClass3;
        }

        @SuppressWarnings("unused")
        public void setTestComplexClass3(Collection<TestClass> testComplexClass3) {
            this.testComplexClass3 = testComplexClass3;
        }

        @SuppressWarnings("unused")
        public TestClass getTestComplexClass4() {
            return testComplexClass4;
        }

        @SuppressWarnings("unused")
        public void setTestComplexClass4(TestClass testComplexClass4) {
            this.testComplexClass4 = testComplexClass4;
        }

        @SuppressWarnings("unused")
        public String getTestString() {
            return testString;
        }

        @SuppressWarnings("unused")
        public void setTestString(String testString) {
            this.testString = testString;
        }

        
    }
}
