package com.blazebit.ai.decisiontree;

/**
 *
 * @author Christian Beikov
 */
public interface Example<T> extends Item{
    
    public T getResult();
}
