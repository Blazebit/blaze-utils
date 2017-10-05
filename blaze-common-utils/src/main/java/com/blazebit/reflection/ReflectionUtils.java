/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * Utillity class for reflection specific actions. This class only uses basic
 * reflection mechanisms provided by the Reflection API. It provides methods
 * that are missing in the standard API and Apache Commons Utils.
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
public final class ReflectionUtils {

    private static final Map<String, Class<?>> PRIMITIVE_NAME_TO_TYPE;
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER;
    private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE;

    static {
        Map<String, Class<?>> primitiveNameToType = new HashMap<String, Class<?>>();
        primitiveNameToType.put("int", Integer.TYPE);
        primitiveNameToType.put("long", Long.TYPE);
        primitiveNameToType.put("double", Double.TYPE);
        primitiveNameToType.put("float", Float.TYPE);
        primitiveNameToType.put("boolean", Boolean.TYPE);
        primitiveNameToType.put("char", Character.TYPE);
        primitiveNameToType.put("byte", Byte.TYPE);
        primitiveNameToType.put("void", Void.TYPE);
        primitiveNameToType.put("short", Short.TYPE);
        PRIMITIVE_NAME_TO_TYPE = Collections.unmodifiableMap(primitiveNameToType);

        Map<Class<?>, Class<?>> primitiveToWrapper = new HashMap<Class<?>, Class<?>>();
        primitiveToWrapper.put(int.class, Integer.class);
        primitiveToWrapper.put(long.class, Long.class);
        primitiveToWrapper.put(double.class, Double.class);
        primitiveToWrapper.put(float.class, Float.class);
        primitiveToWrapper.put(boolean.class, Boolean.class);
        primitiveToWrapper.put(char.class, Character.class);
        primitiveToWrapper.put(byte.class, Byte.class);
        primitiveToWrapper.put(void.class, Void.class);
        primitiveToWrapper.put(short.class, Short.class);
        PRIMITIVE_TO_WRAPPER = Collections.unmodifiableMap(primitiveToWrapper);

        Map<Class<?>, Class<?>> wrapperToPrimitive = new HashMap<Class<?>, Class<?>>();
        wrapperToPrimitive.put(Integer.class, int.class);
        wrapperToPrimitive.put(Long.class, long.class);
        wrapperToPrimitive.put(Double.class, double.class);
        wrapperToPrimitive.put(Float.class, float.class);
        wrapperToPrimitive.put(Boolean.class, boolean.class);
        wrapperToPrimitive.put(Character.class, char.class);
        wrapperToPrimitive.put(Byte.class, byte.class);
        wrapperToPrimitive.put(Void.class, void.class);
        wrapperToPrimitive.put(Short.class, short.class);
        WRAPPER_TO_PRIMITIVE = Collections.unmodifiableMap(wrapperToPrimitive);
    }

    private ReflectionUtils() {
    }

    /**
     * Returns the class object for the specified qualified class name. Calling
     * this method is equal to #{@link Class#forName(java.lang.String)} except
     * that also primitive types can be get via this method. The names for the
     * primitive types needed for this method are equal to the type literals
     * used in the java language.
     * <p>
     * Example:
     * <p>
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
        Class<?> clazz = PRIMITIVE_NAME_TO_TYPE.get(className);

        if (clazz == null) {
            clazz = Class.forName(className);
        }

        return clazz;
    }

    /**
     * Returns the wrapper class of the given primitive class or the given class.
     *
     * @param primitive The primitive class
     * @return The wrapper class or the given class
     */
    public static Class<?> getObjectClassOfPrimitve(Class<?> primitive) {
        Class<?> objectClass = PRIMITIVE_TO_WRAPPER.get(primitive);
        if (objectClass != null) {
            return objectClass;
        }

        return primitive;
    }

    /**
     * Returns the wrapper class of the given primitive class or null.
     *
     * @param primitive The primitive class
     * @return The wrapper class or null
     */
    public static Class<?> getWrapperClassOfPrimitve(Class<?> primitive) {
        return PRIMITIVE_TO_WRAPPER.get(primitive);
    }

