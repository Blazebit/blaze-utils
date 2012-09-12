/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.reflection;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Map;

import org.junit.Test;

/**
 * 
 * @author Christian Beikov
 */
public class ReflectionUtilsTest {

	private interface A {
		@SuppressWarnings("unused")
		static String A_FIELD = "";

		String getA();

		boolean isB();

		void setA(String a);

		void setB(boolean b);
	}

	private interface B {
		@SuppressWarnings("unused")
		static Integer B_FIELD = 1;
	}

	private interface C extends A {
		@SuppressWarnings("unused")
		static boolean C_FIELD = true;
	}

	private class ClassA implements A {

		public String getA() {
			return "";
		}

		public void setA(String a) {

		}

		public boolean isB() {
			return false;
		}

		public void setB(boolean b) {

		}
	}

	private class ClassB extends ClassA implements B {

		@Override
		public boolean isB() {
			return true;
		}
	}

	private class GenericClassA<T, E extends Exception> {
		private T field;
		private Collection<T> fieldCollection;
		private Map<T, T> fieldMap;

		@SuppressWarnings("unused")
		public T getField() {
			return field;
		}

		@SuppressWarnings("unused")
		public void setField(T field) {
			this.field = field;
		}

		@SuppressWarnings("unused")
		public Collection<T> getFieldCollection() {
			return fieldCollection;
		}

		@SuppressWarnings("unused")
		public void setFieldCollection(Collection<T> fieldCollection) {
			this.fieldCollection = fieldCollection;
		}

		@SuppressWarnings("unused")
		public Map<T, T> getFieldMap() {
			return fieldMap;
		}

		@SuppressWarnings("unused")
		public void setFieldMap(Map<T, T> fieldMap) {
			this.fieldMap = fieldMap;
		}

		@SuppressWarnings("unused")
		public void throwsException() throws E {

		}
	}

	private class ConcreteClassA extends GenericClassA<Integer, Exception> {

	}

	/**
	 * Test of resolveTypeVariable method, of class ReflectionUtil.
	 */
	@Test
	public void testResolveTypeVariable() throws Exception {
		// Resolve field types

		TypeVariable<?> typeVariable = (TypeVariable<?>) ReflectionUtils
				.getField(ConcreteClassA.class, "field").getGenericType();
		Class<?> concreteType = ReflectionUtils.resolveTypeVariable(
				ConcreteClassA.class, typeVariable);
		assertEquals(Integer.class, concreteType);

		typeVariable = (TypeVariable<?>) ((ParameterizedType) ReflectionUtils
				.getField(ConcreteClassA.class, "fieldCollection")
				.getGenericType()).getActualTypeArguments()[0];
		concreteType = ReflectionUtils.resolveTypeVariable(ConcreteClassA.class,
				typeVariable);
		assertEquals(Integer.class, concreteType);

		typeVariable = (TypeVariable<?>) ((ParameterizedType) ReflectionUtils
				.getField(ConcreteClassA.class, "fieldMap").getGenericType())
				.getActualTypeArguments()[0];
		concreteType = ReflectionUtils.resolveTypeVariable(ConcreteClassA.class,
				typeVariable);
		assertEquals(Integer.class, concreteType);

		// Resolve method return types

		typeVariable = (TypeVariable<?>) ReflectionUtils.getGetter(
				ConcreteClassA.class, "field").getGenericReturnType();
		concreteType = ReflectionUtils.resolveTypeVariable(ConcreteClassA.class,
				typeVariable);
		assertEquals(Integer.class, concreteType);

		typeVariable = (TypeVariable<?>) ((ParameterizedType) ReflectionUtils
				.getGetter(ConcreteClassA.class, "fieldCollection")
				.getGenericReturnType()).getActualTypeArguments()[0];
		concreteType = ReflectionUtils.resolveTypeVariable(ConcreteClassA.class,
				typeVariable);
		assertEquals(Integer.class, concreteType);

		typeVariable = (TypeVariable<?>) ((ParameterizedType) ReflectionUtils
				.getGetter(ConcreteClassA.class, "fieldMap")
				.getGenericReturnType()).getActualTypeArguments()[0];
		concreteType = ReflectionUtils.resolveTypeVariable(ConcreteClassA.class,
				typeVariable);
		assertEquals(Integer.class, concreteType);

		// Resolve method parameter types

		typeVariable = (TypeVariable<?>) ReflectionUtils.getSetter(
				ConcreteClassA.class, "field").getGenericParameterTypes()[0];
		concreteType = ReflectionUtils.resolveTypeVariable(ConcreteClassA.class,
				typeVariable);
		assertEquals(Integer.class, concreteType);

		typeVariable = (TypeVariable<?>) ((ParameterizedType) ReflectionUtils
				.getSetter(ConcreteClassA.class, "fieldCollection")
				.getGenericParameterTypes()[0]).getActualTypeArguments()[0];
		concreteType = ReflectionUtils.resolveTypeVariable(ConcreteClassA.class,
				typeVariable);
		assertEquals(Integer.class, concreteType);

		typeVariable = (TypeVariable<?>) ((ParameterizedType) ReflectionUtils
				.getSetter(ConcreteClassA.class, "fieldMap")
				.getGenericParameterTypes()[0]).getActualTypeArguments()[0];
		concreteType = ReflectionUtils.resolveTypeVariable(ConcreteClassA.class,
				typeVariable);
		assertEquals(Integer.class, concreteType);

		// Resolve method throws types

		typeVariable = (TypeVariable<?>) ReflectionUtils.getMethod(
				ConcreteClassA.class, "throwsException")
				.getGenericExceptionTypes()[0];
		concreteType = ReflectionUtils.resolveTypeVariable(ConcreteClassA.class,
				typeVariable);
		assertEquals(Exception.class, concreteType);
	}

