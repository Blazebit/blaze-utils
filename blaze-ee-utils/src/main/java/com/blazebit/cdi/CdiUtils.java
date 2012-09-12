/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.cdi;

import java.lang.annotation.Annotation;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Utillity class for Cdi related actions.
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class CdiUtils {

	private static final Logger log = Logger.getLogger(CdiUtils.class.getName());

	/**
	 * Retrieves the #{@link BeanManager} instance via JNDI.
	 * 
	 * @return The found bean manager instance if found, otherwise null
	 */
	public static BeanManager getBeanManager() {
		try {
			InitialContext initialContext = new InitialContext();
			return (BeanManager) initialContext.lookup("java:comp/BeanManager");
		} catch (NamingException e) {
			log.log(Level.SEVERE, "Couldn't get BeanManager through JNDI", e);
			return null;
		}
	}

	/**
	 * Retrieves the instance for a named bean by the given name
	 * 
	 * @param name
	 *            The name of the bean to look for
	 * @return The found bean object if found, otherwise null
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		BeanManager bm = getBeanManager();
		Bean<?> bean = bm.getBeans(name).iterator().next();
		CreationalContext<?> ctx = bm.createCreationalContext(bean);
		return (T) bm.getReference(bean, bean.getBeanClass(), ctx);
	}

	/**
	 * Retrieves the bean for the given class from the bean manager available
	 * via JNDI qualified with #{@link Any}.
	 * 
	 * @param <T>
	 *            The type of the bean to look for
	 * @param clazz
	 *            The class of the bean to look for
	 * @return The bean instance if found, otherwise null
	 */
	public static <T> T getBean(Class<T> clazz) {
		return getBean(getBeanManager(), clazz);
	}

	/**
	 * Retrieves the bean for the given class from the given bean manager
	 * qualified with #{@link Default}.
	 * 
	 * @param <T>
	 *            The type of the bean to look for
	 * @param bm
	 *            The bean manager which should be used for the lookup
	 * @param clazz
	 *            The class of the bean to look for
	 * @return The bean instance if found, otherwise null
	 */
	public static <T> T getBean(BeanManager bm, Class<T> clazz) {
		return getBean(bm, clazz, (Annotation[]) null);
	}

	/**
	 * Retrieves the bean for the given class from the bean manager available
	 * via JNDI qualified with the given annotation(s).
	 * 
	 * @param <T>
	 *            The type of the bean to look for
	 * @param clazz
	 *            The class of the bean to look for
	 * @param annotationClasses
	 *            The qualifiers the bean for the given class must have
	 * @return The bean instance if found, otherwise null
	 */
	public static <T> T getBean(Class<T> clazz, Annotation... annotations) {
		return getBean(getBeanManager(), clazz, annotations);
	}

	/**
	 * Retrieves the bean for the given class from the given bean manager
	 * qualified with the given annotation(s).
	 * 
	 * @param <T>
	 *            The type of the bean to look for
	 * @param bm
	 *            The bean manager which should be used for the lookup
	 * @param clazz
	 *            The class of the bean to look for
	 * @param annotationClasses
	 *            The qualifiers the bean for the given class must have
	 * @return The bean instance if found, otherwise null
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(BeanManager bm, Class<T> clazz,
			Annotation... annotations) {

		Bean<?> bean;

		if (annotations != null) {
			bean = bm.getBeans(clazz, annotations).iterator().next();
		} else {
			bean = bm.getBeans(clazz, new DefaultLiteral()).iterator().next();
		}
		CreationalContext<?> ctx = bm.createCreationalContext(bean);
		return (T) bm.getReference(bean, bean.getBeanClass(), ctx);
	}
}
