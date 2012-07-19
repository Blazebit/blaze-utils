/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.blazebit.reflection.ReflectionUtil;

/**
 * Utillity class for annotation handling. Basically this class only uses
 * java.lang classes for the methods. The only exception is, that #
 * {@link Stereotype} is used for annotation 'inheritance'.
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
@SuppressWarnings("unchecked")
public class AnnotationUtil {

	private static Class<? extends Annotation> stereotypeAnnotationClass;

	static {
		try {
			stereotypeAnnotationClass = (Class<? extends Annotation>) Class
					.forName("javax.enterprise.inject.Stereotype");
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AnnotationUtil.class.getName())
					.log(Level.WARNING,
							"Stereotype annotation can not be found, skipping annotation inheritance via stereotype.",
							ex);
		}
	}

	/**
	 * Returns all annotations of a class, also the annotations of the super
	 * classes, implemented interfaces and the annotations that are present in
	 * stereotype annotations. The stereotype annotation will not be included in
	 * the annotation set.
	 * 
	 * Note that the set can contain annotation objects of the same type with
	 * different values for their members.
	 * 
	 * @param clazz
	 *            the class from which to get the annotations
	 * @return all annotations that are present for the given class
	 */
	public static Set<Annotation> getAllAnnotations(Class<?> clazz) {
		Set<Annotation> annotationSet = new HashSet<Annotation>();
		Stack<Class<?>> annotationTypes = new Stack<Class<?>>();

		// Iterate through all super types of the given class
		for (Class<?> type : ReflectionUtil.getSuperTypes(clazz)) {
			Annotation[] annotations = type.getAnnotations();

			// Iterate through all annotations of the current class
			for (Annotation a : annotations) {
				// Add the current annotation to the result and to the
				// annotation types that needed to be examained for stereotype
				// annotations
				annotationSet.add(a);
				annotationTypes.push(a.annotationType());
			}
		}

		while (!annotationTypes.isEmpty()) {
			Class<?> annotationType = annotationTypes.pop();

			if (stereotypeAnnotationClass != null
					&& annotationType
							.isAnnotationPresent(stereotypeAnnotationClass)) {
				// If the stereotype annotation is present examaine the
				// 'inherited' annotations
				for (Annotation annotation : annotationType.getAnnotations()) {
					// add the 'inherited' annotations to be examined for
					// further stereotype annotations
					annotationTypes.push(annotation.annotationType());

					if (!annotation.annotationType().equals(
							stereotypeAnnotationClass)) {
						// add the stereotyped annotations to the set
						annotationSet.add(annotation);
					}
				}
			}
		}

		return annotationSet;
	}

	/**
	 * Returns all annotations of a class, also the annotations of the super
	 * classes, implemented interfaces and the annotations that are present in
	 * stereotype annotations. The stereotype annotation will not be included in
	 * the annotation set.
	 * 
	 * @param m
	 *            the class from which to get the annotations
	 * @return all annotations that are present for the given class
	 */
	public static Set<Annotation> getAllAnnotations(Method m) {
		Set<Annotation> annotationSet = new HashSet<Annotation>();
		Annotation[] annotations = m.getAnnotations();
		Stack<Class<?>> annotationTypes = new Stack<Class<?>>();

		// Iterate through all annotations of the current class
		for (Annotation a : annotations) {
			// Add the current annotation to the result and to the annotation
			// types that needed to be examained for stereotype annotations
			annotationSet.add(a);
			annotationTypes.push(a.annotationType());
		}

		while (!annotationTypes.isEmpty()) {
			Class<?> annotationType = annotationTypes.pop();

			if (stereotypeAnnotationClass != null
					&& annotationType
							.isAnnotationPresent(stereotypeAnnotationClass)) {
				// If the stereotype annotation is present examaine the
				// 'inherited' annotations
				for (Annotation annotation : annotationType.getAnnotations()) {
					// add the 'inherited' annotations to be examined for
					// further stereotype annotations
					annotationTypes.push(annotation.annotationType());

					if (!annotation.annotationType().equals(
							stereotypeAnnotationClass)) {
						// add the stereotyped annotations to the set
						annotationSet.add(annotation);
					}
				}
			}
		}

		return annotationSet;
	}

	/**
	 * Returns the annotation object for the specified annotation class of
	 * either the method if it can be found, or of the given class object. First
	 * the annotation is searched via the method
	 * {@link #findAnnotation(java.lang.reflect.Method, java.lang.Class) }. If
	 * the annotation can not be found, the method
	 * {@link #findAnnotation(java.lang.Class, java.lang.Class) } is used to find
	 * the annotation on class level.
	 * 
	 * @param <T>
	 *            The annotation type
	 * @param m
	 *            The method in which to look for the annotation
	 * @param clazz
	 *            The class in which to look for the annotation if it can not be
	 *            found on method level
	 * @param annotationClazz
	 *            The type of the annotation to look for
	 * @return The annotation with the given type if found, otherwise null
	 */
	public static <T extends Annotation> T findAnnotation(Method m,
			Class<?> clazz, Class<T> annotationClazz) {
		final T result = findAnnotation(m, annotationClazz);
		return result != null ? result : findAnnotation(clazz, annotationClazz);
	}

	/**
	 * Returns the annotation object for the specified annotation class of the
	 * method if it can be found, otherwise null. The annotation is searched in
	 * the method which and if a stereotype annotation is found, the annotations
	 * present on an annotation are also examined. If the annotation can not be
	 * found, null is returned.
	 * 
	 * @param <T>
	 *            The annotation type
	 * @param m
	 *            The method in which to look for the annotation
	 * @param annotationClazz
	 *            The type of the annotation to look for
	 * @return The annotation with the given type if found, otherwise null
	 */
	public static <T extends Annotation> T findAnnotation(Method m,
			Class<T> annotationClazz) {
		Stack<Class<?>> annotationTypes = new Stack<Class<?>>();

		// Iterate through all annotations of the current class
		for (Annotation a : m.getAnnotations()) {

			if (a.annotationType().equals(annotationClazz)) {
				return annotationClazz.cast(a);
			}

			annotationTypes.push(a.annotationType());
		}

		while (!annotationTypes.isEmpty()) {
			Class<?> annotationType = annotationTypes.pop();

			if (stereotypeAnnotationClass != null
					&& annotationType
							.isAnnotationPresent(stereotypeAnnotationClass)) {
				// If the stereotype annotation is present examaine the
				// 'inherited' annotations
				for (Annotation annotation : annotationType.getAnnotations()) {
					if (!annotation.annotationType().equals(
							stereotypeAnnotationClass)) {
						if (annotation.annotationType().equals(annotationClazz)) {
							return annotationClazz.cast(annotation);
						}
					}

					// add the 'inherited' annotations to be examined for
					// further stereotype annotations
					annotationTypes.push(annotation.annotationType());
				}
			}
		}

		return null;
	}

	/**
	 * Returns the annotation object for the specified annotation class of the
	 * given class object. All super types of the given class are examined to
	 * find the annotation. If a stereotype annotation is found, the annotations
	 * present on an annotation are also examined.
	 * 
	 * @param <T>
	 *            The annotation type
	 * @param clazz
	 *            The class in which to look for the annotation
	 * @param annotationClazz
	 *            The type of the annotation to look for
	 * @return The annotation with the given type if found, otherwise null
	 */
	public static <T extends Annotation> T findAnnotation(Class<?> clazz,
			Class<T> annotationClazz) {
		Stack<Class<?>> annotationTypes = new Stack<Class<?>>();

		// Iterate through all super types of the given class
		for (Class<?> type : ReflectionUtil.getSuperTypes(clazz)) {
			Annotation[] annotations = type.getAnnotations();

			// Iterate through all annotations of the current class
			for (Annotation a : annotations) {

				if (a.annotationType().equals(annotationClazz)) {
					return annotationClazz.cast(a);
				}

				annotationTypes.push(a.annotationType());
			}
		}

		while (!annotationTypes.isEmpty()) {
			Class<?> annotationType = annotationTypes.pop();

			if (stereotypeAnnotationClass != null
					&& annotationType
							.isAnnotationPresent(stereotypeAnnotationClass)) {
				// If the stereotype annotation is present examaine the
				// 'inherited' annotations
				for (Annotation annotation : annotationType.getAnnotations()) {
					if (!annotation.annotationType().equals(
							stereotypeAnnotationClass)) {
						if (annotation.annotationType().equals(annotationClazz)) {
							return annotationClazz.cast(annotation);
						}
					}

					// add the 'inherited' annotations to be examined for
					// further stereotype annotations
					annotationTypes.push(annotation.annotationType());
				}
			}
		}

		return null;
	}
}
