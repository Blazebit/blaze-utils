/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public class FetchUtilTest {
    
    @Test
    public void testSimple() {
        String query = "FROM TesClass t ";
        String alias = "t";
        String expResult = query + " LEFT OUTER JOIN FETCH t.testComplexClass _0_testComplexClass_element_0";
        query += FetchUtil.getFetchProfilePlaceholder(TestClass.class);
        FetchProfile<TestClass> f = new FetchProfile<TestClass>(TestClass.class, "testComplexClass");
        String result = FetchUtil.query(query, alias, f);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testNested() {
        String query = "FROM TesClass t ";
        String alias = "t";
        String expResult = query + " LEFT OUTER JOIN FETCH t.testComplexClass _0_testComplexClass_element_0 LEFT OUTER JOIN FETCH _0_testComplexClass_element_0.testComplexClass _0_testComplexClass_element_1";
        query += FetchUtil.getFetchProfilePlaceholder(TestClass.class);
        FetchProfile<TestClass> f = new FetchProfile<TestClass>(TestClass.class, "testComplexClass.testComplexClass");
        String result = FetchUtil.query(query, alias, f);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testMultipleNested() {
        String query = "FROM TesClass t ";
        String alias = "t";
        String expResult = query + " LEFT OUTER JOIN FETCH t.testComplexClass _0_testComplexClass_element_0 LEFT OUTER JOIN FETCH _0_testComplexClass_element_0.testComplexClass _0_testComplexClass_element_1"
                                 + " LEFT OUTER JOIN FETCH t.testComplexClass2 _1_testComplexClass2_element_0 LEFT OUTER JOIN FETCH _1_testComplexClass2_element_0.testComplexClass _1_testComplexClass_element_1"
                                 + " LEFT OUTER JOIN FETCH _1_testComplexClass2_element_0.testComplexClass2 _2_testComplexClass2_element_1";
        query += FetchUtil.getFetchProfilePlaceholder(TestClass.class);
        FetchProfile<TestClass> f = new FetchProfile<TestClass>(TestClass.class, "testComplexClass.testComplexClass", "testComplexClass2.testComplexClass", "testComplexClass2.testComplexClass2");
        String result = FetchUtil.query(query, alias, f);
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
        query += FetchUtil.getFetchProfilePlaceholder(TestClass.class);
        FetchProfile<TestClass> f = new FetchProfile<TestClass>(TestClass.class, "testComplexClass.testComplexClass.testComplexClass3", "testComplexClass2.testComplexClass.testComplexClass3", "testComplexClass2.testComplexClass2", "testComplexClass2.testComplexClass.testComplexClass");
        String result = FetchUtil.query(query, alias, f);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testHibernateBug(){
        String query = "FROM TesClass t ";
        String alias = "t";
        String expResult = query + " LEFT OUTER JOIN FETCH t.testComplexClass4 LEFT OUTER JOIN FETCH t.testComplexClass4.testComplexClass _0_testComplexClass_element_1";
        query += FetchUtil.getFetchProfilePlaceholder(TestClass.class);
        FetchProfile<TestClass> f = new FetchProfile<TestClass>(TestClass.class, "testComplexClass4.testComplexClass");
        String result = FetchUtil.query(query, alias, f);
        assertEquals(expResult, result);
    }
    
    private static class TestClass{
        private String testString;
        private Set<TestClass> testComplexClass;
        private List<TestClass> testComplexClass2;
        private Collection<TestClass> testComplexClass3;
        private TestClass testComplexClass4;

        public Set<TestClass> getTestComplexClass() {
            return testComplexClass;
        }

        public void setTestComplexClass(Set<TestClass> testComplexClass) {
            this.testComplexClass = testComplexClass;
        }

        public List<TestClass> getTestComplexClass2() {
            return testComplexClass2;
        }

        public void setTestComplexClass2(List<TestClass> testComplexClass2) {
            this.testComplexClass2 = testComplexClass2;
        }

        public Collection<TestClass> getTestComplexClass3() {
            return testComplexClass3;
        }

        public void setTestComplexClass3(Collection<TestClass> testComplexClass3) {
            this.testComplexClass3 = testComplexClass3;
        }

        public TestClass getTestComplexClass4() {
            return testComplexClass4;
        }

        public void setTestComplexClass4(TestClass testComplexClass4) {
            this.testComplexClass4 = testComplexClass4;
        }

        public String getTestString() {
            return testString;
        }

        public void setTestString(String testString) {
            this.testString = testString;
        }

        
    }
}
