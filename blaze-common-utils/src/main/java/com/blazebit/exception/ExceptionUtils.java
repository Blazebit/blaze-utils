/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.exception;

import java.lang.reflect.InvocationTargetException;

/**
 * Utility class for exception objects.
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public final class ExceptionUtils {

	private ExceptionUtils() {
	}

	/**
	 * Unwraps the throwable until it is no instance of the throwableClass.
	 * Basically this method unwraps the throwable instances by calling
	 * getCause() except the throwable element is an instance of
	 * InvocationTargetException. When an InvocationTargetException is given
	 * getTargetException() is used for unwrapping.
	 * 
	 * Be carefull when you use this method with a too general throwableClass.
	 * It might be, that getCause() returns the this element in some cases and
	 * this could lead to an endless loop!
	 * 
	 * @param t
	 *            The throwable to unwrap
	 * @param throwableClass
	 *            The class of which a throwable must be to be unwrapped
	 * @return The unwrapped throwable or null if no further causes can be
	 *         unwrapped
	 */
	@SafeVarargs
	public static Throwable unwrap(Throwable t,
			Class<? extends Throwable>... throwableClasses) {
		Throwable unwrapped = t;

		while (unwrapped != null && isInstance(unwrapped, throwableClasses)) {
			if (unwrapped.getCause() == null
					&& unwrapped instanceof InvocationTargetException) {
				// We do this here because an invocation target exception may
				// return null on getCause() but the throwable element we are
				// looking for can be accessed via getTargetException()
				unwrapped = ((InvocationTargetException) unwrapped)
						.getTargetException();
			} else {
				unwrapped = unwrapped.getCause();
			}
		}

		return unwrapped;
	}

	public static Throwable unwrap(Throwable t,
			Class<? extends Throwable> throwableClass) {
		Throwable unwrapped = t;

		while (unwrapped != null && throwableClass.isInstance(unwrapped)) {
			if (unwrapped.getCause() == null
					&& unwrapped instanceof InvocationTargetException) {
				// We do this here because an invocation target exception may
				// return null on getCause() but the throwable element we are
				// looking for can be accessed via getTargetException()
				unwrapped = ((InvocationTargetException) unwrapped)
						.getTargetException();
			} else {
				unwrapped = unwrapped.getCause();
			}
		}

		return unwrapped;
	}

	private static boolean isInstance(Throwable t,
			Class<? extends Throwable>[] throwableClasses) {
		for (int i = 0; i < throwableClasses.length; i++) {
			if (throwableClasses[i].isInstance(t)) {
				return true;
			}
		}

		return false;
	}
}