	/**
	 * Test of getMethodExceptionTypes method, of class ReflectionUtil.
	 */
	@Test
	public void testGetMethodExceptionTypes() throws Exception {
		assertEquals(Exception.class, ReflectionUtils.getMethodExceptionTypes(
				ConcreteClassA.class, "throwsException")[0]);
		assertNull(ReflectionUtils.getMethodExceptionTypes(ConcreteClassA.class,
				""));
	}

	/**
	 * Test of getResolvedMethodReturnType method, of class ReflectionUtil.
	 */
	@Test
	public void testGetResolvedMethodReturnType() throws Exception {
		assertEquals(Integer.class, ReflectionUtils.getResolvedMethodReturnType(
				ConcreteClassA.class,
				ReflectionUtils.getGetter(ConcreteClassA.class, "field")));
		assertNull(ReflectionUtils.getResolvedMethodReturnType(
				ConcreteClassA.class, ""));
	}

	/**
	 * Test of getResolvedMethodReturnTypeArguments method, of class
	 * ReflectionUtil.
	 */
	@Test
	public void testGetResolvedMethodReturnTypeArguments() throws Exception {
		assertEquals(Integer.class,
				ReflectionUtils.getResolvedMethodReturnTypeArguments(
						ConcreteClassA.class, ReflectionUtils.getGetter(
								ConcreteClassA.class, "fieldCollection"))[0]);
		assertEquals(Integer.class,
				ReflectionUtils.getResolvedMethodReturnTypeArguments(
						ConcreteClassA.class, ReflectionUtils.getGetter(
								ConcreteClassA.class, "fieldMap"))[0]);
		assertArrayEquals(new Class<?>[0],
				ReflectionUtils.getResolvedMethodReturnTypeArguments(
						ConcreteClassA.class, "throwsException"));
		assertNull(ReflectionUtils.getResolvedMethodReturnTypeArguments(
				ConcreteClassA.class, ""));
	}

	/**
	 * Test of getResolvedMethodParameterTypes method, of class ReflectionUtil.
	 */
	@Test
	public void testGetResolvedMethodParameterTypes() throws Exception {
		assertEquals(
				Integer.class,
				ReflectionUtils.getResolvedMethodParameterTypes(
						ConcreteClassA.class,
						ReflectionUtils.getSetter(ConcreteClassA.class, "field"))[0]);
		assertArrayEquals(new Class<?>[0],
				ReflectionUtils.getResolvedMethodParameterTypes(
						ConcreteClassA.class, "throwsException"));
		assertNull(ReflectionUtils.getResolvedMethodParameterTypes(
				ConcreteClassA.class, ""));
	}

