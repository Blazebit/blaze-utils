/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.reflection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Christian Beikov
 */
public class ReflectionUtilTest {

    private interface A {
        static String A_FIELD = "";
        String getA();
        boolean isB();
        void setA(String a);
        void setB(boolean b);
    }

    private interface B {
        static Integer B_FIELD = 1;
    }

    private interface C extends A {
        static boolean C_FIELD = true;
    }

    private class ClassA implements A {

        public String getA() {
            return "";
        }
        
        public void setA(String a){
            
        }
        
        public boolean isB(){
            return false;
        }
        
        public void setB(boolean b){
            
        }
    }

    private class ClassB extends ClassA implements B {
        
        @Override
        public boolean isB(){
            return true;
        }
    }
    
    private class GenericClassA<T, E extends Exception>{
        private T field;
        private Collection<T> fieldCollection;
        private Map<T, T> fieldMap;
        
        public T getField(){
            return field;
        }
        
        public void setField(T field){
            this.field = field;
        }

        public Collection<T> getFieldCollection() {
            return fieldCollection;
        }

        public void setFieldCollection(Collection<T> fieldCollection) {
            this.fieldCollection = fieldCollection;
        }

        public Map<T, T> getFieldMap() {
            return fieldMap;
        }

        public void setFieldMap(Map<T, T> fieldMap) {
            this.fieldMap = fieldMap;
        }
        
        public void throwsException() throws E{
            
        }
    }
    
    private class ConcreteClassA extends GenericClassA<Integer, Exception>{
        
    }

    /**
     * Test of resolveTypeVariable method, of class ReflectionUtil.
     */
    @Test
    public void testResolveTypeVariable() throws Exception {
        // Resolve field types
        
        TypeVariable<?> typeVariable = (TypeVariable<?>)ReflectionUtil.getField(ConcreteClassA.class, "field").getGenericType();
        Class<?> concreteType = ReflectionUtil.resolveTypeVariable(ConcreteClassA.class, typeVariable);
        assertEquals(Integer.class, concreteType);
        
        typeVariable = (TypeVariable<?>)((ParameterizedType)ReflectionUtil.getField(ConcreteClassA.class, "fieldCollection").getGenericType()).getActualTypeArguments()[0];
        concreteType = ReflectionUtil.resolveTypeVariable(ConcreteClassA.class, typeVariable);
        assertEquals(Integer.class, concreteType);
        
        typeVariable = (TypeVariable<?>)((ParameterizedType)ReflectionUtil.getField(ConcreteClassA.class, "fieldMap").getGenericType()).getActualTypeArguments()[0];
        concreteType = ReflectionUtil.resolveTypeVariable(ConcreteClassA.class, typeVariable);
        assertEquals(Integer.class, concreteType);
        
        // Resolve method return types
        
        typeVariable = (TypeVariable<?>)ReflectionUtil.getGetter(ConcreteClassA.class, "field").getGenericReturnType();
        concreteType = ReflectionUtil.resolveTypeVariable(ConcreteClassA.class, typeVariable);
        assertEquals(Integer.class, concreteType);
        
        typeVariable = (TypeVariable<?>)((ParameterizedType)ReflectionUtil.getGetter(ConcreteClassA.class, "fieldCollection").getGenericReturnType()).getActualTypeArguments()[0];
        concreteType = ReflectionUtil.resolveTypeVariable(ConcreteClassA.class, typeVariable);
        assertEquals(Integer.class, concreteType);
        
        typeVariable = (TypeVariable<?>)((ParameterizedType)ReflectionUtil.getGetter(ConcreteClassA.class, "fieldMap").getGenericReturnType()).getActualTypeArguments()[0];
        concreteType = ReflectionUtil.resolveTypeVariable(ConcreteClassA.class, typeVariable);
        assertEquals(Integer.class, concreteType);
        
        // Resolve method parameter types
        
        typeVariable = (TypeVariable<?>)ReflectionUtil.getSetter(ConcreteClassA.class, "field").getGenericParameterTypes()[0];
        concreteType = ReflectionUtil.resolveTypeVariable(ConcreteClassA.class, typeVariable);
        assertEquals(Integer.class, concreteType);
        
        typeVariable = (TypeVariable<?>)((ParameterizedType)ReflectionUtil.getSetter(ConcreteClassA.class, "fieldCollection").getGenericParameterTypes()[0]).getActualTypeArguments()[0];
        concreteType = ReflectionUtil.resolveTypeVariable(ConcreteClassA.class, typeVariable);
        assertEquals(Integer.class, concreteType);
        
        typeVariable = (TypeVariable<?>)((ParameterizedType)ReflectionUtil.getSetter(ConcreteClassA.class, "fieldMap").getGenericParameterTypes()[0]).getActualTypeArguments()[0];
        concreteType = ReflectionUtil.resolveTypeVariable(ConcreteClassA.class, typeVariable);
        assertEquals(Integer.class, concreteType);
        
        // Resolve method throws types
        
        typeVariable = (TypeVariable<?>)ReflectionUtil.getMethod(ConcreteClassA.class, "throwsException").getGenericExceptionTypes()[0];
        concreteType = ReflectionUtil.resolveTypeVariable(ConcreteClassA.class, typeVariable);
        assertEquals(Exception.class, concreteType);
    }

