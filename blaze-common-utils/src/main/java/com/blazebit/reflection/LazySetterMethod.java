/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class is a lazy setter that can be invoked later. Basically this class
 * just holds the target object on which to invoke the setter, the field name
 * via which the setter method is determined and the arguments that should be
 * passed as parameters to the setter.
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class LazySetterMethod {
	private final Object target;
	private final Object value;
	private final PropertyPathExpression<Object, Object> expression;

	/**
	 * Constructs a LazySetterMethod object for the given source object, field
	 * names as a string separated by '.' (dots) and arguments. Using this
	 * constructor is equal to #
	 * {@link LazySetterMethod#LazySetterMethod(java.lang.Object, java.lang.String[], java.lang.Object[]) }
	 * with the second parameter <code>fieldNames.split("\\.")</code>.
	 * 
	 * @param target
	 *            The object on which to invoke the getter/setter chain
	 * @param fieldNames
	 *            The field names which should be used for the setter
	 *            determination
	 * @param args
	 *            The arguments used for the setter method
	 */
	@SuppressWarnings("unchecked")
	public LazySetterMethod(Object target, String fieldNames, Object value) {
		if (target == null) {
			throw new NullPointerException("target");
		}

		this.target = target;
		this.value = value;
		this.expression = (PropertyPathExpression<Object, Object>) ExpressionUtils
				.getExpression(target.getClass(), fieldNames);
	}

	/**
	 * Constructs a LazySetterMethod object for the given source object, field
	 * names as a string separated by '.' (dots) and arguments.
	 * 
	 * This constructor is equal to #
	 * {@link LazySetterMethod#LazySetterMethod(java.lang.Object, java.lang.String, java.lang.Object[])}
	 * except that this constructor shows that #{@link LazyGetterMethod} also
	 * can be used as parameter. The LazyGetterMethods will be invoked lazily
	 * 
	 * @param target
	 *            The object on which to invoke the getter/setter chain
	 * @param fieldNames
	 *            The field names which should be used for the setter
	 *            determination
	 * @param args
	 *            The arguments used for the setter method
	 */
	public LazySetterMethod(Object target, String fieldNames,
			LazyGetterMethod value) {
		this(target, fieldNames, (Object) value);
	}

	/**
	 * Invokes the getter/setter chain based on the source object. First of all
	 * the actual arguments are determined by invoking LazyGetterMethod objects
	 * that have been passed during construction. Then the getter chain is
	 * invoked to determine the actual object on which to invoke the setter.
	 * Finally the setter is invoked with the actual arguments
	 * 
	 * Example of how the chaining works:
	 * 
	 * <code>
	 * class A{
	 *  private B b;
	 * 
	 *  public A(B b){
	 *   this.b = b;
	 *  }
	 *  
	 *  public B getB(){
	 *   return b;
	 *  }
	 * }
	 * 
	 * class B{
	 *  private String s;
	 * 
	 *  public B(String s){
	 *   this.s = s;
	 *  }
	 * 
	 *  public void setS(String s){
	 *   this.s = s;
	 *  }
	 * }
	 * </code>
	 * 
	 * <code>
	 * new LazySetterMethod(new A(new B("")), "b.s", "value").invoke()
	 * </code>
	 * 
	 * is equal to
	 * 
	 * <code>
	 * new A(new B("")).getB().setS("test");
	 * </code>
	 * 
	 * and
	 * 
	 * <code>
	 * new LazySetterMethod(new A(new B("")), "b.s", new LazyGetterMethod(new B("lazyValue"), "s").invoke()
	 * </code>
	 * 
	 * is equal to
	 * 
	 * <code>
	 * new A(new B("")).getB().setS(new B("lazyValue").getS());
	 * </code>
	 * 
	 * @throws InvocationTargetException
	 *             #{@link Method#invoke(java.lang.Object, java.lang.Object[]) }
	 * @throws IllegalAccessException
	 *             #{@link Method#invoke(java.lang.Object, java.lang.Object[]) }
	 */
	public void invoke() throws InvocationTargetException,
			IllegalAccessException {
		Object valueToPass = value;
		
		if (valueToPass != null && valueToPass instanceof LazyGetterMethod) {
			valueToPass = ((LazyGetterMethod) valueToPass).invoke();
		}
		
		expression.setValue(target, valueToPass);
	}
}
