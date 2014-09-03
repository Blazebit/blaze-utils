package com.blazebit.cdi.exclude;

import com.blazebit.cdi.exclude.annotation.ExcludeIfExists;

@ExcludeIfExists(value = ExcludeInterface1.class)
public class ExcludedInterface1Impl2 implements ExcludeInterface1 {

    @Override
    public String getName() {
        return "2";
    }

}
