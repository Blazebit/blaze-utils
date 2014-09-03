package com.blazebit.cdi.exclude;

import javax.enterprise.inject.Produces;

import com.blazebit.cdi.exclude.annotation.ExcludeIfExists;

public class IntegerProducer2 {

    @Produces
    @ExcludeIfExists(Integer.class)
    public Integer getInteger() {
        return 2;
    }
}
