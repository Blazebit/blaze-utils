package com.blazebit.lang;

/**
 * As the name says, value holders hold a value that can be accessed.
 * {@link ValueHolder}s are mutable and the methods may throw
 * {@link RuntimeException}s.
 *
 * @param <X> The type of the value that is held by the value holder.
 * @author Christian Beikov
 * @since 1.0
 */
public interface ValueHolder<X> {

    /**
     * Returns the value of the value holder. Subsequent call may return
     * different values. The concrete value holder may throw
     * {@link RuntimeException}s.
     *
     * @return The value of the value holder.
     */
    public X getValue();

    /**
     * Sets the value of the value holder. The concrete value holder may throw
     * {@link RuntimeException}s.
     *
     * @param value The new value of the value holder.
     */
    public void setValue(X value);

}
