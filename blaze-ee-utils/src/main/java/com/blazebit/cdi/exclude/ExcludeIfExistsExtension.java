package com.blazebit.cdi.exclude;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.util.Nonbinding;
import javax.inject.Named;

import org.apache.deltaspike.core.api.literal.AnyLiteral;
import org.apache.deltaspike.core.api.literal.DefaultLiteral;
import org.apache.deltaspike.core.util.bean.BeanBuilder;

import com.blazebit.apt.service.ServiceProvider;
import com.blazebit.cdi.exclude.annotation.ExcludeIfExists;
import com.blazebit.reflection.ReflectionUtils;
import java.lang.reflect.Field;

@ServiceProvider(Extension.class)
public class ExcludeIfExistsExtension implements Extension {

    private final Map<AnnotatedType<Object>, ExcludeIfExists> possibleIncludes = new HashMap<AnnotatedType<Object>, ExcludeIfExists>();
    private final Map<AnnotatedMember<Object>, ExcludeIfExists> possibleProducerIncludes = new HashMap<AnnotatedMember<Object>, ExcludeIfExists>();
    private final Map<Bean<?>, Annotated> beans = new HashMap<Bean<?>, Annotated>();
    private final Map<AnnotatedType<Object>, Bean<Object>> typeBeans = new HashMap<AnnotatedType<Object>, Bean<Object>>();

    protected void vetoBeans(@Observes ProcessAnnotatedType<?> processAnnotatedType) {
        ExcludeIfExists exclude = processAnnotatedType.getAnnotatedType().getAnnotation(ExcludeIfExists.class);
        if (exclude == null) {
            boolean veto = false;
            for (AnnotatedField<?> field : processAnnotatedType.getAnnotatedType().getFields()) {
                if (field.isAnnotationPresent(Produces.class)) {
                    exclude = field.getAnnotation(ExcludeIfExists.class);
                    if (exclude != null) {
                        veto = true;
                        possibleProducerIncludes.put((AnnotatedMember<Object>) field, exclude);
                    }
                }
            }

            for (AnnotatedMethod<?> method : processAnnotatedType.getAnnotatedType().getMethods()) {
                if (method.isAnnotationPresent(Produces.class)) {
                    exclude = method.getAnnotation(ExcludeIfExists.class);
                    if (exclude != null) {
                        veto = true;
                        possibleProducerIncludes.put((AnnotatedMember<Object>) method, exclude);
                    }
                }
            }

            if (veto) {
                processAnnotatedType.veto();
            }
        } else {
            possibleIncludes.put((AnnotatedType<Object>) processAnnotatedType.getAnnotatedType(), exclude);
            processAnnotatedType.veto();
        }
    }

    protected void registerBean(@Observes ProcessBean<?> processBean) {
        beans.put(processBean.getBean(), processBean.getAnnotated());
    }