	/**
	 * Test of getResolvedMethodExceptionTypes method, of class ReflectionUtil.
	 */
	@Test
	public void testGetResolvedMethodExceptionTypes() throws Exception {
		assertEquals(Exception.class,
				ReflectionUtils.getResolvedMethodExceptionTypes(
						ConcreteClassA.class, ReflectionUtils.getMethod(
								ConcreteClassA.class, "throwsException"))[0]);
		assertArrayEquals(new Class<?>[0],
				ReflectionUtils.getResolvedMethodExceptionTypes(
						ConcreteClassA.class, "getField"));
		assertNull(ReflectionUtils.getResolvedMethodExceptionTypes(
				ConcreteClassA.class, ""));
	}

	/**
	 * Test of getClass method, of class ReflectionUtil.
	 */
	@Test
	public void testGetClass() throws Exception {
		assertEquals(ReflectionUtils.getClass("int"), Integer.TYPE);
		assertEquals(ReflectionUtils.getClass("long"), Long.TYPE);
		assertEquals(ReflectionUtils.getClass("double"), Double.TYPE);
		assertEquals(ReflectionUtils.getClass("float"), Float.TYPE);
		assertEquals(ReflectionUtils.getClass("boolean"), Boolean.TYPE);
		assertEquals(ReflectionUtils.getClass("char"), Character.TYPE);
		assertEquals(ReflectionUtils.getClass("byte"), Byte.TYPE);
		assertEquals(ReflectionUtils.getClass("void"), Void.TYPE);
		assertEquals(ReflectionUtils.getClass("short"), Short.TYPE);
		assertEquals(ReflectionUtils.getClass("java.lang.Object"), Object.class);
	}

	/**
	 * Test of isSubtype method, of class ReflectionUtil.
	 */
	@Test
	public void testIsSubtype() {
		assertTrue(ReflectionUtils.isSubtype(ClassB.class, ClassB.class));
		assertTrue(ReflectionUtils.isSubtype(ClassB.class, ClassA.class));
		assertTrue(ReflectionUtils.isSubtype(ClassB.class, B.class));
		assertTrue(ReflectionUtils.isSubtype(ClassB.class, A.class));

		assertTrue(ReflectionUtils.isSubtype(ClassA.class, ClassA.class));
		assertTrue(ReflectionUtils.isSubtype(ClassA.class, A.class));

		assertFalse(ReflectionUtils.isSubtype(ClassA.class, ClassB.class));
		assertFalse(ReflectionUtils.isSubtype(ClassA.class, B.class));
	}

	/**
	 * Test of getSuperTypes method, of class ReflectionUtil.
	 */
	@Test
	public void testGetSuperTypes() {
		assertTrue(ReflectionUtils.getSuperTypes(ClassB.class).contains(
				ClassB.class));
		assertTrue(ReflectionUtils.getSuperTypes(ClassB.class).contains(
				ClassA.class));
		assertTrue(ReflectionUtils.getSuperTypes(ClassB.class).contains(A.class));
		assertTrue(ReflectionUtils.getSuperTypes(ClassB.class).contains(B.class));
		assertTrue(ReflectionUtils.getSuperTypes(ClassA.class).contains(
				ClassA.class));
		assertTrue(ReflectionUtils.getSuperTypes(ClassA.class).contains(A.class));
		assertTrue(ReflectionUtils.getSuperTypes(C.class).contains(C.class));
		assertTrue(ReflectionUtils.getSuperTypes(C.class).contains(A.class));
		assertTrue(ReflectionUtils.getSuperTypes(B.class).contains(B.class));
		assertTrue(ReflectionUtils.getSuperTypes(A.class).contains(A.class));
	}

