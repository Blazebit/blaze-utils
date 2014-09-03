package com.blazebit.cdi.exclude;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.Producer;

import org.apache.deltaspike.core.util.BeanUtils;
import org.apache.deltaspike.core.util.metadata.builder.ContextualLifecycle;


public class ProducerMethodCreationalContext implements ContextualLifecycle<Object> {
    
    private final Bean<Object> declaringBean;
    private final AnnotatedMethod<?> producerMethod;
    private final AnnotatedMethod<?> disposalMethod;
    private final List<InjectionPoint> producerInjectionPoints;
    private final List<InjectionPoint> disposalInjectionPoints;
    private final BeanManager beanManager;

    public ProducerMethodCreationalContext(Bean<Object> declaringBean, AnnotatedMethod<?> producerMethod, BeanManager beanManager, boolean isOwbAndDependentScope) {
        this.declaringBean = declaringBean;
        this.producerMethod = producerMethod;
        this.producerInjectionPoints = BeanUtils.createInjectionPoints(producerMethod, declaringBean, beanManager);
        this.disposalMethod = findDisposalMethod();
        
        if (this.disposalMethod != null) {
            if (isOwbAndDependentScope) {
                throw new IllegalArgumentException("Due to a bug in OpenWebBeans the disposal method '" + disposalMethod.getJavaMember().getName() + "' of the class '"
                    + disposalMethod.getDeclaringType().getJavaClass().getName() + "' will never be called in conjunction with the ExcludeIfExists extension. "
                    + "Therefore the disposal method should be removed!");
            }
            this.disposalInjectionPoints = BeanUtils.createInjectionPoints(disposalMethod, declaringBean, beanManager);
        } else {
            this.disposalInjectionPoints = null;
        }
        
        this.beanManager = beanManager;
    }

    private AnnotatedMethod<?> findDisposalMethod() {
        for (AnnotatedMethod<?> m : producerMethod.getDeclaringType().getMethods()) {
            if (m == producerMethod) {
                continue;
            }
            
            for (AnnotatedParameter<?> p : m.getParameters()) {
                if (p.getAnnotation(Disposes.class) == null) {
                    continue;
                }
                
                // p is the disposes parameter
                if (p.getBaseType().equals(producerMethod.getBaseType())) {
                    return m;
                }
                
                // if we found a disposes parameter that doesn't match the producer type, we can skip this method
                break;
            }
        }
        
        return null;
    }

    @Override
    public Object create(Bean<Object> bean, CreationalContext<Object> creationalContext) {        
        CreationalContext<Object> parentCreationalContext = null;
        InjectableMethod m;
        try {
            parentCreationalContext = beanManager.createCreationalContext(declaringBean);
            Object parentInstance = beanManager.getReference(declaringBean, declaringBean.getBeanClass(), parentCreationalContext);
            m = new InjectableMethod(producerMethod, parentInstance, declaringBean, creationalContext, beanManager, producerInjectionPoints);
            
            return m.doInjection();
        } finally {
            if (parentCreationalContext != null) {
                parentCreationalContext.release();
            }
        }
    }

    @Override
    public void destroy(Bean<Object> bean, Object instance, CreationalContext<Object> creationalContext) {
        if (disposalMethod != null) {
            CreationalContext<Object> parentCreationalContext = null;
            try {
                parentCreationalContext = beanManager.createCreationalContext(declaringBean);
                Object parentInstance = beanManager.getReference(declaringBean, declaringBean.getBeanClass(), parentCreationalContext);
                InjectableMethod m = new InjectableMethod(disposalMethod, parentInstance, declaringBean, creationalContext, beanManager, disposalInjectionPoints);
                m.setDisposable(true);
                m.setProducerMethodInstance(instance);

                m.doInjection();
            } finally {
                if (parentCreationalContext != null) {
                    parentCreationalContext.release();
                }
            }
        }
    }
    
    public Set<InjectionPoint> getInjectionPoints() {
        return new HashSet<InjectionPoint>(producerInjectionPoints);
    }
    
    protected static List<InjectionPoint> createInjectionPoints(Producer<?> owner, Member member) {
        List<InjectionPoint> injectionPoints = new ArrayList<InjectionPoint>();
        for (InjectionPoint injectionPoint : owner.getInjectionPoints())
        {
            if (injectionPoint.getMember().equals(member))
            {
                injectionPoints.add(injectionPoint);
            }
        }
        return injectionPoints;
    }

}