    /**
     * Test of getMethodExceptionTypes method, of class ReflectionUtil.
     */
    @Test
    public void testGetMethodExceptionTypes() throws Exception {
        assertEquals(Exception.class, ReflectionUtil.getMethodExceptionTypes(ConcreteClassA.class, "throwsException")[0]);
        assertNull(ReflectionUtil.getMethodExceptionTypes(ConcreteClassA.class, ""));
    }

    /**
     * Test of getResolvedMethodReturnType method, of class ReflectionUtil.
     */
    @Test
    public void testGetResolvedMethodReturnType() throws Exception {
        assertEquals(Integer.class, ReflectionUtil.getResolvedMethodReturnType(ConcreteClassA.class, ReflectionUtil.getGetter(ConcreteClassA.class, "field")));
        assertNull(ReflectionUtil.getResolvedMethodReturnType(ConcreteClassA.class, ""));
    }

    /**
     * Test of getResolvedMethodReturnTypeArguments method, of class ReflectionUtil.
     */
    @Test
    public void testGetResolvedMethodReturnTypeArguments() throws Exception {
        assertEquals(Integer.class, ReflectionUtil.getResolvedMethodReturnTypeArguments(ConcreteClassA.class, ReflectionUtil.getGetter(ConcreteClassA.class, "fieldCollection"))[0]);
        assertEquals(Integer.class, ReflectionUtil.getResolvedMethodReturnTypeArguments(ConcreteClassA.class, ReflectionUtil.getGetter(ConcreteClassA.class, "fieldMap"))[0]);
        assertArrayEquals(new Class<?>[0], ReflectionUtil.getResolvedMethodReturnTypeArguments(ConcreteClassA.class, "throwsException"));
        assertNull(ReflectionUtil.getResolvedMethodReturnTypeArguments(ConcreteClassA.class, ""));
    }

    /**
     * Test of getResolvedMethodParameterTypes method, of class ReflectionUtil.
     */
    @Test
    public void testGetResolvedMethodParameterTypes() throws Exception {
        assertEquals(Integer.class, ReflectionUtil.getResolvedMethodParameterTypes(ConcreteClassA.class, ReflectionUtil.getSetter(ConcreteClassA.class, "field"))[0]);
        assertArrayEquals(new Class<?>[0], ReflectionUtil.getResolvedMethodParameterTypes(ConcreteClassA.class, "throwsException"));
        assertNull(ReflectionUtil.getResolvedMethodParameterTypes(ConcreteClassA.class, ""));
    }

