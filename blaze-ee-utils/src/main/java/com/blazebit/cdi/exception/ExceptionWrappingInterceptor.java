/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.exception;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.blazebit.annotation.AnnotationUtils;
import com.blazebit.cdi.exception.annotation.ExceptionWrap;
import com.blazebit.cdi.exception.annotation.ExceptionWrapping;
import com.blazebit.exception.ExceptionUtils;

/**
 * This interceptor simply wraps exceptions which are declared as sources in the
 * ExceptionWrap annotations into the exception type "wrapper" which is also
 * declared in the ExceptionWrap annotation. It ignores exceptions which are
 * declared in the throws clause of the method for which this interceptor runs.
 * 
 * <pre>
 * <code>
 * public class Bean implements Serializable {
 * 
 * 	@ExceptionWrapping(value = {
 * 			@ExceptionWrap(source = RuntimeExceptionA.class, wrapper = MyExceptionA.class),
 * 			@ExceptionWrap(source = RuntimeExceptionB.class, wrapper = MyExceptionB.class),
 * 			@ExceptionWrap(wrapper = MyExceptionC.class) })
 * 	public void example() throws MyExceptionA, MyExceptionB, MyExceptionC {
 * 		// code
 * 	}
 * }
 * </code>
 * </pre>
 * 
 * RuntimeExceptionA will be wrapped into MyExceptionA RuntimeExceptionB will be
 * wrapped into MyExceptionB Every other Exception, that is not instance of
 * MyExceptionA, MyExceptionB or MyExceptionC, will be wrapped into MyExceptionC
 * 
 * The order of ExceptionWrap annotations within the ExceptionWrapping
 * annotation is important and should be declared from specific to general.
 * 
 * If you would specify <code>@ExceptionWrap( wrapper=MyExceptionC.class)</code>
 * as the first element of the ExceptionWrapping annotation, then every
 * exception that is not declared in the throws clause will be wrapped into a
 * MyExceptionC!
 * 
 * @since 0.1.2
 * @author Christian Beikov
 * @see ExceptionWrapping
 * @see ExceptionWrap
 * 
 */
@Interceptor
@ExceptionWrapping(value = {})
public class ExceptionWrappingInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;

	@AroundInvoke
	public Object errorLogging(InvocationContext ic) throws Exception {
		/* Retrieve the logger for the intercepted class */
		Logger log = Logger.getLogger(ic.getTarget().getClass().getName());
		Method m = ic.getMethod();
		Object targetObject = ic.getTarget();
		Class<?> targetClass = targetObject == null ? m.getDeclaringClass()
				: targetObject.getClass();
		/*
		 * Retrieve the exceptions declared in the throws clause of the
		 * intercepted method
		 */
		Class<?>[] declaredExceptions = m.getExceptionTypes();
		ExceptionWrapping wrappingAnnotation = AnnotationUtils.findAnnotation(
				m, targetClass, ExceptionWrapping.class);
		ExceptionWrap[] wraps = null;
		Object ret;
		boolean doWrapping = true;

		if (wrappingAnnotation == null) {
			throw new IllegalStateException(
					"The interceptor annotation can not be determined!");
		}

		wraps = wrappingAnnotation.value();

		/* Only try to do wrapping when exceptionWrap elements are available */
		doWrapping = wraps != null && wraps.length > 0;

		try {
			ret = ic.proceed();
		} catch (Throwable t) {
			/*
			 * Check if the throwable is an instance of
			 * InvocationTargetException and if so, unwrap the cause. OWB did
			 * not unwrap exceptions that have been thrown in decorators in some
			 * versions so we need to do this to be able to handle the right
			 * exception
			 */
			Throwable t1 = ExceptionUtils.unwrap(t,
					InvocationTargetException.class);

			if (doWrapping) {
				/*
				 * Check if the exception that was thrown is declared in the
				 * throws clause and if so, don't apply exception wrapping
				 */
				for (Class<?> declaredException : declaredExceptions) {
					if (declaredException.isInstance(t1)) {
						/* Do this so no exception wrapper is applied */
						doWrapping = false;
						break;
					}
				}

				if (doWrapping) {
					/*
					 * If a wrapping should be applied, iterate through
					 * exception wraps and check if the thrown exception is an
					 * instance of a declared source class.
					 */
					for (ExceptionWrap wrap : wraps) {
						Class<? extends Throwable>[] sourceClasses = wrap
								.sources();
						Class<? extends Exception> wrapperClass = wrap
								.wrapper();
						Exception e = null;

						/*
						 * Only do wrapping if the exception isn't already a
						 * instance of the wrapper exceptionF
						 */
						if (!wrapperClass.isInstance(t1)) {
							for (Class<? extends Throwable> source : sourceClasses) {
								/*
								 * When the thrown exception is instance of an
								 * exception wrap source class, create a new
								 * exception instance with the thrown exception
								 * as cause
								 */
								if (source.isInstance(t1)) {
									try {
										e = wrapperClass.getConstructor(
												Throwable.class)
												.newInstance(t1);
									} catch (Throwable t2) {
										/*
										 * Declared wrapper exception has no
										 * constructor with a throwable as param
										 */
										Exception ex1 = null;

										/*
										 * Cast to or wrap the throwable into a
										 * new exception because we may not
										 * throw Throwable instances within here
										 */
										if (t2 instanceof Exception) {
											ex1 = (Exception) t2;
										} else {
											ex1 = new Exception(t2);
										}

										log.log(Level.WARNING,
												"The applied wrapper exception on the method "
														+ m.getName()
														+ " has no constructor for type Throwable!",
												ex1);
									}

									if (e != null) {
										throw e;
									}
								}
							}
						}
					}
				}
			}

			/*
			 * No exception wrapping was applied, so cast to or wrap the
			 * throwable into a new exception and throw it
			 */
			if (t1 instanceof Exception) {
				throw (Exception) t1;
			}

			throw new Exception(t1);
		}

		return ret;
	}
}
