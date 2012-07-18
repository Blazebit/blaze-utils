/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.exception;

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.apache.deltaspike.core.api.exception.control.event.ExceptionToCatchEvent;

import com.blazebit.annotation.AnnotationUtil;
import com.blazebit.annotation.constraint.NullClass;
import com.blazebit.cdi.cleanup.annotation.Cleanup;
import com.blazebit.cdi.exception.annotation.CatchHandler;
import com.blazebit.cdi.exception.annotation.CatchHandling;
import com.blazebit.exception.ExceptionUtil;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 * @see CatchHandler
 * @see CatchHandling
 * 
 */
@Interceptor
@CatchHandler
public class CatchHandlerInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Event<ExceptionToCatchEvent> catchEvent;

	/**
	 * Handles the exception.
	 * 
	 * @param ic
	 *            The InvocationContext.
	 * @return The result of the intercepted method.
	 * @throws Exception
	 *             if an error occurs the handling
	 */
	@AroundInvoke
	public Object handle(InvocationContext ic) throws Exception {
		Method m = ic.getMethod();
		Object targetObject = ic.getTarget();
		Class<?> targetClass = targetObject == null ? m.getDeclaringClass()
				: targetObject.getClass();
		CatchHandler exceptionHandlerAnnotation = AnnotationUtil
				.findAnnotation(m, targetClass, CatchHandler.class);
		Exception unexpectedException = null;

		if (exceptionHandlerAnnotation == null) {
			throw new IllegalStateException(
					"The interceptor annotation can not be determined!");
		}

		CatchHandling[] exceptionHandlingAnnotations = exceptionHandlerAnnotation
				.value();

		try {
			return ic.proceed();
		} catch (Exception ex) {
			// Unwrap Exception if ex is instanceof InvocationTargetException
			Throwable t = ExceptionUtil.unwrapInvocationTargetException(ex);
			boolean exceptionHandled = false;

			if (exceptionHandlingAnnotations.length > 0) {
				for (CatchHandling handling : exceptionHandlingAnnotations) {
					if (handling.exception().isInstance(t)) {
						try {
							handleThrowable(t);
							exceptionHandled = true;
						} catch (Exception unexpected) {
							unexpectedException = unexpected;
						}

						// Only invoke cleanup declared at handling level
						if (!handling.cleanup().equals(NullClass.class)) {
							invokeCleanups(targetClass, targetObject,
									handling.cleanup());
						}

						break;
					}
				}
			} else {
				// Handle the default exception type if no handlings are
				// declared
				if (exceptionHandlerAnnotation.exception().isInstance(t)) {
					try {
						handleThrowable(t);
						exceptionHandled = true;
					} catch (Exception unexpected) {
						unexpectedException = unexpected;
					}

					if (!exceptionHandlerAnnotation.cleanup().equals(
							NullClass.class)) {
						invokeCleanups(targetClass, targetObject,
								exceptionHandlerAnnotation.cleanup());
					}
				}
			}

			if (!exceptionHandled) {
				if (t instanceof Exception) {
					unexpectedException = (Exception) t;
				} else {
					unexpectedException = new Exception(t);
				}
			}
		}

		if (unexpectedException != null) {
			throw unexpectedException;
		}

		return null;
	}

	/**
	 * This method should populate the given throwable to a handler that will do
	 * the appropriate exception handling. The default implementation will
	 * populate the throwable via CDI-Event.
	 * 
	 * @param t
	 *            The throwable to handle
	 */
	protected void handleThrowable(Throwable t) {
		catchEvent.fire(new ExceptionToCatchEvent(t));
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
			Class<?> cleanupClazz) throws Exception {
		boolean invoked = false;

		if (!cleanupClazz.equals(NullClass.class)) {
			for (Method m : clazz.getMethods()) {
				Cleanup cleanup = m.getAnnotation(Cleanup.class);

				if (cleanup != null && cleanup.value().equals(cleanupClazz)) {
					m.invoke(target);
					invoked = true;
				}
			}
		}
		return invoked;
	}
}
