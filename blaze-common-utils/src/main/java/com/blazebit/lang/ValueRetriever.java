package com.blazebit.lang;

/**
 * ValueRetrievers can be used to access a value of a target object in a
 * stateless manner. {@link ValueRetriever}s are immutable and the methods may
 * throw {@link RuntimeException}s.
 * 
 * @author Christian Beikov
 * @since 1.0
 * @param <X>
 *            The type of the target object from which to retrieve the value.
 * @param <Y>
 *            The type of the value that is retrieved.
 */
public interface ValueRetriever<X, Y> {

	/**
	 * Retrieves the value specified by this ValueRetriever of the target
	 * object. Subsequent call may return different values. An implementation
	 * may throw {@link RuntimeException}s.
	 * 
	 * @param target
	 *            The target object from which to retrieve the value.
	 * @return The value of the target object.
	 */
	public Y getValue(X target);

}
