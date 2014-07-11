/*
 * Copyright 2014 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blazebit.persistence.view.impl.metamodel;

import com.blazebit.annotation.AnnotationUtils;
import com.blazebit.lang.StringUtils;
import com.blazebit.persistence.view.Mapping;
import com.blazebit.persistence.view.metamodel.MethodAttribute;
import com.blazebit.persistence.view.metamodel.ViewType;
import com.blazebit.reflection.ReflectionUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 * @author cpbec
 */
public class MethodAttributeImpl<X, Y> implements MethodAttribute<X, Y> {
    
    private final String name;
    private final ViewType<X> declaringType;
    private final Method javaMethod;
    private final Class<Y> javaType;
    private final String mapping;

    private MethodAttributeImpl(ViewType<X> viewType, Method method, Mapping mapping) {
        this.name = StringUtils.firstToLower(method.getName().substring(3));
        this.declaringType = viewType;
        this.javaMethod = method;
        this.javaType = (Class<Y>) ReflectionUtils.getResolvedMethodReturnType(viewType.getJavaType(), method);
        this.mapping = mapping.value();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ViewType<X> getDeclaringType() {
        return declaringType;
    }

    @Override
    public Method getJavaMethod() {
        return javaMethod;
    }

    @Override
    public Class<Y> getJavaType() {
        return javaType;
    }

    @Override
    public String getMapping() {
        return mapping;
    }
    
    public static <X> MethodAttribute<? super X, ?> createMappingAttribute(ViewType<X> viewType, Method method) {
        Mapping mapping = getMapping(viewType, method);
        if (mapping == null) {
            return null;
        }
        
        return new MethodAttributeImpl<X, Object>(viewType, method, mapping);
    }
    
    private static Mapping getMapping(ViewType<?> viewType, Method m) {
        // We only support bean style getters
        if (!m.getName().startsWith("get") || m.getParameterTypes().length != 0 || !Modifier.isAbstract(m.getModifiers())) {
            return null;
        }
        
        Class<?> entityClass = viewType.getEntityClass();
        Mapping mapping = AnnotationUtils.findAnnotation(m, Mapping.class);
        
        if (mapping == null) {
            // Implicit mapping
            String attributeName = StringUtils.firstToLower(m.getName().substring(3));
            
            // First check if a the same method exists in the entity class
            boolean entityAttributeExists = ReflectionUtils.getMethod(entityClass, m.getName()) != null;
            // If not, check if a field with the given attribute name exists in the entity class
            entityAttributeExists = entityAttributeExists || ReflectionUtils.getField(entityClass, attributeName) != null;
            
            if (!entityAttributeExists) {
                throw new IllegalArgumentException("The entity class '" + entityClass.getName() + "' has no attribute '" + attributeName + "' that was implicitly mapped in entity view '" + viewType.getName() + "' in class '" + m.getDeclaringClass().getName() + "'");
            }
            
            mapping = new MappingLiteral(attributeName);
        }
        
        return mapping;
    }
}
