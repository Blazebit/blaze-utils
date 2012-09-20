/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.blazebit.lang.ValueAccessor;

/**
 * This class can be used to predefine a getter chain invocation but to be
 * invoked later. It holds the source object on which to invoke the getter chain
 * and the field names with which the getter methods are determined.
 * 
 * @author Christian Beikov
 * @since 1.0
 */
public class PropertyPathExpression<X, Y> implements ValueAccessor<X, Y> {
	private final Class<X> source;
	private String[] explodedPropertyPath;
	private Method[] getterChain;
	private Method leafGetter;
	private Method leafSetter;
	private volatile boolean dirty = true;

	/**
	 * Constructs a LazyGetterMethod object for the given source object and
	 * field names as a string separated by '.' (dots). Using this constructor
	 * is equal to #
	 * {@link PropertyPathExpression#LazyGetterMethod(java.lang.Object, java.lang.String[]) }
	 * with the second parameter <code>fieldNames.split("\\.")</code>.
	 * 
	 * @param source
	 *            The object on which to invoke the first getter
	 * @param propertyPath
	 *            The field names which should be used for the getter
	 *            determination
	 */
	public PropertyPathExpression(Class<X> source, String propertyPath) {
		this(source, propertyPath.split("\\."));
	}

	/**
	 * Constructs a LazyGetterMethod object for the given source object and
	 * field names as a string array.
	 * 
	 * @param source
	 *            The object on which to invoke the first getter
	 * @param explodedPropertyPath
	 *            The field names which should be used for the getter
	 *            determination
	 */
	private PropertyPathExpression(Class<X> source, String[] explodedPropertyPath) {
		if (source == null) {
			throw new NullPointerException("source");
		}
		
		this.source = source;
		this.explodedPropertyPath = explodedPropertyPath;
	}
	
	private void initialize(){
		if(dirty){
			synchronized (this) {
				if(dirty){
					final int getterChainLength = explodedPropertyPath.length - 1;
					this.getterChain = new Method[getterChainLength];
					
					Class<?> current = source;
					
					if(getterChainLength > 0){
						/* Retrieve the getters for the field names and also resolve the return type */
						for (int i = 0; i < getterChainLength; i++) {
							getterChain[i] = ReflectionUtils.getGetter(current, explodedPropertyPath[i]);
							current = ReflectionUtils.getResolvedMethodReturnType(current, getterChain[i]);
						}
					}
					
					/* TODO: Think about what to do when a getter or setter is missing */
					/* Retrieve the leaf methods for get and set access */
					leafGetter = ReflectionUtils.getGetter(current, explodedPropertyPath[getterChainLength]);
					leafSetter = ReflectionUtils.getSetter(current, explodedPropertyPath[getterChainLength]);
					/* Finally let GC do some work ;) */
					explodedPropertyPath = null;
					dirty = false;
				}
			}
		}
	}


	/**
	 * Invokes the getter chain based on the source object. First the source
	 * object is used as invocation target for the first getter then the results
	 * of the previous operations will be used for the invocation.
	 * 
	 * Example of how the chaining works:
	 * 
	 * class A{ B getB(){ // return b element } }
	 * 
	 * class B{ String getA(){ // return a element } }
	 * 
	 * new LazyGetterMethod(new A(), "a.b").invoke()
	 * 
	 * is equal to
	 * 
	 * new A().getB().getA()
	 * 
	 * @return The result of the last getter in the chain
	 * @throws InvocationTargetException
	 *             {@link Method#invoke(java.lang.Object, java.lang.Object[]) }
	 * @throws IllegalAccessException
	 *             {@link Method#invoke(java.lang.Object, java.lang.Object[]) }
	 */
	public final Y getValue(X target){
		return getValue(target, false);
	}
	
	public final Y getNullSafeValue(X target){
		return getValue(target, true);
	}
	
	@SuppressWarnings("unchecked")
	private Y getValue(X target, boolean nullSafe){
		initialize();
		try{
			Object leafObj = getLeafObject(target, nullSafe);
			return nullSafe && leafObj == null ? null : (Y) leafGetter.invoke(leafObj);
		}catch(RuntimeException ex){
			throw ex;
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
	
	public final void setValue(X target, Y value){
		initialize();
		
		try{
			leafSetter.invoke(getLeafObject(target, false), value);
		}catch(RuntimeException ex){
			throw ex;
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
	
	private Object getLeafObject(X target, boolean nullSafe) throws IllegalAccessException, InvocationTargetException{
		if(nullSafe && target == null){
			return null;
		}
		
		if(!source.isInstance(target)){
			throw new IllegalArgumentException("Given target is not instance of the source class");
		}
		
		Object current = target;

		if(getterChain.length > 0){
			for (Method m : getterChain) {
				current = m.invoke(current);
				
				if(current == null){
					if(nullSafe){
						return null;
					}
					
					throw new NullPointerException(new StringBuilder(m.getName()).append(" returned null").toString());
				}
			}
		}
		
		return current;
	}
}
