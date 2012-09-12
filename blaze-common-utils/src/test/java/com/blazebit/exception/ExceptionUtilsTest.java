/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

/**
 * 
 * @author Christian Beikov
 */
public class ExceptionUtilsTest {

	/**
	 * Test of unwrapInvocationTargetException method, of class ExceptionUtil.
	 */
	@Test
	public void testUnwrapInvocationTargetException() {
		Throwable inner = new RuntimeException();
		Throwable outer = new InvocationTargetException(inner);
		assertEquals(ExceptionUtils.unwrapInvocationTargetException(outer),
				inner);
		outer = new InvocationTargetException(outer);
		assertEquals(ExceptionUtils.unwrapInvocationTargetException(outer),
				inner);
		assertEquals(ExceptionUtils.unwrapInvocationTargetException(inner),
				inner);
		assertNull(ExceptionUtils
				.unwrapInvocationTargetException(new InvocationTargetException(
						null)));
	}

	/**
	 * Test of unwrap method, of class ExceptionUtil.
	 */
	@Test
	public void testUnwrap() {
		Throwable inner = new RuntimeException();
		Throwable outer = new InvocationTargetException(inner);
		assertEquals(
				ExceptionUtils.unwrap(outer, InvocationTargetException.class),
				inner);
		Throwable outer1 = new Error(outer);
		assertEquals(ExceptionUtils.unwrap(outer1, Error.class), outer);
		assertNull(ExceptionUtils.unwrap(inner, RuntimeException.class));

	}
}