    /**
     * Test of getResolvedMethodExceptionTypes method, of class ReflectionUtil.
     */
    @Test
    public void testGetResolvedMethodExceptionTypes() throws Exception {
        assertEquals(Exception.class, ReflectionUtil.getResolvedMethodExceptionTypes(ConcreteClassA.class, ReflectionUtil.getMethod(ConcreteClassA.class, "throwsException"))[0]);
        assertArrayEquals(new Class<?>[0], ReflectionUtil.getResolvedMethodExceptionTypes(ConcreteClassA.class, "getField"));
        assertNull(ReflectionUtil.getResolvedMethodExceptionTypes(ConcreteClassA.class, ""));
    }

    /**
     * Test of getClass method, of class ReflectionUtil.
     */
    @Test
    public void testGetClass() throws Exception {
        assertEquals(ReflectionUtil.getClass("int"), Integer.TYPE);
        assertEquals(ReflectionUtil.getClass("long"), Long.TYPE);
        assertEquals(ReflectionUtil.getClass("double"), Double.TYPE);
        assertEquals(ReflectionUtil.getClass("float"), Float.TYPE);
        assertEquals(ReflectionUtil.getClass("boolean"), Boolean.TYPE);
        assertEquals(ReflectionUtil.getClass("char"), Character.TYPE);
        assertEquals(ReflectionUtil.getClass("byte"), Byte.TYPE);
        assertEquals(ReflectionUtil.getClass("void"), Void.TYPE);
        assertEquals(ReflectionUtil.getClass("short"), Short.TYPE);
        assertEquals(ReflectionUtil.getClass("java.lang.Object"), Object.class);
    }

    /**
     * Test of isSubtype method, of class ReflectionUtil.
     */
    @Test
    public void testIsSubtype() {
        assertTrue(ReflectionUtil.isSubtype(ClassB.class, ClassB.class));
        assertTrue(ReflectionUtil.isSubtype(ClassB.class, ClassA.class));
        assertTrue(ReflectionUtil.isSubtype(ClassB.class, B.class));
        assertTrue(ReflectionUtil.isSubtype(ClassB.class, A.class));

        assertTrue(ReflectionUtil.isSubtype(ClassA.class, ClassA.class));
        assertTrue(ReflectionUtil.isSubtype(ClassA.class, A.class));

        assertFalse(ReflectionUtil.isSubtype(ClassA.class, ClassB.class));
        assertFalse(ReflectionUtil.isSubtype(ClassA.class, B.class));
    }
    
    /**
     * Test of getSuperTypes method, of class ReflectionUtil.
     */
    @Test
    public void testGetSuperTypes(){
        assertTrue(ReflectionUtil.getSuperTypes(ClassB.class).contains(ClassB.class));
        assertTrue(ReflectionUtil.getSuperTypes(ClassB.class).contains(ClassA.class));
        assertTrue(ReflectionUtil.getSuperTypes(ClassB.class).contains(A.class));
        assertTrue(ReflectionUtil.getSuperTypes(ClassB.class).contains(B.class));
        assertTrue(ReflectionUtil.getSuperTypes(ClassA.class).contains(ClassA.class));
        assertTrue(ReflectionUtil.getSuperTypes(ClassA.class).contains(A.class));
        assertTrue(ReflectionUtil.getSuperTypes(C.class).contains(C.class));
        assertTrue(ReflectionUtil.getSuperTypes(C.class).contains(A.class));
        assertTrue(ReflectionUtil.getSuperTypes(B.class).contains(B.class));
        assertTrue(ReflectionUtil.getSuperTypes(A.class).contains(A.class));
    }

