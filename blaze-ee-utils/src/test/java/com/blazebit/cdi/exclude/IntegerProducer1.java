package com.blazebit.cdi.exclude;

import javax.enterprise.inject.Produces;


public class IntegerProducer1 {

    @Produces
    public Integer getInteger() {
        return 1;
    }
}
