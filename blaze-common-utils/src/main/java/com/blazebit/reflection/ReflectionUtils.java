/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Utillity class for reflection specific actions. This class only uses basic
 * reflection mechanisms provided by the Reflection API. It provides methods
 * that are missing in the standard API and Apache Commons Utils.
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public final class ReflectionUtils {

	private static final Map<String, Class<?>> primitiveClasses = new HashMap<String, Class<?>>();
	private static final Map<Class<?>, Class<?>> primitiveToObjectClasses = new HashMap<Class<?>, Class<?>>();

	static {
		primitiveClasses.put("int", Integer.TYPE);
		primitiveClasses.put("long", Long.TYPE);
		primitiveClasses.put("double", Double.TYPE);
		primitiveClasses.put("float", Float.TYPE);
		primitiveClasses.put("boolean", Boolean.TYPE);
		primitiveClasses.put("char", Character.TYPE);
		primitiveClasses.put("byte", Byte.TYPE);
		primitiveClasses.put("void", Void.TYPE);
		primitiveClasses.put("short", Short.TYPE);

		primitiveToObjectClasses.put(int.class, Integer.class);
		primitiveToObjectClasses.put(long.class, Long.class);
		primitiveToObjectClasses.put(double.class, Double.class);
		primitiveToObjectClasses.put(float.class, Float.class);
		primitiveToObjectClasses.put(boolean.class, Boolean.class);
		primitiveToObjectClasses.put(char.class, Character.class);
		primitiveToObjectClasses.put(byte.class, Byte.class);
		primitiveToObjectClasses.put(void.class, Void.class);
		primitiveToObjectClasses.put(short.class, Short.class);
	}
	
	private ReflectionUtils(){}

	/**
	 * Returns the class object for the specified qualified class name. Calling
	 * this method is equal to #{@link Class#forName(java.lang.String)} except
	 * that also primitive types can be get via this method. The names for the
	 * primitive types needed for this method are equal to the type literals
	 * used in the java language.
	 * 
	 * Example:
	 * 
	 * <code>
	 * ReflectionUtil.getClass("void").equals(void.class)
	 * ReflectionUtil.getClass("int").equals(int.class)
	 * </code>
	 * 
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> getClass(String className)
			throws ClassNotFoundException {
		Class<?> clazz = primitiveClasses.get(className);

		if (clazz == null) {
			clazz = Class.forName(className);
		}

		return clazz;
	}

	public static Class<?> getObjectClassOfPrimitve(Class<?> primitive) {
		if (primitiveToObjectClasses.containsKey(primitive)) {
			return primitiveToObjectClasses.get(primitive);
		}

		return primitive;

	}

	/**
	 * Checks if the target class is a subtype of the supertype or not.
	 * Basically this method gathers all interfaces and super classes of the
	 * superType and checks if the targetClazz, interfaces that are implemented
	 * by targetClazz or super classes of the targetClazz are within the list of
	 * the superType classes.
	 * 
	 * Example:
	 * 
	 * <code>
	 * public interface A{}
	 * public interface B extends A{}
	 * public class ClassA implements A{}
	 * public class ClassB extends ClassA implements B{}
	 * 
	 * ReflectionUtil.isSubtype(ClassB.class, ClassB.class) == true
	 * ReflectionUtil.isSubtype(ClassB.class, ClassA.class) == true
	 * ReflectionUtil.isSubtype(ClassB.class, B.class) == true
	 * ReflectionUtil.isSubtype(ClassB.class, A.class) == true
	 * </code>
	 * 
	 * @param targetClazz
	 *            the class to check wether it is a subtype of the supertype or
	 *            not
	 * @param superType
	 *            the supertype class to search on targetClazz
	 * @return true if targetClazz is subtype of superType or targetClazz equals
	 *         superType, otherwise false
	 */
	public static boolean isSubtype(Class<?> targetClazz, Class<?> superType) {
		Class<?> traverseClass = targetClazz;

		do {
			if (traverseClass.equals(superType)) {
				return true;
			}

			for (Class<?> c : traverseClass.getInterfaces()) {
				if (superType.equals(c)) {
					return true;
				}
			}

			traverseClass = traverseClass.getSuperclass();
		} while (traverseClass != null);

		return false;
	}

	/**
	 * Retrieves all super types of the given class type. Super types are all
	 * types the given class extends or implements. The given class type is also
	 * included in the set. The iteration order of the set has to be from most
	 * concrete to most general.
	 * 
	 * @param clazz
	 *            The class from which the super types should be retrieved
	 * @return The super types of the given class
	 */
	public static Set<Class<?>> getSuperTypes(Class<?> clazz) {
		Set<Class<?>> list = new LinkedHashSet<Class<?>>();
		Class<?> traverseClass = clazz;

		do {
			list.add(traverseClass);
			Collections.addAll(list, traverseClass.getInterfaces());
			traverseClass = traverseClass.getSuperclass();
		} while (traverseClass != null);

		return list;
	}

	/**
	 * Returns the type of a field if it exists within the class. Calling this
	 * method is equal to calling #
	 * {@link ReflectionUtils#getField(java.lang.Class, java.lang.String)
     * } with a
	 * null check and finally return the type via getType().
	 * 
	 * @param clazz
	 *            The class within to look for the field with the given field
	 *            name
	 * @param fieldName
	 *            The name of the field to be returned
	 * @return The type of the field if it can be found, otherwise null
	 * @see ReflectionUtils#getField(java.lang.Class, java.lang.String)
	 */
	public static Class<?> getFieldType(Class<?> clazz, String fieldName) {
		Field f = getField(clazz, fieldName);

		if (f == null) {
			return null;
		}

		return f.getType();
	}

	public static Class<?> getResolvedFieldType(Class<?> clazz, String fieldName) {
		return getResolvedFieldType(clazz, getField(clazz, fieldName));
	}

	public static Class<?> getResolvedFieldType(Class<?> clazz, Field f) {
		if (f == null) {
			return null;
		}

		if (f.getGenericType() instanceof TypeVariable<?>) {
			return resolveTypeVariable(clazz,
					(TypeVariable<?>) f.getGenericType());
		}

		return f.getType();
	}

	public static Class<?>[] getResolvedFieldTypeArguments(Class<?> clazz,
			String fieldName) {
		return getResolvedFieldTypeArguments(clazz, getField(clazz, fieldName));
	}

	public static Class<?>[] getResolvedFieldTypeArguments(Class<?> clazz,
			Field f) {
		if (f == null) {
			return null;
		}

		return resolveTypeArguments(clazz, f.getGenericType());
	}

	public static Class<?>[] resolveTypeArguments(Class<?> concreteClass,
			Type type) {
		if (type instanceof ParameterizedType) {
			return resolveTypeArguments(concreteClass, (ParameterizedType) type);
		}

		return new Class<?>[0];
	}

	public static Class<?>[] resolveTypeArguments(Class<?> concreteClass,
			ParameterizedType parameterizedType) {
		if (parameterizedType == null) {
			return null;
		}

		Type[] argumentTypes = parameterizedType.getActualTypeArguments();
		Class<?>[] resolvedClasses = new Class<?>[argumentTypes.length];

		for (int i = 0; i < argumentTypes.length; i++) {
			if (argumentTypes[i] instanceof TypeVariable<?>) {
				resolvedClasses[i] = resolveTypeVariable(concreteClass,
						(TypeVariable<?>) argumentTypes[i]);
			} else {
				// We assume here that only class types and type variables are
				// possible argument types for the parameterizedType
				resolvedClasses[i] = (Class<?>) argumentTypes[i];
			}
		}

		return resolvedClasses;
	}

	/**
	 * Tries to resolve the type variable againts the concrete class. The
	 * concrete class has to be a subtype of the type in which the type variable
	 * has been declared. This method tries to resolve the given type variable
	 * by inspecting the subclasses of the class in which the type variable was
	 * declared and as soon as the resolved type is instance of java.lang.Class
	 * it stops and returns that class.
	 * 
	 * @param concreteClass
	 *            The class which is used to resolve the type. The type for the
	 *            type variable must be bound in this class or a superclass.
	 * @param typeVariable
	 *            The type variable to resolve.
	 * @return The resolved type as class or null if the type can not be resolved.
	 * @throws IllegalArgumentException
	 *             Is thrown when the concrete class is not a subtype of the
	 *             class in which the type variable has been declared.
	 */
	public static Class<?> resolveTypeVariable(Class<?> concreteClass,
			TypeVariable<?> typeVariable) {
		Class<?> classThatContainsTypeVariable = null;

		if (isSubtype(typeVariable.getGenericDeclaration().getClass(),
				Class.class)) {
			classThatContainsTypeVariable = (Class<?>) typeVariable
					.getGenericDeclaration();
		} else if (isSubtype(typeVariable.getGenericDeclaration().getClass(),
				Method.class)) {
			classThatContainsTypeVariable = ((Method) typeVariable
					.getGenericDeclaration()).getDeclaringClass();
		} else if (isSubtype(typeVariable.getGenericDeclaration().getClass(),
				Constructor.class)) {
			classThatContainsTypeVariable = ((Constructor<?>) typeVariable
					.getGenericDeclaration()).getDeclaringClass();
		}

		if (!isSubtype(concreteClass, classThatContainsTypeVariable)) {
			throw new IllegalArgumentException(
					"The given concrete class is not a subtype of the class that contain the type variable!");
		}

		int position = getTypeVariablePosition(typeVariable);

		if (position == -1) {
			// Should never happen
			throw new IllegalArgumentException(
					"Type variable not found in its container class!");
		}

		Stack<Class<?>> classStack = new Stack<Class<?>>();
		Class<?> currentClass = concreteClass;
		Type resolvedType = typeVariable;

		classStack.push(currentClass);

		// Build a stack of the class hierarchy to be able to resolve the type
		while (!Object.class.equals(currentClass.getSuperclass()) &&
				!currentClass.getSuperclass().equals(typeVariable.getGenericDeclaration())) {
			currentClass = currentClass.getSuperclass();
			classStack.push(currentClass);
		}

		// Resolve every type variable in every class level
		// We need to do this here because the type variables of subclasses
		// of the container class of the type variable could move their
		// type variables to different positions
		while (!classStack.isEmpty() && !(resolvedType instanceof Class<?>)) {
			// Start at most general type and go down the hierarchy until
			// we reach concreteClass. Resolve the current type variable
			// to every level of the hierarchy and stop as soon as the
			// type is instance of java.lang.Class
			Class<?> classToInspect = classStack.pop();
			Type classToInspectType = classToInspect.getGenericSuperclass();

			// Since the resolvedType is not yet instance of class, the
			// classToInspectType has to be a ParameterizedType
			// otherwise we can not resolve the type variable
			if (!(classToInspectType instanceof ParameterizedType)) {
				return null;
			}

			ParameterizedType parameterizedClassToInspect = (ParameterizedType) classToInspectType;

			// This should be fulfilled, anyway we check it
			if (parameterizedClassToInspect.getActualTypeArguments().length < position + 1) {
				throw new IllegalArgumentException("Could not resolve type");
			}

			// Set the type of the type arguments at the needed position
			// as the resolvedType
			resolvedType = parameterizedClassToInspect.getActualTypeArguments()[position];

			if (resolvedType instanceof TypeVariable<?>) {
				// If the currently available resolvedType is still a type
				// variable
				// retrieve the position of the type variable within the type
				// variables of the current class, so we can look in the next
				// subclass for the concrete type
				position = getTypeVariablePosition(classToInspect,
						(TypeVariable<?>) resolvedType);
			} else if (resolvedType instanceof ParameterizedType) {
				// Since we can only want a class object, we don't
				// care about type arguments of the parameterized type
				// and just set the raw type of it as the resolved
				// type
				resolvedType = ((ParameterizedType) resolvedType).getRawType();
			}
		}

		if (!(resolvedType instanceof Class<?>)) {
			return null;
		}

		return (Class<?>) resolvedType;
	}

	/**
	 * Returns the position of the type variable for the class in which it is
	 * declared.
	 * 
	 * @param typeVariable
	 *            The type variable for which the position should be retrieved
	 * @return The position of the type variable within the class in which it is
	 *         declared.
	 */
	public static int getTypeVariablePosition(TypeVariable<?> typeVariable) {
		return getTypeVariablePosition(typeVariable.getGenericDeclaration(),
				typeVariable);
	}

	/**
	 * Tries to find the position of the given type variable in the type
	 * parameters of the given class. This method iterates through the type
	 * parameters of the given class and tries to find the given type variable
	 * within the type parameters. When the type variable is found, the position
	 * is returned, otherwise -1.
	 * 
	 * @param genericDeclartion
	 *            The generic declartion type in which to look for the type
	 *            variable
	 * @param typeVariable
	 *            The type variable to look for in the given class type
	 *            parameters
	 * @return The position of the given type variable within the type
	 *         parameters of the given class if found, otherwise -1
	 */
	public static int getTypeVariablePosition(
			GenericDeclaration genericDeclartion, TypeVariable<?> typeVariable) {
		int position = -1;
		TypeVariable<?>[] typeVariableDeclarationParameters = genericDeclartion
				.getTypeParameters();

		// Try to find the position of the type variable in the class
		for (int i = 0; i < typeVariableDeclarationParameters.length; i++) {
			if (typeVariableDeclarationParameters[i].equals(typeVariable)) {
				position = i;
				break;
			}
		}

		return position;
	}

	/**
	 * Returns the field object found for the given field name in the given
	 * class. This method traverses through the super classes of the given class
	 * and tries to find the field as declared field within these classes. When
	 * the object class is reached the traversing stops. If the field can not be
	 * found, null is returned.
	 * 
	 * @param clazz
	 *            The class within to look for the field with the given field
	 *            name
	 * @param fieldName
	 *            The name of the field to be returned
	 * @return The field object with the given field name if the field can be
	 *         found, otherwise null
	 */
	public static Field getField(Class<?> clazz, String fieldName) {
		Class<?> traverseClass = clazz;

		while (traverseClass != null) {
			try {
				return traverseClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException ex1) {
				// Look for the field in all interfaces of the traverse class
				for (Class<?> interfaceClass : traverseClass.getInterfaces()) {
					try {
						return interfaceClass.getDeclaredField(fieldName);
					} catch (NoSuchFieldException ex2) {
					}
				}

				traverseClass = traverseClass.getSuperclass();
			}
		}

		return null;
	}

	/**
	 * Returns the return type of a method if it exists within the class.
	 * Calling this method is equal to calling #
	 * {@link ReflectionUtils#getMethod(java.lang.Class, java.lang.String)
     * } with
	 * a null check and finally return the type via getReturnType().
	 * 
	 * @param clazz
	 *            The class within to look for the method with the given method
	 *            name
	 * @param methodName
	 *            The name of the method to be returned
	 * @param parameterTypes
	 *            The accepting parameter types of the method
	 * @return The return type of the method if it can be found, otherwise null
	 * @see ReflectionUtils#getMethod(java.lang.Class, java.lang.String)
	 */
	public static Class<?> getMethodReturnType(Class<?> clazz,
			String methodName, Class<?>... parameterTypes) {
		Method m = getMethod(clazz, methodName, parameterTypes);

		if (m == null) {
			return null;
		}

		return m.getReturnType();
	}

	public static Class<?>[] getMethodParameterTypes(Class<?> clazz,
			String methodName, Class<?>... parameterTypes) {
		Method m = getMethod(clazz, methodName, parameterTypes);

		if (m == null) {
			return null;
		}

		return m.getParameterTypes();
	}

	public static Class<?>[] getMethodExceptionTypes(Class<?> clazz,
			String methodName, Class<?>... parameterTypes) {
		Method m = getMethod(clazz, methodName, parameterTypes);

		if (m == null) {
			return null;
		}

		return m.getExceptionTypes();
	}

	public static Class<?> getResolvedMethodReturnType(Class<?> clazz,
			String methodName, Class<?>... parameterTypes) {
		return getResolvedMethodReturnType(clazz,
				getMethod(clazz, methodName, parameterTypes));
	}

	public static Class<?> getResolvedMethodReturnType(Class<?> clazz, Method m) {
		if (m == null) {
			return null;
		}

		if (m.getGenericReturnType() instanceof TypeVariable<?>) {
			return resolveTypeVariable(clazz,
					(TypeVariable<?>) m.getGenericReturnType());
		}

		return m.getReturnType();
	}

	public static Class<?>[] getResolvedMethodReturnTypeArguments(
			Class<?> clazz, String methodName, Class<?>... parameterTypes) {
		return getResolvedMethodReturnTypeArguments(clazz,
				getMethod(clazz, methodName, parameterTypes));
	}

	public static Class<?>[] getResolvedMethodReturnTypeArguments(
			Class<?> clazz, Method m) {
		if (m == null) {
			return null;
		}

		return resolveTypeArguments(clazz, m.getGenericReturnType());
	}

	public static Class<?>[] getResolvedMethodParameterTypes(Class<?> clazz,
			String methodName, Class<?>... parameterTypes) {
		return getResolvedMethodParameterTypes(clazz,
				getMethod(clazz, methodName, parameterTypes));
	}

	public static Class<?>[] getResolvedMethodParameterTypes(Class<?> clazz,
			Method m) {
		if (m == null) {
			return null;
		}

		Type[] genericParameterTypes = m.getGenericParameterTypes();
		Class<?>[] parameterTypes = new Class<?>[genericParameterTypes.length];

		for (int i = 0; i < genericParameterTypes.length; i++) {
			if (genericParameterTypes[i] instanceof TypeVariable<?>) {
				parameterTypes[i] = resolveTypeVariable(clazz,
						(TypeVariable<?>) genericParameterTypes[i]);
			} else {
				parameterTypes[i] = (Class<?>) genericParameterTypes[i];
			}
		}

		return parameterTypes;
	}

	public static Class<?>[] getResolvedMethodExceptionTypes(Class<?> clazz,
			String methodName, Class<?>... parameterTypes) {
		return getResolvedMethodExceptionTypes(clazz,
				getMethod(clazz, methodName, parameterTypes));
	}

	public static Class<?>[] getResolvedMethodExceptionTypes(Class<?> clazz,
			Method m) {
		if (m == null) {
			return null;
		}

		Type[] genericExceptionTypes = m.getGenericExceptionTypes();
		Class<?>[] exceptionTypes = new Class<?>[genericExceptionTypes.length];

		for (int i = 0; i < genericExceptionTypes.length; i++) {
			if (genericExceptionTypes[i] instanceof TypeVariable<?>) {
				exceptionTypes[i] = resolveTypeVariable(clazz,
						(TypeVariable<?>) genericExceptionTypes[i]);
			} else {
				exceptionTypes[i] = (Class<?>) genericExceptionTypes[i];
			}
		}

		return exceptionTypes;
	}

	public static MethodParameter[] getMethodParameters(Class<?> clazz,
			String methodName, Class<?>... parameterTypes) {
		return getMethodParameters(clazz,
				getMethod(clazz, methodName, parameterTypes));
	}

	public static MethodParameter[] getMethodParameters(Class<?> clazz, Method m) {
		if (m == null) {
			return null;
		}

		Type[] exceptionTypes = m.getGenericExceptionTypes();
		MethodParameter[] methodParameters = new MethodParameter[exceptionTypes.length];

		for (int i = 0; i < exceptionTypes.length; i++) {
			methodParameters[i] = new MethodParameter(m, i);
		}

		return methodParameters;
	}

	public static MethodException[] getMethodExceptions(Class<?> clazz,
			String methodName, Class<?>... parameterTypes) {
		return getMethodExceptions(clazz,
				getMethod(clazz, methodName, parameterTypes));
	}

	public static MethodException[] getMethodExceptions(Class<?> clazz, Method m) {
		if (m == null) {
			return null;
		}

		Type[] exceptionTypes = m.getGenericExceptionTypes();
		MethodException[] methodExceptions = new MethodException[exceptionTypes.length];

		for (int i = 0; i < exceptionTypes.length; i++) {
			methodExceptions[i] = new MethodException(m, i);
		}

		return methodExceptions;
	}

	/**
	 * Returns the method object found for the given method name in the given
	 * class. This method traverses through the super classes of the given class
	 * and tries to find the method as declared method within these classes.
	 * When the object class is reached the traversing stops. If the method can
	 * not be found, null is returned.
	 * 
	 * @param clazz
	 *            The class within to look for the method with the given method
	 *            name
	 * @param methodName
	 *            The name of the method to be returned
	 * @param parameterTypes
	 *            The accepting parameter types of the method
	 * @return The method object with the given method name if the method can be
	 *         found, otherwise null
	 */
	public static Method getMethod(Class<?> clazz, String methodName,
			Class<?>... parameterTypes) {
		Class<?> traverseClass = clazz;

		while (traverseClass != null) {
			try {
				return traverseClass.getDeclaredMethod(methodName,
						parameterTypes);
			} catch (NoSuchMethodException ex1) {

				// Look for the method in all interfaces of the traverse class
				for (Class<?> interfaceClass : traverseClass.getInterfaces()) {
					try {
						return interfaceClass.getDeclaredMethod(methodName,
								parameterTypes);
					} catch (NoSuchMethodException ex2) {
					}
				}

				traverseClass = traverseClass.getSuperclass();
			}
		}

		return null;
	}

	/**
	 * Retrieves the getter method of the given class for the specified field
	 * name. The method first tries to find the getFieldName method of the class
	 * and if it can not find that method it looks for the isFieldName method.
	 * If this method also can not be found, null is returned.
	 * 
	 * This method uses #
	 * {@link ReflectionUtils#getMethodReturnType(Class, String, Class...)} to
	 * retrieve the getter.
	 * 
	 * A getter must not have any parameters and must have a return type that is
	 * different from void.
	 * 
	 * @param clazz
	 *            The class within to look for the getter method
	 * @param fieldName
	 *            The field name for which to find the getter method
	 * @return The getter method for the given fieldName if it can be found,
	 *         otherwise null
	 */
	public static Method getGetter(Class<?> clazz, String fieldName) {
		StringBuilder sb = new StringBuilder("get").append(
				Character.toUpperCase(fieldName.charAt(0))).append(fieldName,
				1, fieldName.length());
		Method m = getMethod(clazz, sb.toString());

		if (m == null) {
			sb.replace(0, 3, "is");
			m = getMethod(clazz, sb.toString());
		}

		if (!isGetter(m)) {
			return null;
		}

		return m;
	}

	private static boolean isGetter(Method m) {
		return m != null && !void.class.equals(m.getReturnType())
				&& m.getParameterTypes().length == 0;
	}

	/**
	 * Retrieves the setter method of the given class for the specified field
	 * name. The method traverses through all methods of all types that are in
	 * the inheritance hierarchie of the given class and tries to find a setter
	 * method with the return type void and exactly one parameter. If that
	 * method can not be found, null is returned.
	 * 
	 * A setter must have void return type and accept exactly one parameter.
	 * 
	 * @param clazz
	 *            The class within to look for the setter method
	 * @param fieldName
	 *            The field name for which to find the setter method
	 * @return The setter method for the given fieldName if it can be found,
	 *         otherwise null
	 */
	public static Method getSetter(Class<?> clazz, String fieldName) {
		StringBuilder sb = new StringBuilder("set").append(
				Character.toUpperCase(fieldName.charAt(0))).append(fieldName,
				1, fieldName.length());
		String methodName = sb.toString();
		Class<?> traverseClass = clazz;
		Method method = null;

		while (traverseClass != null) {
			method = findSetter(methodName, traverseClass);

			if (method != null) {
				break;
			}

			// Look for the method in all interfaces of the traverse class
			method = findSetter(methodName, traverseClass.getInterfaces());

			if (method != null) {
				break;
			}

			traverseClass = traverseClass.getSuperclass();
		}

		return method;
	}

	private static Method findSetter(String methodName, Class<?>... classes) {
		for (Class<?> clazz : classes) {
			for (Method m : clazz.getDeclaredMethods()) {
				if (m.getName().equals(methodName) && isSetter(m)) {
					return m;
				}
			}
		}

		return null;
	}

	private static boolean isSetter(Method m) {
		return m != null && m.getReturnType().equals(void.class)
				&& m.getParameterTypes().length == 1;
	}
}
