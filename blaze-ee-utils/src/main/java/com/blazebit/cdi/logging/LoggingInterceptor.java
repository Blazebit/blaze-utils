/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.logging;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.blazebit.cdi.logging.annotation.Logging;
import com.blazebit.exception.ExceptionUtils;

/**
 * This interceptor logs invocations of methods, class name, method name,
 * parameter values, return values, thrown exceptions, through the standard java
 * logging API. It skips the logging when debug is not enabled for the
 * intercepted class.
 * 
 * <code>
 * public class Bean implements Serializable {
 * 
 * @Logging public String example(String value) throws Exception{ if(value !=
 *          null && value.isEmpty()){ throw new Exception("Empty String given");
 *          }
 * 
 *          return value; } } </code>
 * 
 *          Invocation: bean.example("test"); Log result: Bean.example(test)
 *          called Bean.example() returned test
 * 
 *          Invocation: bean.example(null); Log result: Bean.example(null)
 *          called Bean.example() returned null
 * 
 *          Invocation: bean.example(""); Log result: Bean.example("") called
 *          Bean.example() returned null Bean.example() throwed Exception with
 *          the message: Empty String given
 * 
 * @author Christian Beikov
 * @since 0.1.2
 * @see Logging
 */
@Interceptor
@Logging
public class LoggingInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;

	@AroundInvoke
	public Object errorLogging(InvocationContext ic) throws Exception {
		// Retrieve the logger for the intercepted class
		Logger log = Logger.getLogger(ic.getTarget().getClass().getName());

		// Skip logging and directly proceed when debug is not enabled for
		// the intercepted class
		if (!log.isLoggable(Level.FINE)) {
			return ic.proceed();
		}

		Object ret = null;
		String className = ic.getMethod().getDeclaringClass().getSimpleName();
		String methodName = ic.getMethod().getName();
		Class<?> methodReturnType = ic.getMethod().getReturnType();
		StringBuilder sb = new StringBuilder();

		Throwable t = null;
		Object[] o = ic.getParameters();
		sb.append(className).append(methodName).append("(");

		// This is a simple string join operation, like in apache StringUtils
		// with the exception, that null is printed as the string "null"
		for (int i = 0; i < o.length; i++) {
			if (i != 0) {
				sb.append(", ");
			}

			if (o[i] == null) {
				sb.append("null");
			} else {
				sb.append(o[i].toString());
			}
		}

		log.log(Level.FINE, sb.append(")").append(" called").toString());

		try {
			ret = ic.proceed();
		} catch (Throwable t1) {
			t = t1;

			// Check if the throwable is an instance of
			// InvocationTargetException
			// and if so, unwrap the cause. OWB did not unwrap exceptions that
			// have been thrown in decorators in some versions so we need to do
			// this to be able to log the right exception
			t = ExceptionUtils.unwrap(t, InvocationTargetException.class);
		}

		// Reuse the StringBuilder
		sb.setLength(0);
		sb.append(className).append(methodName).append("()");

		if (t == null) {
			if (void.class.equals(methodReturnType)) {
				sb.append(" exited");
			} else {
				sb.append(" returned ");

				// Append the return value to the log entry
				if (ret == null) {
					sb.append("null");
				} else {
					sb.append(ret.toString());
				}
			}
		} else {
			sb.append(" throwed ");
			sb.append(t.getClass().getSimpleName()).append(
					"() with the message: ");

			// Append the exception message if there is one
			if (t.getMessage() != null && !t.getMessage().isEmpty()) {
				sb.append(t.getMessage());
			}
		}

		log.log(Level.FINE, sb.toString());

		if (t != null) {
			// Cast to or wrap the
			// throwable into a new exception and rethrow it
			if (t instanceof Exception) {
				throw (Exception) t;
			}

			throw new Exception(t);
		}

		return ret;
	}
}
