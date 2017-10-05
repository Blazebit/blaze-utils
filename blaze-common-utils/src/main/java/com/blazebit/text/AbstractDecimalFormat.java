/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import java.io.Serializable;

/**
 * @author Christian Beikov
 */
public abstract class AbstractDecimalFormat<T extends Serializable> extends
        AbstractFormat<T> {

    private static final long serialVersionUID = 1L;

    public AbstractDecimalFormat(Class<T> clazz) {
        super(clazz);
    }

}
