package com.blazebit.cdi.exclude;

import com.blazebit.cdi.exclude.annotation.ExcludeIfExists;

import javax.enterprise.inject.Produces;

public class IntegerProducer2 {

    @Produces
    @ExcludeIfExists(Integer.class)
    public Integer getInteger() {
        return 2;
    }
}
