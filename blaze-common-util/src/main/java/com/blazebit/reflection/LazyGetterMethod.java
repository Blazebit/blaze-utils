/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class can be used to predefine a getter chain invocation but to be
 * invoked later. It holds the source object on which to invoke the getter chain
 * and the field names with which the getter methods are determined.
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class LazyGetterMethod {
	private Object source;
	private String[] fieldNames;

	/**
	 * Constructs a LazyGetterMethod object for the given source object and
	 * field names as a string separated by '.' (dots). Using this constructor
	 * is equal to #
	 * {@link LazyGetterMethod#LazyGetterMethod(java.lang.Object, java.lang.String[]) }
	 * with the second parameter <code>fieldNames.split("\\.")</code>.
	 * 
	 * @param source
	 *            The object on which to invoke the first getter
	 * @param fieldNames
	 *            The field names which should be used for the getter
	 *            determination
	 */
	public LazyGetterMethod(Object source, String fieldNames) {
		this(source, fieldNames.split("\\."));
	}

	/**
	 * Constructs a LazyGetterMethod object for the given source object and
	 * field names as a string array.
	 * 
	 * @param source
	 *            The object on which to invoke the first getter
	 * @param fieldNames
	 *            The field names which should be used for the getter
	 *            determination
	 */
	public LazyGetterMethod(Object source, String[] fieldNames) {
		if (source == null) {
			throw new NullPointerException("target");
		}

		this.source = source;
		this.fieldNames = fieldNames;
	}

	/**
	 * Invokes the getter chain based on the source object. First the source
	 * object is used as invocation target for the first getter then the results
	 * of the previous operations will be used for the invocation.
	 * 
	 * Example of how the chaining works:
	 * 
	 * class A{ B getB(){ // return b element } }
	 * 
	 * class B{ String getA(){ // return a element } }
	 * 
	 * new LazyGetterMethod(new A(), "a.b").invoke()
	 * 
	 * is equal to
	 * 
	 * new A().getB().getA()
	 * 
	 * @return The result of the last getter in the chain
	 * @throws InvocationTargetException
	 *             #{@link Method#invoke(java.lang.Object, java.lang.Object[]) }
	 * @throws IllegalAccessException
	 *             #{@link Method#invoke(java.lang.Object, java.lang.Object[]) }
	 */
	public Object invoke() throws InvocationTargetException,
			IllegalAccessException {
		Object current = source;

		for (String fieldName : fieldNames) {
			Method m = ReflectionUtil.getGetter(current.getClass(), fieldName);
			current = m.invoke(current);
		}

		return current;
	}
}