	/**
	 * Test of getFieldType method, of class ReflectionUtil.
	 */
	@Test
	public void testGetFieldType() {
		assertEquals(ReflectionUtils.getFieldType(ClassB.class, "B_FIELD"),
				Integer.class);
		assertEquals(ReflectionUtils.getFieldType(ClassB.class, "A_FIELD"),
				String.class);
		assertNull(ReflectionUtils.getFieldType(ClassA.class, "B_FIELD"));
		assertEquals(ReflectionUtils.getFieldType(ClassA.class, "A_FIELD"),
				String.class);
		assertEquals(ReflectionUtils.getFieldType(A.class, "A_FIELD"),
				String.class);
		assertEquals(ReflectionUtils.getFieldType(B.class, "B_FIELD"),
				Integer.class);
		assertNull(ReflectionUtils.getFieldType(B.class, "A_FIELD"));
		assertEquals(ReflectionUtils.getFieldType(C.class, "C_FIELD"),
				boolean.class);
		assertNull(ReflectionUtils.getFieldType(C.class, "B_FIELD"));
		assertEquals(ReflectionUtils.getFieldType(C.class, "A_FIELD"),
				String.class);
	}

	/**
	 * Test of getField method, of class ReflectionUtil.
	 */
	@Test
	public void testGetField() {
		assertNotNull(ReflectionUtils.getField(ClassB.class, "B_FIELD"));
		assertNotNull(ReflectionUtils.getField(ClassB.class, "A_FIELD"));
		assertNull(ReflectionUtils.getField(ClassA.class, "B_FIELD"));
		assertNotNull(ReflectionUtils.getField(ClassA.class, "A_FIELD"));
		assertNotNull(ReflectionUtils.getField(A.class, "A_FIELD"));
		assertNotNull(ReflectionUtils.getField(B.class, "B_FIELD"));
		assertNull(ReflectionUtils.getField(B.class, "A_FIELD"));
		assertNotNull(ReflectionUtils.getField(C.class, "C_FIELD"));
		assertNull(ReflectionUtils.getField(C.class, "B_FIELD"));
		assertNotNull(ReflectionUtils.getField(C.class, "A_FIELD"));
	}

	/**
	 * Test of getMethodType method, of class ReflectionUtil.
	 */
	@Test
	public void testGetMethodType() {
		assertEquals(ReflectionUtils.getMethodReturnType(ClassB.class, "getA"),
				String.class);
		assertEquals(ReflectionUtils.getMethodReturnType(ClassA.class, "getA"),
				String.class);
		assertEquals(ReflectionUtils.getMethodReturnType(A.class, "getA"),
				String.class);
		assertEquals(ReflectionUtils.getMethodReturnType(C.class, "getA"),
				String.class);
		assertNull(ReflectionUtils.getMethodReturnType(B.class, "getA",
				String.class));
		assertEquals(ReflectionUtils.getMethodReturnType(ClassB.class, "isB"),
				boolean.class);
		assertEquals(ReflectionUtils.getMethodReturnType(ClassA.class, "isB"),
				boolean.class);
		assertEquals(ReflectionUtils.getMethodReturnType(A.class, "isB"),
				boolean.class);
		assertEquals(ReflectionUtils.getMethodReturnType(C.class, "isB"),
				boolean.class);
		assertNull(ReflectionUtils.getMethodReturnType(B.class, "isB"));

		assertEquals(ReflectionUtils.getMethodReturnType(ClassB.class, "setA",
				String.class), void.class);
		assertEquals(ReflectionUtils.getMethodReturnType(ClassA.class, "setA",
				String.class), void.class);
		assertEquals(ReflectionUtils.getMethodReturnType(A.class, "setA",
				String.class), void.class);
		assertEquals(ReflectionUtils.getMethodReturnType(C.class, "setA",
				String.class), void.class);
		assertNull(ReflectionUtils.getMethodReturnType(B.class, "setA",
				String.class));
		assertEquals(ReflectionUtils.getMethodReturnType(ClassB.class, "setB",
				boolean.class), void.class);
		assertEquals(ReflectionUtils.getMethodReturnType(ClassA.class, "setB",
				boolean.class), void.class);
		assertEquals(ReflectionUtils.getMethodReturnType(A.class, "setB",
				boolean.class), void.class);
		assertEquals(ReflectionUtils.getMethodReturnType(C.class, "setB",
				boolean.class), void.class);
		assertNull(ReflectionUtils.getMethodReturnType(B.class, "setB",
				boolean.class));
	}