    private boolean hasBean(Type[] types, Annotation[] qualifiers) {
        BEAN_OUTER: for (Map.Entry<Bean<?>, Annotated> entry : beans.entrySet()) {
            Bean<?> bean = entry.getKey();
            boolean found = false;

            for (Type t : types) {
                if (bean.getTypes().contains(t)) {
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                continue;
            }

            if (bean.getQualifiers().size() != qualifiers.length) {
                continue;
            }

            OUTER: for (Annotation beanQualifier : bean.getQualifiers()) {
                for (int i = 0; i < qualifiers.length; i++) {
                    if (areQualifiersEquivalent(beanQualifier, qualifiers[i])) {
                        continue OUTER;
                    }
                }

                // No qualifier matches the current bean qualifier
                continue BEAN_OUTER;
            }
            
            return true;
        }

        return false;
    }

    private boolean areQualifiersEquivalent(Annotation beanQualifier, Annotation annotation) {
        if (!beanQualifier.annotationType().equals(annotation.annotationType())) {
            return false;
        }

        try {
            for (Method m : beanQualifier.annotationType().getMethods()) {
                if ("equals".equals(m.getName()) && m.getReturnType().equals(boolean.class) && m.getParameterTypes().length == 1 && m.getParameterTypes()[0].equals(Object.class)) {
                    continue;
                }
                if ("hashCode".equals(m.getName()) && m.getReturnType().equals(int.class) && m.getParameterTypes().length == 0) {
                    continue;
                }
                if ("toString".equals(m.getName()) && m.getReturnType().equals(String.class) && m.getParameterTypes().length == 0) {
                    continue;
                }
                if ("annotationType".equals(m.getName()) && m.getReturnType().equals(Class.class) && m.getParameterTypes().length == 0) {
                    continue;
                }
                if (!m.isAnnotationPresent(Nonbinding.class)) {
                    Object o1 = m.invoke(beanQualifier);
                    Object o2 = m.invoke(annotation);

                    if (!o1.equals(o2)) {
                        return false;
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return true;
    }

    protected void vetoBeans(@Observes AfterBeanDiscovery afterBeanDiscovery, BeanManager beanManager) {
        boolean isOwbBug = isOwbBug(beanManager.getClass().getPackage());
        
        for (Map.Entry<AnnotatedType<Object>, ExcludeIfExists> entry : possibleIncludes.entrySet()) {
            AnnotatedType<Object> annotatedType = entry.getKey();
            Class<?>[] types = entry.getValue().value();
            Annotation[] qualifiers = getQualifiers(annotatedType, beanManager);

            if (hasBean(types, qualifiers)) {
                continue;
            }

            Bean<Object> bean = new BeanBuilder<Object>(beanManager).readFromType(annotatedType).create();
            afterBeanDiscovery.addBean(bean);
        }

        for (Map.Entry<AnnotatedMember<Object>, ExcludeIfExists> entry : possibleProducerIncludes.entrySet()) {
            AnnotatedMember<Object> annotatedMember = entry.getKey();
            Class<?>[] types = entry.getValue().value();
            Annotation[] qualifiers = getQualifiers(annotatedMember, beanManager);

            if (hasBean(types, qualifiers)) {
                continue;
            }

            Bean<Object> bean = typeBeans.get(annotatedMember.getDeclaringType());
            
            if (bean == null) {
                bean = new BeanBuilder<Object>(beanManager).readFromType(annotatedMember.getDeclaringType()).create();
                typeBeans.put(annotatedMember.getDeclaringType(), bean);
                afterBeanDiscovery.addBean(bean);
            }

            Bean<Object> producerBean = readFromMember(new BeanBuilder<Object>(beanManager), annotatedMember, bean, isOwbBug).create();
            afterBeanDiscovery.addBean(producerBean);
        }
    }

    private Annotation[] getQualifiers(Annotated annotatedElement, BeanManager beanManager) {
        Set<Annotation> annotations = new HashSet<Annotation>();
        for (Annotation annotation : annotatedElement.getAnnotations()) {
            if (beanManager.isQualifier(annotation.annotationType())) {
                annotations.add(annotation);
            }
        }

        if (annotations.isEmpty()) {
            annotations.add(new DefaultLiteral());
        }

        annotations.add(new AnyLiteral());
        return annotations.toArray(new Annotation[annotations.size()]);
    }

    private BeanBuilder<Object> readFromMember(BeanBuilder<Object> beanBuilder, AnnotatedMember<Object> annotatedMember, Bean<Object> bean, boolean isOwbBug) {
        // Init the qualifiers set
        beanBuilder.qualifiers();
        Set<Class<? extends Annotation>> stereotypes = new HashSet<Class<? extends Annotation>>();

        for (Annotation annotation : annotatedMember.getAnnotations()) {
            if (beanBuilder.getBeanManager().isQualifier(annotation.annotationType())) {
                beanBuilder.addQualifier(annotation);
            } else if (beanBuilder.getBeanManager().isScope(annotation.annotationType())) {
                beanBuilder.scope(annotation.annotationType());
            } else if (beanBuilder.getBeanManager().isStereotype(annotation.annotationType())) {
                stereotypes.add(annotation.annotationType());
            }

            if (annotation instanceof Named) {
                beanBuilder.name(((Named) annotation).value());
            }
            if (annotation instanceof Alternative) {
                beanBuilder.alternative(true);
            }
        }

        beanBuilder.stereotypes(stereotypes);

        if (beanBuilder.getScope() == null) {
            beanBuilder.scope(Dependent.class);
        }
        
        boolean isDependent = beanBuilder.getScope() == Dependent.class;
        
        Class<?> declaringClass = annotatedMember.getDeclaringType().getJavaClass();

        if (annotatedMember instanceof AnnotatedField<?>) {
            AnnotatedField<Object> annotatedField = (AnnotatedField<Object>) annotatedMember;
            Field field = annotatedField.getJavaMember();
            Class<?> rawType = ReflectionUtils.getResolvedFieldType(declaringClass, field);
            beanBuilder.beanClass(rawType);
            beanBuilder.beanLifecycle(new ProducerFieldCreationalContext(bean, annotatedField, beanBuilder.getBeanManager()));
        } else {
            AnnotatedMethod<Object> annotatedMethod = (AnnotatedMethod<Object>) annotatedMember;
            Method method = annotatedMethod.getJavaMember();
            Class<?> rawType = ReflectionUtils.getResolvedMethodReturnType(declaringClass, method);
            beanBuilder.beanClass(rawType);
            ProducerMethodCreationalContext ctx = new ProducerMethodCreationalContext(bean, annotatedMethod, beanBuilder.getBeanManager(), isDependent && isOwbBug);
            beanBuilder.beanLifecycle(ctx);
            beanBuilder.injectionPoints(ctx.getInjectionPoints());
        }

        beanBuilder.types(annotatedMember.getTypeClosure());
        
        if (beanBuilder.getQualifiers().isEmpty()) {
            beanBuilder.addQualifier(new DefaultLiteral());
        }
        beanBuilder.addQualifier(new AnyLiteral());
        return beanBuilder;
    }

    private boolean isOwbBug(Package owbPackage) {
        if (!owbPackage.getName().startsWith("org.apache.webbeans")) {
            return false;
        }
        String[] versionParts = owbPackage.getImplementationVersion().split("\\.");
        Integer minor = Integer.parseInt(versionParts[1]);
        Integer bugfix = Integer.parseInt(versionParts[2]);
        // TODO: update this as soon as there is a known version which fixes this
        return minor <= 2 && bugfix <= 6;
    }
}
