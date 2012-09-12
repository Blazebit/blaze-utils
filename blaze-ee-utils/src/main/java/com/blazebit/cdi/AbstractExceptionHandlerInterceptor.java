/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.interceptor.InvocationContext;

import com.blazebit.annotation.AnnotationUtils;
import com.blazebit.cdi.cleanup.annotation.Cleanup;
import com.blazebit.exception.ExceptionUtils;

/**
 * This abstract ExceptionHandlerInterceptor is the base for every type safe
 * exception handler. An ExceptionHandler can have a clean up, which can be
 * declared by the attribute cleanupName. A method within the class scope, that
 * is annotated with the #{@link Cleanup} annotation and the declared name will
 * be executed in case, an exception occurred. An exception handling element can
 * override the behavior for a different exception type, by also declaring a
 * cleanup reference via cleanupName attribute. If the cleanup in the exception
 * handling annotation cannot be found but the cleanup of the exception handler
 * is available, then the cleanup of the exception handler is invoked. <br/>
 * With the ExceptionMessage annotation you can define a default message for the
 * particular exception. If you define the default Message in your
 * ExceptionHandling Annotation as a default value for the Enum parameter. Then
 * the default message of the exceptionMessage annotation will be used.
 * Otherwise define the Enum in the Exception handling annotation and it will be
 * used. <br/>
 * If you implement an ExceptionHandler please take a look at
 * CoreExceptionHandler and take this implementation as an default
 * implementation
 * 
 * @param <T>
 * @param <V>
 * @param <M>
 * @todo Rethink the exception handling and evaluate better methods
 * @author Christian Beikov
 * @since 0.1.2
 * 
 */
public abstract class AbstractExceptionHandlerInterceptor<T extends Annotation, V extends Annotation, M extends Enum<M>> {

	private Class<T> clazz;
	private M defaultMessage;
	private Class<V> defaultMessageAnnotation;

	public AbstractExceptionHandlerInterceptor() {
		super();
	}

	public AbstractExceptionHandlerInterceptor(Class<T> clazz,
			M defaultMessage, Class<V> defaultMessageAnnotation) {
		this.clazz = clazz;
		this.defaultMessage = defaultMessage;
		this.defaultMessageAnnotation = defaultMessageAnnotation;
	}

	public Object handleError(InvocationContext ic) throws Exception {
		Object targetObj = ic.getTarget();
		Object ret = null;

		// Avoid ExceptionHandling for @Cleanup annotated methods could
		// result in a forever loop
		if (ic.getMethod().isAnnotationPresent(Cleanup.class)) {
			return ic.proceed();
		}

		try {
			ret = ic.proceed();
		} catch (Throwable t) {

			// Unwrap Exception if t is instanceof InvocationTargetException
			if (t instanceof InvocationTargetException) {
				t = ExceptionUtils
						.unwrapInvocationTargetException((InvocationTargetException) t);
			}

			// Method level exception handling is preferred
			if (!handleException(ic.getMethod().getAnnotation(clazz), ic,
					targetObj, t)) {
				// Class level exception handling if not handled by method level
				if (!handleException(AnnotationUtils.findAnnotation(
						targetObj.getClass(), clazz), ic, targetObj, t)) {
					if (t instanceof Exception) {
						throw (Exception) t;
					}
					throw new Exception(t);
				}
			}
		}
		return ret;
	}

	/**
	 * Handles the exception.
	 * 
	 * @param handler
	 *            the handler, class or method level
	 * @param ic
	 *            the InvocationContext
	 * @param t
	 *            the thrown throwable
	 * @throws Exception
	 *             if an error occurs the handling
	 */
	@SuppressWarnings("unchecked")
	private boolean handleException(T handler, InvocationContext ic,
			Object targetObj, Throwable t) throws Exception {
		Object[] handlings = null;

		if (handler != null) {

			handlings = (Object[]) clazz.getMethod("value").invoke(handler);

			// Unwrap Exception if t is instanceof InvocationTargetException
			if (t instanceof InvocationTargetException) {
				t = ExceptionUtils
						.unwrapInvocationTargetException((InvocationTargetException) t);
			}

			for (Object exHandle : handlings) {
				if (t.getClass().equals(
						(Class<? extends Exception>) exHandle.getClass()
								.getMethod("exception").invoke(exHandle))) {
					// logError(log,
					// ic.getMethod().getDeclaringClass().getSimpleName(),
					// ic.getMethod().getName(), t);
					Enum<M> message = (Enum<M>) exHandle.getClass()
							.getMethod("message").invoke(exHandle);

					if (message == defaultMessage) {
						Annotation messageAnnotaion = t.getClass()
								.getAnnotation(defaultMessageAnnotation);
						if (messageAnnotaion != null) {
							message = (Enum<M>) messageAnnotaion.getClass()
									.getMethod("value")
									.invoke(messageAnnotaion);
						}
					}

					boolean invokedByException = invokeCleanups(
							ic.getMethod().getDeclaringClass(),
							targetObj,
							(String) exHandle.getClass()
									.getMethod("cleanupName").invoke(exHandle));

					if (!invokedByException) {
						invokeCleanups(
								ic.getMethod().getDeclaringClass(),
								targetObj,
								(String) handler.getClass()
										.getMethod("cleanupName")
										.invoke(handler));
					}

					handleException(t, message);
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Invokes the cleanup methods of the exception handler or exception
	 * handling.
	 * 
	 * @param clazz
	 *            the class of the bean to get the methods from
	 * @param target
	 *            the target on which the method is invoked
	 * @param cleanupName
	 *            the name of the cleanup method
	 * @return true if the cleanup method were found and invoked
	 * @throws Exception
	 *             if an reflection specific exception occurs
	 */
	private boolean invokeCleanups(Class<?> clazz, Object target,
			String cleanupName) throws Exception {
		boolean invoked = false;

		if (!cleanupName.isEmpty()) {
			for (Method m : clazz.getMethods()) {
				Cleanup cleanup = m.getAnnotation(Cleanup.class);

				if (cleanup != null && cleanup.value().getName().equals(cleanupName)) {
					m.invoke(target);
					invoked = true;
				}
			}
		}
		return invoked;
	}

	/**
	 * Handles the message in case of an thrown exception.
	 * 
	 * @param ex
	 *            the thrown Throwable
	 * @param message
	 *            the message corresponding to the Throwable
	 */
	protected abstract void handleException(Throwable ex, Enum<M> message);
}
