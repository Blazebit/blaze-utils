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
package com.blazebit.persistence.impl.objectbuilder;

import com.blazebit.persistence.ObjectBuilder;
import java.lang.reflect.Constructor;
import java.util.List;
import org.hibernate.transform.ResultTransformer;

/**
 *
 * @author ccbem
 */
public class ClassResultTransformer<T> implements ObjectBuilder<T> {

    private final Class<T> clazz;

    public ClassResultTransformer(Class<T> clazz) {
        this.clazz = clazz;
    }

    // TODO: implement collection awareness
    @Override
    public T build(Object[] tuple, String[] aliases) {
        Constructor<?>[] constructors = clazz.getConstructors();
        Constructor<T> matchingConstr = null;
        for (Constructor<?> constr : constructors) {
            Class<?>[] paramTypes = constr.getParameterTypes();
            if (paramTypes.length == tuple.length) {
                boolean match = true;
                for (int i = 0; i < paramTypes.length; i++) {
                    if (tuple[i] != null && !paramTypes[i].isAssignableFrom(tuple[i].getClass())) {
                        match = false;
                        break;
                    }
                }
                if (match == true) {
                    if (matchingConstr != null) {
                        throw new RuntimeException("Multiple constructors matching");
                    }
                    matchingConstr = (Constructor<T>) constr;
                }

            }
        }
        if (matchingConstr == null) {
            throw new RuntimeException("No matching constructor");
        }
        try {
            return matchingConstr.newInstance(tuple);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<T> buildList(List<T> list) {
        return list;
    }

}
