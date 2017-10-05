package com.blazebit.cdi.exclude;

import com.blazebit.cdi.exclude.annotation.ExcludeIfExists;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

public class DisposableProducer {

    public static boolean disposeCalled = false;

    @Produces
    @ApplicationScoped
    @ExcludeIfExists(StringHolder.class)
    public StringHolder getObject() {
        return new DisposableObject("Hello");
    }

    public void dispose(@Disposes StringHolder object) {
        disposeCalled = true;
    }
}