    /**
     * Returns the primitive class of the given wrapper class or null.
     *
     * @param wrapperClass The wrapper class
     * @return The primitive class or null
     */
    public static Class<?> getPrimitiveClassOfWrapper(Class<?> wrapperClass) {
        return WRAPPER_TO_PRIMITIVE.get(wrapperClass);
    }

    /**
     * Checks if the target class is a subtype of the supertype or not.
     * Basically this method calls @link{java.lang.Class#isAssignableFrom(Class)}
     * and therefore only acts as an alias.
     *
     * @param targetClazz the class to check wether it is a subtype of the supertype or
     *                    not
     * @param superType   the supertype class
     * @return true if targetClazz is subtype of superType or targetClazz equals
     * superType, otherwise false
     */
    public static boolean isSubtype(Class<?> targetClazz, Class<?> superType) {
        return superType.isAssignableFrom(targetClazz);
    }

    /**
     * Retrieves all super types of the given class type. Super types are all
     * types the given class extends or implements. The given class type is also
     * included in the set. The iteration order of the set has to be from most
     * concrete to most general.
     *
     * @param clazz The class from which the super types should be retrieved
     * @return The super types of the given class
     */
    public static Set<Class<?>> getSuperTypes(Class<?> clazz) {
        return getSuperTypes(clazz, Object.class);
    }

    public static Set<Class<?>> getSuperTypes(Class<?> clazz, Class<?> commonSuperType) {
        Set<Class<?>> list = new LinkedHashSet<Class<?>>();
        addSuperTypes(list, clazz, commonSuperType);
        return list;
    }

    private static void addSuperTypes(Set<Class<?>> superTypes, Class<?> clazz, Class<?> commonSuperType) {
        Class<?> traverseClass = clazz;

        do {
            if (isSubtype(traverseClass, commonSuperType)) {
                superTypes.add(traverseClass);
            }
            for (Class<?> interfaceClass : traverseClass.getInterfaces()) {
                if (isSubtype(interfaceClass, commonSuperType)) {
                    superTypes.add(interfaceClass);
                }
            }
            for (Class<?> interfaceClass : traverseClass.getInterfaces()) {
                if (isSubtype(interfaceClass, commonSuperType)) {
                    addSuperTypes(superTypes, interfaceClass, commonSuperType);
                }
            }
            traverseClass = traverseClass.getSuperclass();
        } while (traverseClass != null);
    }

