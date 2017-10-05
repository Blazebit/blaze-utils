package com.blazebit.cdi;

import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.Map;

public abstract class InvocationContextAdapter implements InvocationContext {

    @Override
    public void setParameters(Object[] parameters) {
    }

    @Override
    public Object getTimer() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public Map<String, Object> getContextData() {
        return null;
    }

    @Override
    public Object proceed() throws Exception {
        return null;
    }

    @Override
    public Object getTarget() {
        return null;
    }

    @Override
    public Method getMethod() {
        return null;
    }
}