    /**
     * Test of getFieldType method, of class ReflectionUtil.
     */
    @Test
    public void testGetFieldType() {
        assertEquals(ReflectionUtil.getFieldType(ClassB.class, "B_FIELD"), Integer.class);
        assertEquals(ReflectionUtil.getFieldType(ClassB.class, "A_FIELD"), String.class);
        assertNull(ReflectionUtil.getFieldType(ClassA.class, "B_FIELD"));
        assertEquals(ReflectionUtil.getFieldType(ClassA.class, "A_FIELD"), String.class);
        assertEquals(ReflectionUtil.getFieldType(A.class, "A_FIELD"), String.class);
        assertEquals(ReflectionUtil.getFieldType(B.class, "B_FIELD"), Integer.class);
        assertNull(ReflectionUtil.getFieldType(B.class, "A_FIELD"));
        assertEquals(ReflectionUtil.getFieldType(C.class, "C_FIELD"), boolean.class);
        assertNull(ReflectionUtil.getFieldType(C.class, "B_FIELD"));
        assertEquals(ReflectionUtil.getFieldType(C.class, "A_FIELD"), String.class);
    }

    /**
     * Test of getField method, of class ReflectionUtil.
     */
    @Test
    public void testGetField() {
        assertNotNull(ReflectionUtil.getField(ClassB.class, "B_FIELD"));
        assertNotNull(ReflectionUtil.getField(ClassB.class, "A_FIELD"));
        assertNull(ReflectionUtil.getField(ClassA.class, "B_FIELD"));
        assertNotNull(ReflectionUtil.getField(ClassA.class, "A_FIELD"));
        assertNotNull(ReflectionUtil.getField(A.class, "A_FIELD"));
        assertNotNull(ReflectionUtil.getField(B.class, "B_FIELD"));
        assertNull(ReflectionUtil.getField(B.class, "A_FIELD"));
        assertNotNull(ReflectionUtil.getField(C.class, "C_FIELD"));
        assertNull(ReflectionUtil.getField(C.class, "B_FIELD"));
        assertNotNull(ReflectionUtil.getField(C.class, "A_FIELD"));
    }

    /**
     * Test of getMethodType method, of class ReflectionUtil.
     */
    @Test
    public void testGetMethodType() {
        assertEquals(ReflectionUtil.getMethodReturnType(ClassB.class, "getA"), String.class);
        assertEquals(ReflectionUtil.getMethodReturnType(ClassA.class, "getA"), String.class);
        assertEquals(ReflectionUtil.getMethodReturnType(A.class, "getA"), String.class);
        assertEquals(ReflectionUtil.getMethodReturnType(C.class, "getA"), String.class);
        assertNull(ReflectionUtil.getMethodReturnType(B.class, "getA", String.class));
        assertEquals(ReflectionUtil.getMethodReturnType(ClassB.class, "isB"), boolean.class);
        assertEquals(ReflectionUtil.getMethodReturnType(ClassA.class, "isB"), boolean.class);
        assertEquals(ReflectionUtil.getMethodReturnType(A.class, "isB"), boolean.class);
        assertEquals(ReflectionUtil.getMethodReturnType(C.class, "isB"), boolean.class);
        assertNull(ReflectionUtil.getMethodReturnType(B.class, "isB"));
        
        assertEquals(ReflectionUtil.getMethodReturnType(ClassB.class, "setA", String.class), void.class);
        assertEquals(ReflectionUtil.getMethodReturnType(ClassA.class, "setA", String.class), void.class);
        assertEquals(ReflectionUtil.getMethodReturnType(A.class, "setA", String.class), void.class);
        assertEquals(ReflectionUtil.getMethodReturnType(C.class, "setA", String.class), void.class);
        assertNull(ReflectionUtil.getMethodReturnType(B.class, "setA", String.class));
        assertEquals(ReflectionUtil.getMethodReturnType(ClassB.class, "setB", boolean.class), void.class);
        assertEquals(ReflectionUtil.getMethodReturnType(ClassA.class, "setB", boolean.class), void.class);
        assertEquals(ReflectionUtil.getMethodReturnType(A.class, "setB", boolean.class), void.class);
        assertEquals(ReflectionUtil.getMethodReturnType(C.class, "setB", boolean.class), void.class);
        assertNull(ReflectionUtil.getMethodReturnType(B.class, "setB", boolean.class));
    }