    /**
     * Returns the type of a field if it exists within the class. Calling this
     * method is equal to calling #
     * {@link ReflectionUtils#getField(java.lang.Class, java.lang.String)
     * } with
     * a null check and finally return the type via getType().
     *
     * @param clazz     The class within to look for the field with the given field
     *                  name
     * @param fieldName The name of the field to be returned
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

    public static Class<?>[] getResolvedFieldTypeArguments(Class<?> clazz, String fieldName) {
        return getResolvedFieldTypeArguments(clazz, getField(clazz, fieldName));
    }

    public static Class<?>[] getResolvedFieldTypeArguments(Class<?> clazz, Field f) {
        if (f == null) {
            return null;
        }

        return resolveTypeArguments(clazz, f.getGenericType());
    }

    public static Class<?>[] resolveTypeArguments(Class<?> concreteClass, Type type) {
        if (type instanceof ParameterizedType) {
            return resolveTypeArguments(concreteClass, (ParameterizedType) type);
        }

        return new Class<?>[0];
    }

    public static Class<?>[] resolveTypeArguments(Class<?> concreteClass, ParameterizedType parameterizedType) {
        if (parameterizedType == null) {
            return null;
        }

        Type[] argumentTypes = parameterizedType.getActualTypeArguments();
        Class<?>[] resolvedClasses = new Class<?>[argumentTypes.length];

        for (int i = 0; i < argumentTypes.length; i++) {
            resolvedClasses[i] = resolveType(concreteClass, argumentTypes[i]);
        }

        return resolvedClasses;
    }

    private static Class<?> resolveType(Class<?> concreteClass, Type type) {
        if (type instanceof TypeVariable<?>) {
            return resolveTypeVariable(concreteClass, (TypeVariable<?>) type);
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            Class<?> componentClass;
            if (componentType instanceof Class) {
                componentClass = (Class<?>) componentType;
            } else if (componentType instanceof ParameterizedType) {
                componentClass = (Class<?>) ((ParameterizedType) componentType).getRawType();
            } else {
                throw new IllegalArgumentException("Unsupported array component type: " + componentType);
            }

            Object o = Array.newInstance((Class<?>) componentClass, 0);
            return (Class<?>) o.getClass();
        } else if (type instanceof WildcardType) {
            WildcardType wildcardType = ((WildcardType) type);

            if (wildcardType.getLowerBounds().length > 0) {
                return resolveType(concreteClass, wildcardType.getLowerBounds()[0]);
            } else {
                return resolveType(concreteClass, wildcardType.getUpperBounds()[0]);
            }
        } else {
            // We assume here that only class types, type variables and parameterized types are
            // possible as argument types for the parameterized type
            return (Class<?>) type;
        }
    }

    private static Class<?> getClassThatContainsTypeVariable(TypeVariable<?> typeVariable) {
        if (isSubtype(typeVariable.getGenericDeclaration().getClass(),
                Class.class)) {
            return (Class<?>) typeVariable
                    .getGenericDeclaration();
        } else if (isSubtype(typeVariable.getGenericDeclaration().getClass(),
                Method.class)) {
            return ((Method) typeVariable
                    .getGenericDeclaration()).getDeclaringClass();
        } else if (isSubtype(typeVariable.getGenericDeclaration().getClass(),
                Constructor.class)) {
            return ((Constructor<?>) typeVariable
                    .getGenericDeclaration()).getDeclaringClass();
        }

        return null;
    }

    /**
     * Tries to resolve the type variable againts the concrete class. The
     * concrete class has to be a subtype of the type in which the type variable
     * has been declared. This method tries to resolve the given type variable
     * by inspecting the subclasses of the class in which the type variable was
     * declared and as soon as the resolved type is instance of java.lang.Class
     * it stops and returns that class.
     *
     * @param concreteClass The class which is used to resolve the type. The type for the
     *                      type variable must be bound in this class or a superclass.
     * @param typeVariable  The type variable to resolve.
     * @return The resolved type as class or null if the type can not be
     * resolved.
     * @throws IllegalArgumentException Is thrown when the concrete class is not a subtype of the
     *                                  class in which the type variable has been declared.
     */
    public static Class<?> resolveTypeVariable(Class<?> concreteClass, TypeVariable<?> typeVariable) {
        Class<?> classThatContainsTypeVariable = getClassThatContainsTypeVariable(typeVariable);

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

        Set<Class<?>> superTypes = getSuperTypes(concreteClass, classThatContainsTypeVariable);
        List<Class<?>> classStack = new ArrayList<Class<?>>();
        Type resolvedType = typeVariable;

        // The class that contains the type variable mustn't be considered
        superTypes.remove(classThatContainsTypeVariable);
        // Build a stack of the class hierarchy to be able to resolve the type
        for (Class<?> superType : superTypes) {
            classStack.add(superType);
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
            Class<?> classToInspect = classStack.remove(classStack.size() - 1);
            Type[] genericInterfaces = classToInspect.getGenericInterfaces();
            List<Type> typesToInspect = new ArrayList<Type>(genericInterfaces.length + 1);
            typesToInspect.add(classToInspect.getGenericSuperclass());
            Collections.addAll(typesToInspect, genericInterfaces);

            for (Type classToInspectType : typesToInspect) {
                // Since the resolvedType is not yet instance of class, the
                // classToInspectType has to be a ParameterizedType
                // otherwise we can not resolve the type variable
                if (!(classToInspectType instanceof ParameterizedType)) {
                    continue;
                }

                ParameterizedType parameterizedClassToInspect = (ParameterizedType) classToInspectType;

                // The found parameterized type is not the one we are looking for
                if (!classThatContainsTypeVariable.equals(parameterizedClassToInspect.getRawType())) {
                    continue;
                }

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
                    classThatContainsTypeVariable = getClassThatContainsTypeVariable((TypeVariable<?>) resolvedType);
                    break;
                } else if (resolvedType instanceof ParameterizedType) {
                    // Since we only want a class object, we don't
                    // care about type arguments of the parameterized type
                    // and just set the raw type of it as the resolved
                    // type
                    resolvedType = ((ParameterizedType) resolvedType).getRawType();
                    break;
                } else if (resolvedType instanceof WildcardType) {
                    WildcardType wildcardType = ((WildcardType) resolvedType);

                    if (wildcardType.getLowerBounds().length > 0) {
                        resolvedType = resolveType(concreteClass, wildcardType.getLowerBounds()[0]);
                    } else {
                        resolvedType = resolveType(concreteClass, wildcardType.getUpperBounds()[0]);
                    }
                    break;
                } else if (resolvedType instanceof Class<?>) {
                    break;
                }
            }
        }

