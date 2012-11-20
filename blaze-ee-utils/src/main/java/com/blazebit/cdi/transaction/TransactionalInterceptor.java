/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi.transaction;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Resource;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import com.blazebit.annotation.AnnotationUtils;
import com.blazebit.cdi.transaction.annotation.Transactional;
import com.blazebit.exception.ExceptionUtils;

/**
 * This interceptor executes a intercepted method within a JTA trasaction. When
 * requires new is set to true, a nested transaction should be started, but JTA
 * does not support nested transactions. Methods that are called from within the
 * method, which are also annotated with @Transactional will join the parent
 * transaction. The most outer interceptor that started the transaction is
 * responsible for commiting and rollback.
 * 
 * A commit is always done when the most outer intercepted method exits, but a
 * rollback only if it throws a Throwable.
 * 
 * Setting the requiresNew attribute to true will throw an
 * UnsupportedOperationException
 * 
 * <code>
 * public class Bean implements Serializable {
 * 
 * @Transactional public void example() { // code } } </code>
 * 
 *                The code of the example method will be invoked between,
 *                UserTransaction.begin() and UserTransaction.commit() or
 *                UserTransaction.rollback().
 * 
 * @author Christian Beikov
 * @since 0.1.2
 * @see Transactional
 */
@Transactional
@Interceptor
public class TransactionalInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;
	@Resource
	private UserTransaction utx;

	@SuppressWarnings("unused")
	@AroundInvoke
	public Object applyTransaction(InvocationContext ic) throws Exception {
		Method m = ic.getMethod();
		Object targetObject = ic.getTarget();
		Class<?> targetClass = targetObject == null ? m.getDeclaringClass()
				: targetObject.getClass();
		Transactional transactionalAnnotation = AnnotationUtils.findAnnotation(
				m, targetClass, Transactional.class);
		Object ret;

		if (transactionalAnnotation == null) {
			throw new IllegalStateException(
					"The interceptor annotation can not be determined!");
		}

		if (transactionalAnnotation != null) {
			if (!transactionalAnnotation.requiresNew()) {
				boolean startedTransaction = false;

				if (utx.getStatus() != Status.STATUS_ACTIVE) {
					utx.begin();
					startedTransaction = true;
				}

				try {
					ret = ic.proceed();

					if (startedTransaction) {
						utx.commit();

					}
				} catch (Throwable t) {
					if (startedTransaction) {
						utx.rollback();
					}

					// Check if the throwable is an instance of
					// InvocationTargetException
					// and if so, unwrap the cause. OWB did not unwrap
					// exceptions that
					// have been thrown in decorators in some versions so we
					// need to do
					// this to be able to log the right exception
					Throwable t1 = ExceptionUtils
							.unwrap(t, InvocationTargetException.class);

					// Cast to or wrap the throwable into a new exception and
					// rethrow it
					if (t1 instanceof Exception) {
						throw (Exception) t1;
					}

					throw new Exception(t1);
				}
			} else {
				throw new UnsupportedOperationException("Not supported yet.");
			}
		} else {
			ret = ic.proceed();
		}

		return ret;

	}
}