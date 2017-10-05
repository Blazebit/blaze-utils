package com.blazebit.cdi.exclude;

import org.apache.deltaspike.core.util.metadata.builder.ContextualLifecycle;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.reflect.Field;


public class ProducerFieldCreationalContext implements ContextualLifecycle<Object> {

    private final Bean<Object> declaringBean;
    private final Field field;
    private final BeanManager beanManager;

    public ProducerFieldCreationalContext(Bean<Object> declaringBean, AnnotatedField<?> field, BeanManager beanManager) {
        this.declaringBean = declaringBean;
        this.field = field.getJavaMember();
        this.beanManager = beanManager;
        this.field.setAccessible(true);
    }

    @Override
    public Object create(Bean<Object> bean, CreationalContext<Object> creationalContext) {
        CreationalContext<?> cc = beanManager.createCreationalContext(null);
        Object o = beanManager.getReference(declaringBean, declaringBean.getBeanClass(), cc);

        try {
            return field.get(o);
        } catch (Exception e) {
            throw new RuntimeException("Could not retrieve the value from the producer field", e);
        }
    }

    @Override
    public void destroy(Bean<Object> bean, Object instance, CreationalContext<Object> creationalContext) {
    }

}