        if (resolvedType instanceof Class<?>) {
            return (Class<?>) resolvedType;
        } else if (resolvedType instanceof TypeVariable<?>) {
            Type boundType = ((TypeVariable<?>) resolvedType).getBounds()[0];

            if (boundType instanceof Class<?>) {
                return (Class<?>) boundType;
            } else if (boundType instanceof ParameterizedType) {
                return (Class<?>) ((ParameterizedType) boundType).getRawType();
            }
        } else if (resolvedType instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) resolvedType).getRawType();
        } else if (resolvedType instanceof WildcardType) {
            WildcardType wildcardType = ((WildcardType) resolvedType);

            if (wildcardType.getLowerBounds().length > 0) {
                return resolveType(concreteClass, wildcardType.getLowerBounds()[0]);
            } else {
                return resolveType(concreteClass, wildcardType.getUpperBounds()[0]);
            }
        }

        throw new IllegalArgumentException("Could not resolve the type variable '" + typeVariable + "' for the concrete class " + concreteClass.getName() + ". The resolved type is unknown: " + resolvedType);
    }

    /**
     * Returns the position of the type variable for the class in which it is
     * declared.
     *
     * @param typeVariable The type variable for which the position should be retrieved
     * @return The position of the type variable within the class in which it is
     * declared.
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
     * @param genericDeclartion The generic declartion type in which to look for the type
     *                          variable
     * @param typeVariable      The type variable to look for in the given class type
     *                          parameters
     * @return The position of the given type variable within the type
     * parameters of the given class if found, otherwise -1
     */
    public static int getTypeVariablePosition(GenericDeclaration genericDeclartion, TypeVariable<?> typeVariable) {
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
     * Returns the static field objects that are declared in the given class or
     * any of it's super types. Calling this method is equivalent to a call to
     * {@link ReflectionUtils#getNonMatchingFields(Class, int)} with the
     * modifiers {@link Modifier#STATIC}.
     *
     * @param clazz The class within to look for the fields with the given
     *              modifiers
     * @return The array of static fields that are within the type hierarchy of
     * the given class
     */
    public static Field[] getInstanceFields(Class<?> clazz) {
        return getNonMatchingFields(clazz, Modifier.STATIC);
    }

    /**
     * Returns the static field objects that are declared in the given class or
     * any of it's super types. Calling this method is equivalent to a call to
     * {@link ReflectionUtils#getMatchingFields(Class, int)} with the modifiers
     * {@link Modifier#STATIC}.
     *
     * @param clazz The class within to look for the fields with the given
     *              modifiers
     * @return The array of static fields that are within the type hierarchy of
     * the given class
     */
    public static Field[] getStaticFields(Class<?> clazz) {
        return getMatchingFields(clazz, Modifier.STATIC);
    }

    private static final Comparator<Field> FIELD_NAME_AND_DECLARING_CLASS_COMPARATOR = new Comparator<Field>() {

        @Override
        public int compare(Field o1, Field o2) {
            int result = o1.getName().compareTo(o2.getName());
            return result == 0 ? o1.getDeclaringClass().getName()
                    .compareTo(o2.getDeclaringClass().getName()) : result;
        }
    };

    /**
     * Returns the field objects that are declared in the given class or any of
     * it's super types that have any of the given modifiers. The type hierarchy
     * is traversed upwards and all declared fields that match the given
     * modifiers are added to the result array. The elements in the array are
     * sorted by their names and declaring classes.
     *
     * @param clazz     The class within to look for the fields with the given
     *                  modifiers
     * @param modifiers The OR-ed together modifiers that a field must match to be
     *                  included into the result
     * @return The array of fields that match the modifiers and are within the
     * type hierarchy of the given class
     */
    public static Field[] getMatchingFields(Class<?> clazz, final int modifiers) {
        final Set<Field> fields = new TreeSet<Field>(
                FIELD_NAME_AND_DECLARING_CLASS_COMPARATOR);
        traverseHierarchy(clazz, new TraverseTask<Field>() {

            @Override
            public Field run(Class<?> clazz) {
                Field[] fieldArray = clazz.getDeclaredFields();
                for (int i = 0; i < fieldArray.length; i++) {
                    if ((modifiers & fieldArray[i].getModifiers()) != 0) {
                        fields.add(fieldArray[i]);
                    }
                }
                return null;
            }
        });

        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * Returns the field objects that are declared in the given class or any of
     * it's super types that have none of the given modifiers. The type
     * hierarchy is traversed upwards and all declared fields that do not match
     * the given modifiers are added to the result array. The elements in the
     * array are sorted by their names and declaring classes.
     *
     * @param clazz     The class within to look for the fields with the given
     *                  modifiers
     * @param modifiers The OR-ed together modifiers that a field must not match to be
     *                  included into the result
     * @return The array of fields that do not match the modifiers and are
     * within the type hierarchy of the given class
     */
    public static Field[] getNonMatchingFields(Class<?> clazz, final int modifiers) {
        final Set<Field> fields = new TreeSet<Field>(
                FIELD_NAME_AND_DECLARING_CLASS_COMPARATOR);
        traverseHierarchy(clazz, new TraverseTask<Field>() {

            @Override
            public Field run(Class<?> clazz) {
                Field[] fieldArray = clazz.getDeclaredFields();
                for (int i = 0; i < fieldArray.length; i++) {
                    if ((modifiers & fieldArray[i].getModifiers()) == 0) {
                        fields.add(fieldArray[i]);
                    }
                }
                return null;
            }
        });

        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * Returns the field object found for the given field name in the given
     * class. This method traverses through the super classes of the given class
     * and tries to find the field as declared field within these classes. When
     * the object class is reached the traversing stops. If the field can not be
     * found, null is returned.
     *
     * @param clazz     The class within to look for the field with the given field
     *                  name
     * @param fieldName The name of the field to be returned
     * @return The field object with the given field name if the field can be
     * found, otherwise null
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        final String internedName = fieldName.intern();
        return traverseHierarchy(clazz, new TraverseTask<Field>() {

            @Override
            public Field run(Class<?> clazz) {
                Field[] fields = clazz.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].getName() == internedName) {
                        return fields[i];
                    }
                }
                return null;
            }
        });
    }

    /**
     * Returns the return type of a method if it exists within the class.
     * Calling this method is equal to calling {@link ReflectionUtils#getMethod(Class, String, Class[])} with
     * a null check and finally return the type via getReturnType().
     *
     * @param clazz          The class within to look for the method with the given method
     *                       name
     * @param methodName     The name of the method to be returned
     * @param parameterTypes The accepting parameter types of the method
     * @return The return type of the method if it can be found, otherwise null
     * @see ReflectionUtils#getMethod(Class, String, Class[])
     */
    public static Class<?> getMethodReturnType(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Method m = getMethod(clazz, methodName, parameterTypes);

        if (m == null) {
            return null;
        }

        return m.getReturnType();
    }

    public static Class<?>[] getMethodParameterTypes(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Method m = getMethod(clazz, methodName, parameterTypes);

        if (m == null) {
            return null;
        }

        return m.getParameterTypes();
    }

    public static Class<?>[] getMethodExceptionTypes(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Method m = getMethod(clazz, methodName, parameterTypes);

        if (m == null) {
            return null;
        }

        return m.getExceptionTypes();
    }

    public static Class<?> getResolvedMethodReturnType(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
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

    public static Class<?>[] getResolvedMethodReturnTypeArguments(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        return getResolvedMethodReturnTypeArguments(clazz,
                getMethod(clazz, methodName, parameterTypes));
    }

    public static Class<?>[] getResolvedMethodReturnTypeArguments(Class<?> clazz, Method m) {
        if (m == null) {
            return null;
        }

        return resolveTypeArguments(clazz, m.getGenericReturnType());
    }

    public static Class<?>[] getResolvedMethodParameterTypes(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        return getResolvedMethodParameterTypes(clazz,
                getMethod(clazz, methodName, parameterTypes));
    }

    public static Class<?>[] getResolvedMethodParameterTypes(Class<?> clazz, Method m) {
        if (m == null) {
            return null;
        }

        Type[] genericParameterTypes = m.getGenericParameterTypes();
        Class<?>[] parameterTypes = new Class<?>[genericParameterTypes.length];

        for (int i = 0; i < genericParameterTypes.length; i++) {
            if (genericParameterTypes[i] instanceof TypeVariable<?>) {
                parameterTypes[i] = resolveTypeVariable(clazz,
                        (TypeVariable<?>) genericParameterTypes[i]);
            } else if (genericParameterTypes[i] instanceof ParameterizedType) {
                parameterTypes[i] = (Class<?>) ((ParameterizedType) genericParameterTypes[i]).getRawType();
            } else {
                parameterTypes[i] = (Class<?>) genericParameterTypes[i];
            }
        }

        return parameterTypes;
    }

    public static Class<?>[][] getResolvedMethodParameterTypesArguments(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        return getResolvedMethodParameterTypesArguments(clazz, getMethod(clazz, methodName, parameterTypes));
    }

    public static Class<?>[][] getResolvedMethodParameterTypesArguments(Class<?> clazz, Method method) {
        int parameterCount = method.getParameterTypes().length;
        Class<?>[][] parameterTypeArguments = new Class<?>[parameterCount][];
        Type[] genericParameterTypes = method.getGenericParameterTypes();

        for (int i = 0; i < parameterCount; i++) {
            parameterTypeArguments[i] = resolveTypeArguments(clazz, genericParameterTypes[i]);
        }

        return parameterTypeArguments;
    }

    public static Class<?>[] getResolvedMethodExceptionTypes(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        return getResolvedMethodExceptionTypes(clazz,
                getMethod(clazz, methodName, parameterTypes));
    }

    public static Class<?>[] getResolvedMethodExceptionTypes(Class<?> clazz, Method m) {
        if (m == null) {
            return null;
        }

        Type[] genericExceptionTypes = m.getGenericExceptionTypes();
        Class<?>[] exceptionTypes = new Class<?>[genericExceptionTypes.length];

        for (int i = 0; i < genericExceptionTypes.length; i++) {
            if (genericExceptionTypes[i] instanceof TypeVariable<?>) {
                exceptionTypes[i] = resolveTypeVariable(clazz,
                        (TypeVariable<?>) genericExceptionTypes[i]);
            } else if (genericExceptionTypes[i] instanceof ParameterizedType) {
                exceptionTypes[i] = (Class<?>) ((ParameterizedType) genericExceptionTypes[i]).getRawType();
            } else {
                exceptionTypes[i] = (Class<?>) genericExceptionTypes[i];
            }
        }

        return exceptionTypes;
    }

    public static MethodParameter[] getMethodParameters(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
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

    public static MethodException[] getMethodExceptions(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
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
     * @param clazz          The class within to look for the method with the given method
     *                       name
     * @param methodName     The name of the method to be returned
     * @param parameterTypes The accepting parameter types of the method
     * @return The method object with the given method name if the method can be
     * found, otherwise null
     */
    public static Method getMethod(Class<?> clazz, final String methodName, final Class<?>... parameterTypes) {
        final String internedName = methodName.intern();
        return traverseHierarchy(clazz, new TraverseTask<Method>() {

            @Override
            public Method run(Class<?> clazz) {
                Method[] methods = clazz.getDeclaredMethods();
                Method res = null;

                for (int i = 0; i < methods.length; i++) {
                    Method m = methods[i];
                    if (m.getName() == internedName
                            && arrayContentsEq(parameterTypes, m.getParameterTypes())
                            && (res == null
                            || res.getReturnType().isAssignableFrom(m.getReturnType()))) {
                        res = m;
                    }
                }

                return res;
            }
        });
    }

    private static interface TraverseTask<T> {
        public T run(Class<?> clazz);
    }

    private static <T> T traverseHierarchy(Class<?> clazz, TraverseTask<T> task) {
        Queue<Class<?>> classQueue = new LinkedList<Class<?>>();
        Class<?> traverseClass;
        classQueue.add(clazz);

        while (!classQueue.isEmpty()) {
            traverseClass = classQueue.remove();

            T result = task.run(traverseClass);

            if (result != null) {
                return result;
            }

            if (traverseClass.getSuperclass() != null) {
                classQueue.add(traverseClass.getSuperclass());
            }

            for (Class<?> interfaceClass : traverseClass.getInterfaces()) {
                classQueue.add(interfaceClass);
            }
        }

        return null;
    }

    private static boolean arrayContentsEq(Object[] a1, Object[] a2) {
        if (a1 == null) {
            return a2 == null || a2.length == 0;
        }

        if (a2 == null) {
            return a1.length == 0;
        }

        if (a1.length != a2.length) {
            return false;
        }

        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the method object for a method which is annotated with the
     * given annotation of the given class. This method traverses through
     * the super classes of the given class and tries to find the method as
     * declared method within these classes that is annotated with an
     * annotation of the given annotation type.
     * When the object class is reached the traversing stops. If the method can
     * not be found, null is returned.
     * This methods immediatelly returns the first method found that is
     * annotated with an annotation of the given type.
     * To retrieve all methods annotated with the given annotation type see
     * {@link ReflectionUtils#getMethods(java.lang.Class, java.lang.Class)}
     *
     * @param clazz      The class within to look for the method
     * @param annotation The annotation type a method must be annotated with to be
     *                   returned
     * @return The method object for the method annotated with the given
     * annotation type if the method can be found, otherwise null
     */
    public static Method getMethod(Class<?> clazz, final Class<? extends Annotation> annotation) {
        return traverseHierarchy(clazz, new TraverseTask<Method>() {

            @Override
            public Method run(Class<?> clazz) {
                Method[] methods = clazz.getDeclaredMethods();

                for (int i = 0; i < methods.length; i++) {
                    Method m = methods[i];
                    if (m.getAnnotation(annotation) != null) {
                        return m;
                    }
                }

                return null;
            }
        });
    }

    /**
     * Returns the method objects for methods which are annotated with the
     * given annotation of the given class. This method traverses through
     * the super classes of the given class and tries to find methods as
     * declared methods within these classes which are annotated with an
     * annotation of the given annotation type.
     * When the object class is reached the traversing stops. If no methods
     * can be found, an empty list is returned.
     * The order of the methods is random.
     *
     * @param clazz      The class within to look for the methods
     * @param annotation The annotation type a method must be annotated with to be
     *                   included in the list
     * @return A list of method objects for methods annotated with the given
     * annotation type or an emtpy list
     */
    public static List<Method> getMethods(Class<?> clazz, final Class<? extends Annotation> annotation) {
        final List<Method> methods = new ArrayList<Method>();
        traverseHierarchy(clazz, new TraverseTask<Method>() {

            @Override
            public Method run(Class<?> clazz) {
                Method[] methodArray = clazz.getDeclaredMethods();

                for (int i = 0; i < methodArray.length; i++) {
                    Method m = methodArray[i];
                    if (m.getAnnotation(annotation) != null) {
                        methods.add(m);
                    }
                }

                return null;
            }
        });
        return methods;
    }

    /**
     * Retrieves the getter method of the given class for the specified field
     * name. The method first tries to find the getFieldName method of the class
     * and if it can not find that method it looks for the isFieldName method.
     * If this method also can not be found, null is returned.
     * <p>
     * This method uses #
     * {@link ReflectionUtils#getMethodReturnType(Class, String, Class...)} to
     * retrieve the getter.
     * <p>
     * A getter must not have any parameters and must have a return type that is
     * different from void.
     *
     * @param clazz     The class within to look for the getter method
     * @param fieldName The field name for which to find the getter method
     * @return The getter method for the given fieldName if it can be found,
     * otherwise null
     */
    public static Method getGetter(Class<?> clazz, String fieldName) {
        StringBuilder sb = new StringBuilder("get").append(
                Character.toUpperCase(fieldName.charAt(0))).append(fieldName,
                1, fieldName.length());
        final String internedGetName = sb.toString().intern();
        final String internedIsName = sb.replace(0, 3, "is").toString().intern();
        return traverseHierarchy(clazz, new TraverseTask<Method>() {

            @Override
            public Method run(Class<?> clazz) {
                Method[] methods = clazz.getDeclaredMethods();
                Method res = null;

                for (int i = 0; i < methods.length; i++) {
                    Method m = methods[i];
                    if (isGetterSignature(m)) {
                        if (m.getName() == internedGetName
                                && (res == null
                                || res.getReturnType().isAssignableFrom(m.getReturnType()))) {
                            res = m;
                        }
                        if (m.getName() == internedIsName
                                && (res == null
                                || res.getReturnType().isAssignableFrom(m.getReturnType()))) {
                            res = m;
                        }
                    }
                }

                return res;
            }
        });
    }

    private static boolean isGetterSignature(Method m) {
        return m != null && !void.class.equals(m.getReturnType())
                && m.getParameterTypes().length == 0;
    }

    public static boolean isGetter(Method m) {
        return m != null && (m.getName().startsWith("get") || m.getName().startsWith("is"))
                && !void.class.equals(m.getReturnType()) && m.getParameterTypes().length == 0;
    }

    /**
     * Retrieves the setter method of the given class for the specified field
     * name. The method traverses through all methods of all types that are in
     * the inheritance hierarchie of the given class and tries to find a setter
     * method with the return type void and exactly one parameter. If that
     * method can not be found, null is returned.
     * <p>
     * A setter must have void return type and accept exactly one parameter.
     *
     * @param clazz     The class within to look for the setter method
     * @param fieldName The field name for which to find the setter method
     * @return The setter method for the given fieldName if it can be found,
     * otherwise null
     */
    public static Method getSetter(Class<?> clazz, String fieldName) {
        StringBuilder sb = new StringBuilder("set").append(
                Character.toUpperCase(fieldName.charAt(0))).append(fieldName,
                1, fieldName.length());
        final String internedName = sb.toString().intern();
        return traverseHierarchy(clazz, new TraverseTask<Method>() {

            @Override
            public Method run(Class<?> clazz) {
                Method[] methods = clazz.getDeclaredMethods();
                Method res = null;

                for (int i = 0; i < methods.length; i++) {
                    Method m = methods[i];
                    if (isSetterSignature(m)) {
                        if (m.getName() == internedName
                                && (res == null
                                || res.getParameterTypes()[0].isAssignableFrom(m.getParameterTypes()[0]))) {
                            res = m;
                        }
                    }
                }

                return res;
            }
        });
    }

    private static boolean isSetterSignature(Method m) {
        return m != null && m.getReturnType().equals(void.class)
                && m.getParameterTypes().length == 1;
    }

    public static boolean isSetter(Method m) {
        return m != null && m.getName().startsWith("set") && m.getReturnType().equals(void.class)
                && m.getParameterTypes().length == 1;
    }
}