    /**
     * Test of getMethod method, of class ReflectionUtil.
     */
    @Test
    public void testGetMethod() {
        assertNotNull(ReflectionUtil.getMethod(ClassB.class, "getA"));
        assertNotNull(ReflectionUtil.getMethod(ClassA.class, "getA"));
        assertNotNull(ReflectionUtil.getMethod(A.class, "getA"));
        assertNotNull(ReflectionUtil.getMethod(C.class, "getA"));
        assertNull(ReflectionUtil.getMethod(B.class, "getA"));
        assertNotNull(ReflectionUtil.getMethod(ClassB.class, "isB"));
        assertNotNull(ReflectionUtil.getMethod(ClassA.class, "isB"));
        assertNotNull(ReflectionUtil.getMethod(A.class, "isB"));
        assertNotNull(ReflectionUtil.getMethod(C.class, "isB"));
        assertNull(ReflectionUtil.getMethod(B.class, "isB"));
        
        assertNotNull(ReflectionUtil.getMethod(ClassB.class, "setA", String.class));
        assertNotNull(ReflectionUtil.getMethod(ClassA.class, "setA", String.class));
        assertNotNull(ReflectionUtil.getMethod(A.class, "setA", String.class));
        assertNotNull(ReflectionUtil.getMethod(C.class, "setA", String.class));
        assertNull(ReflectionUtil.getMethod(B.class, "setA", String.class));
        assertNotNull(ReflectionUtil.getMethod(ClassB.class, "setB", boolean.class));
        assertNotNull(ReflectionUtil.getMethod(ClassA.class, "setB", boolean.class));
        assertNotNull(ReflectionUtil.getMethod(A.class, "setB", boolean.class));
        assertNotNull(ReflectionUtil.getMethod(C.class, "setB", boolean.class));
        assertNull(ReflectionUtil.getMethod(B.class, "setB", boolean.class));
    }

    /**
     * Test of getGetter method, of class ReflectionUtil.
     */
    @Test
    public void testGetGetter() {
        assertNotNull(ReflectionUtil.getGetter(ClassB.class, "a"));
        assertNotNull(ReflectionUtil.getGetter(ClassA.class, "a"));
        assertNotNull(ReflectionUtil.getGetter(A.class, "a"));
        assertNotNull(ReflectionUtil.getGetter(C.class, "a"));
        assertNull(ReflectionUtil.getGetter(B.class, "a"));
        assertNotNull(ReflectionUtil.getGetter(ClassB.class, "b"));
        assertNotNull(ReflectionUtil.getGetter(ClassA.class, "b"));
        assertNotNull(ReflectionUtil.getGetter(A.class, "b"));
        assertNotNull(ReflectionUtil.getGetter(C.class, "b"));
        assertNull(ReflectionUtil.getGetter(B.class, "b"));
    }

    /**
     * Test of getSetter method, of class ReflectionUtil.
     */
    @Test
    public void testGetSetter() {
        assertNotNull(ReflectionUtil.getSetter(ClassB.class, "a"));
        assertNotNull(ReflectionUtil.getSetter(ClassA.class, "a"));
        assertNotNull(ReflectionUtil.getSetter(A.class, "a"));
        assertNotNull(ReflectionUtil.getSetter(C.class, "a"));
        assertNull(ReflectionUtil.getSetter(B.class, "a"));
        assertNotNull(ReflectionUtil.getSetter(ClassB.class, "b"));
        assertNotNull(ReflectionUtil.getSetter(ClassA.class, "b"));
        assertNotNull(ReflectionUtil.getSetter(A.class, "b"));
        assertNotNull(ReflectionUtil.getSetter(C.class, "b"));
        assertNull(ReflectionUtil.getSetter(B.class, "b"));
    }
}
