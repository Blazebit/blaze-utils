package com.blazebit.cdi.exclude;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.spi.*;
import java.lang.reflect.Method;
import java.util.List;

import static org.apache.deltaspike.core.util.ReflectionUtils.invokeMethod;

public class InjectableMethod {

    private final CreationalContext<?> creationalContext;
    private final BeanManager beanManager;

    /**
     * Injectable method
     */
    protected Method method;

    /**
     * Bean parent instance that owns the method
     */
    protected Object ownerInstance;

    /**
     * If this method is dispose method
     */
    private boolean disposable;

    /**
     * Used in dispose method, represents produces method parameter instance
     */
    private Object producerMethodInstance = null;

    private List<InjectionPoint> injectionPoints;

    public InjectableMethod(AnnotatedMethod<?> annotatedMethod, Object instance, Bean<?> declaringBean, CreationalContext<?> creationalContext, BeanManager beanManager, List<InjectionPoint> injectionPoints) {
        this.method = annotatedMethod.getJavaMember();
        this.ownerInstance = instance;
        this.creationalContext = creationalContext;
        this.beanManager = beanManager;
        this.injectionPoints = injectionPoints;
    }

    public Object doInjection() {
        Object[] parameterValues = new Object[injectionPoints.size()];
        for (int i = 0; i < injectionPoints.size(); i++) {
            parameterValues[i] = beanManager.getInjectableReference(injectionPoints.get(i), creationalContext);
        }
        for (int i = 0; i < injectionPoints.size(); i++) {
            for (InjectionPoint point : injectionPoints) {
                AnnotatedParameter<?> parameter = (AnnotatedParameter<?>) point.getAnnotated();

                if (isDisposable() && parameter.getAnnotation(Disposes.class) != null) {
                    parameterValues[i] = producerMethodInstance;
                } else {
                    parameterValues[i] = beanManager.getInjectableReference(point, creationalContext);
                }
            }
        }

        try {
            return invokeMethod(ownerInstance, method, Object.class, true, parameterValues);
        } catch (RuntimeException e) {
            //X TODO check if it is compatible with Weld
            //workaround for OWB which wraps InvocationTargetException the original exception
            //see ReflectionUtils#invokeMethod
            if (RuntimeException.class.getName().equals(e.getClass().getName()) && e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw e;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    private boolean isDisposable() {
        return disposable;
    }

    public void setDisposable(boolean disposable) {
        this.disposable = disposable;
    }

    public void setProducerMethodInstance(Object instance) {
        producerMethodInstance = instance;
    }
}