	/**
	 * Test of getMethod method, of class ReflectionUtil.
	 */
	@Test
	public void testGetMethod() {
		assertNotNull(ReflectionUtils.getMethod(ClassB.class, "getA"));
		assertNotNull(ReflectionUtils.getMethod(ClassA.class, "getA"));
		assertNotNull(ReflectionUtils.getMethod(A.class, "getA"));
		assertNotNull(ReflectionUtils.getMethod(C.class, "getA"));
		assertNull(ReflectionUtils.getMethod(B.class, "getA"));
		assertNotNull(ReflectionUtils.getMethod(ClassB.class, "isB"));
		assertNotNull(ReflectionUtils.getMethod(ClassA.class, "isB"));
		assertNotNull(ReflectionUtils.getMethod(A.class, "isB"));
		assertNotNull(ReflectionUtils.getMethod(C.class, "isB"));
		assertNull(ReflectionUtils.getMethod(B.class, "isB"));

		assertNotNull(ReflectionUtils.getMethod(ClassB.class, "setA",
				String.class));
		assertNotNull(ReflectionUtils.getMethod(ClassA.class, "setA",
				String.class));
		assertNotNull(ReflectionUtils.getMethod(A.class, "setA", String.class));
		assertNotNull(ReflectionUtils.getMethod(C.class, "setA", String.class));
		assertNull(ReflectionUtils.getMethod(B.class, "setA", String.class));
		assertNotNull(ReflectionUtils.getMethod(ClassB.class, "setB",
				boolean.class));
		assertNotNull(ReflectionUtils.getMethod(ClassA.class, "setB",
				boolean.class));
		assertNotNull(ReflectionUtils.getMethod(A.class, "setB", boolean.class));
		assertNotNull(ReflectionUtils.getMethod(C.class, "setB", boolean.class));
		assertNull(ReflectionUtils.getMethod(B.class, "setB", boolean.class));
	}

	/**
	 * Test of getGetter method, of class ReflectionUtil.
	 */
	@Test
	public void testGetGetter() {
		assertNotNull(ReflectionUtils.getGetter(ClassB.class, "a"));
		assertNotNull(ReflectionUtils.getGetter(ClassA.class, "a"));
		assertNotNull(ReflectionUtils.getGetter(A.class, "a"));
		assertNotNull(ReflectionUtils.getGetter(C.class, "a"));
		assertNull(ReflectionUtils.getGetter(B.class, "a"));
		assertNotNull(ReflectionUtils.getGetter(ClassB.class, "b"));
		assertNotNull(ReflectionUtils.getGetter(ClassA.class, "b"));
		assertNotNull(ReflectionUtils.getGetter(A.class, "b"));
		assertNotNull(ReflectionUtils.getGetter(C.class, "b"));
		assertNull(ReflectionUtils.getGetter(B.class, "b"));
	}

	/**
	 * Test of getSetter method, of class ReflectionUtil.
	 */
	@Test
	public void testGetSetter() {
		assertNotNull(ReflectionUtils.getSetter(ClassB.class, "a"));
		assertNotNull(ReflectionUtils.getSetter(ClassA.class, "a"));
		assertNotNull(ReflectionUtils.getSetter(A.class, "a"));
		assertNotNull(ReflectionUtils.getSetter(C.class, "a"));
		assertNull(ReflectionUtils.getSetter(B.class, "a"));
		assertNotNull(ReflectionUtils.getSetter(ClassB.class, "b"));
		assertNotNull(ReflectionUtils.getSetter(ClassA.class, "b"));
		assertNotNull(ReflectionUtils.getSetter(A.class, "b"));
		assertNotNull(ReflectionUtils.getSetter(C.class, "b"));
		assertNull(ReflectionUtils.getSetter(B.class, "b"));
	}
}
