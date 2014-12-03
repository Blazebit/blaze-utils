/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.exception;

import com.blazebit.annotation.AnnotationUtils;
import com.blazebit.cdi.cleanup.annotation.Cleanup;
import com.blazebit.cdi.exception.annotation.CatchHandler;
import com.blazebit.cdi.exception.annotation.CatchHandling;
import com.blazebit.exception.ExceptionUtils;
import com.blazebit.reflection.ReflectionUtils;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.apache.deltaspike.core.api.exception.control.event.ExceptionToCatchEvent;

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
		CatchHandler exceptionHandlerAnnotation = AnnotationUtils
				.findAnnotation(m, targetClass, CatchHandler.class);
		Exception unexpectedException = null;

		if (exceptionHandlerAnnotation == null) {
			throw new IllegalStateException(
					"The interceptor annotation can not be determined!");
		}

		CatchHandling[] exceptionHandlingAnnotations = exceptionHandlerAnnotation
				.value();
		Class<? extends Throwable>[] unwrap = exceptionHandlerAnnotation
				.unwrap();

		try {
			return ic.proceed();
		} catch (Exception ex) {
			if (!contains(unwrap, InvocationTargetException.class)) {
				unwrap = Arrays.copyOf(unwrap, unwrap.length + 1);
				unwrap[unwrap.length - 1] = InvocationTargetException.class;
			}

			// Unwrap Exception if ex is instanceof InvocationTargetException
			Throwable t = ExceptionUtils.unwrap(ex,
					InvocationTargetException.class);
			boolean exceptionHandled = false;
                        boolean cleanupInvoked = false;

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
						if (!handling.cleanup().equals(Object.class)) {
                                                        cleanupInvoked = invokeCleanups(targetClass, targetObject,
                                                                    handling.cleanup(), t);
						}

						break;
					}
				}
			}

			// Handle the default exception type if no handlings are
			// declared or the handling did not handle the exception
			if (!exceptionHandled) {
				if (exceptionHandlerAnnotation.exception().isInstance(t)) {
					try {
						handleThrowable(t);
						exceptionHandled = true;
					} catch (Exception unexpected) {
						unexpectedException = unexpected;
					}

					if (!exceptionHandlerAnnotation.cleanup().equals(
							Object.class) && !cleanupInvoked) {
						if(!cleanupInvoked) {
                                                    invokeCleanups(targetClass, targetObject,
								exceptionHandlerAnnotation.cleanup(), t);
                                                }
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

	private static boolean contains(Class<? extends Throwable>[] classes,
			Class<? extends Throwable> clazz) {
		for (int i = 0; i < classes.length; i++) {
			if (classes[i].getName().equals(clazz.getName())) {
				return true;
			}
		}

		return false;
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
			Class<?> cleanupClazz, Throwable exception) throws Exception {
		boolean invoked = false;

		if (!cleanupClazz.equals(Object.class)) {
                        // Christian Beikov 29.07.2013: Traverse whole hierarchy
                        // instead of retrieving the annotation directly from
                        // the class object.
                        List<Method> methods = ReflectionUtils.getMethods(target.getClass(), Cleanup.class);
                        Method m = null;
                        
                        for (Method candidate : methods) {
                            Cleanup c = AnnotationUtils.findAnnotation(candidate, Cleanup.class);
                            
                            if (cleanupClazz.equals(c.value())) {
                                m = candidate;
                                break;
                            }
                        }
                        
                        if(m != null) {
                            final Class<?>[] parameterTypes = m.getParameterTypes();
                            if (parameterTypes.length == 1) {
                                if (ReflectionUtils.isSubtype(exception.getClass(), parameterTypes[0])) {
                                    m.invoke(target, exception);
                                    invoked = true;
                                } else {
                                    throw new IllegalArgumentException("Cleanup method with name " + cleanupClazz.getName() + " requires a parameter that is not a subtype of the exception class " + exception.getClass().getName());
                                }
                            } else {
                                m.invoke(target);
                                invoked = true;
                            }
                        }
		}
                
		return invoked;
	}
}
