package com.blazebit.cdi.exclude;

import com.blazebit.cdi.exclude.annotation.ExcludeIfExists;

@ExcludeIfExists(value = ExcludeInterface2.class)
public class ExcludedInterface2Impl1 implements ExcludeInterface2 {

}
