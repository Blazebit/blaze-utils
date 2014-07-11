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
import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.metamodel.MappingConstructor;
import com.blazebit.persistence.view.metamodel.MethodAttribute;
import com.blazebit.persistence.view.metamodel.ViewType;
import com.blazebit.reflection.ReflectionUtils;
import edu.emory.mathcs.backport.java.util.Collections;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author cpbec
 */
public class ViewTypeImpl<X> implements ViewType<X> {

    private final Class<X> javaType;
    private final String name;
    private final Class<?> entityClass;
    private final Map<String, MethodAttribute<? super X, ?>> attributes;
    private final Set<MappingConstructor<X>> constructors;
    
    public ViewTypeImpl(Class<? extends X> clazz) {
        this.javaType = (Class<X>) clazz;
        
        EntityView entityViewAnnot = AnnotationUtils.findAnnotation(clazz, EntityView.class);
        
        if (entityViewAnnot == null) {
            throw new IllegalArgumentException("Could not find any EntityView annotation for the class '" + clazz.getName() + "'");
        }
        
        if (entityViewAnnot.name().isEmpty()) {
            this.name = clazz.getSimpleName();
        } else {
            this.name = entityViewAnnot.name();
        }
        
        this.entityClass = entityViewAnnot.value();
        this.attributes = new HashMap<String, MethodAttribute<? super X, ?>>();
        
        for (Class<?> type : ReflectionUtils.getSuperTypes(clazz)) {
            for (Method method : type.getDeclaredMethods()) {
                MethodAttribute<? super X, ?> attribute = MethodAttributeImpl.createMappingAttribute(this, method);
                if (attribute != null) {
                    attributes.put(attribute.getName(), attribute);
                }
            }
        }
        
        Set<MappingConstructor<X>> constructors = new HashSet<MappingConstructor<X>>();
        
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            constructors.add(new MappingConstructorImpl<X>(this, (Constructor<X>) constructor));
        }
        
        this.constructors = Collections.unmodifiableSet(constructors);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<X> getJavaType() {
        return javaType;
    }

    @Override
    public Class<?> getEntityClass() {
        return entityClass;
    }
    
    @Override
    public Set<MethodAttribute<? super X, ?>> getAttributes() {
        return new HashSet<MethodAttribute<? super X, ?>>(attributes.values());
    }

    @Override
    public MethodAttribute<? super X, ?> getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Set<MappingConstructor<X>> getConstructors() {
        return constructors;
    }
    
}
