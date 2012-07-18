/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParsePosition;

/**
 *
 * @author Christian Beikov
 */
public abstract class AbstractDecimalFormat<T extends Serializable> extends AbstractFormat<T> {

    public AbstractDecimalFormat(Class<T> clazz) {
        super(clazz);
    }

}
