package com.blazebit.cdi;

import java.lang.reflect.Method;


public class SimpleInvocationContext extends InvocationContextAdapter {
    private final Exception ex;
    private final Object target;
    private final Method m;
    
    public SimpleInvocationContext(Exception ex, Object target, Method m) {
        super();
        this.ex = ex;
        this.target = target;
        this.m = m;
    }

    @Override
    public Object proceed() throws Exception {
        throw ex;
    }
    
    @Override
    public Object getTarget() {
        return target;
    }
    @Override
    public Method getMethod() {
        return m;
    }
}
