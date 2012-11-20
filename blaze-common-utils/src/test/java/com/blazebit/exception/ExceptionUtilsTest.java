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
		assertEquals(ExceptionUtils.unwrap(outer, InvocationTargetException.class),
				inner);
		outer = new InvocationTargetException(outer);
		assertEquals(ExceptionUtils.unwrap(outer, InvocationTargetException.class),
				inner);
		assertEquals(ExceptionUtils.unwrap(inner, InvocationTargetException.class),
				inner);
		assertNull(ExceptionUtils
				.unwrap(new InvocationTargetException(
						null), InvocationTargetException.class));
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
