/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

/**
 *
 * @author Christian Beikov
 */
public class MethodParameter {

    private Constructor<?> constructor;
    private Method method;
    private int index;
    
    MethodParameter(Method method, int index){
        if(method == null){
            throw new NullPointerException("Method must not be null");
        }
        
        this.method = method;
        this.index = index;
    }
    
    MethodParameter(Constructor<?> constructor, int index){
        if(constructor == null){
            throw new NullPointerException("Method must not be null");
        }
        
        this.constructor = constructor;
        this.index = index;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public int getIndex() {
        return index;
    }
    
    public Class<?> getType(){
        return method != null ? method.getParameterTypes()[index] : constructor.getParameterTypes()[index];
    }
    
    public Class<?> getResolvedType(Class<?> concreteClass){
        Type t = method != null ? method.getGenericParameterTypes()[index] : constructor.getGenericParameterTypes()[index];
        
        if(t instanceof TypeVariable<?>){
            return ReflectionUtil.resolveTypeVariable(concreteClass, (TypeVariable<?>) t);
        }
        
        return getType();
    }
    
    public Class<?>[] getResolvedTypeParameters(Class<?> concreteClass){
        Type t = method != null ? method.getGenericParameterTypes()[index] : constructor.getGenericParameterTypes()[index];
        return ReflectionUtil.resolveTypeArguments(concreteClass, t);
    }
    
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass){
        if(annotationClass == null){
            throw new NullPointerException("annotationClass must not be null");
        }
        
        for(Annotation a : getAnnotations()){
            if(annotationClass.isInstance(a)){
                return annotationClass.cast(a);
            }
        }
        
        return null;
    }
    
    public Annotation[] getAnnotations(){
        return method != null ? method.getParameterAnnotations()[index] : constructor.getParameterAnnotations()[index];
    }

    public Method getMethod() {
        return method;
    }
}
