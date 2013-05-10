package com.blazebit.lang;

/**
 * ValueAccessor can be used to access a value of a target object in a stateless
 * manner. {@link ValueAccessor}s are immutable and the methods may throw
 * {@link RuntimeException}s.
 * 
 * @author Christian Beikov
 * @since 1.0
 * @param <X>
 *            The type of the target object from which to retrieve the value.
 * @param <Y>
 *            The type of the value that is accessed.
 */
public interface ValueAccessor<X, Y> extends ValueRetriever<X, Y> {

	/**
	 * Sets the given value specified in the target object. An implementation
	 * may throw {@link RuntimeException}s.
	 * 
	 * @param target
	 *            The target object on which to set the given value.
	 * @param value
	 *            The new value of the target object.
	 */
	public void setValue(X target, Y value);

}
