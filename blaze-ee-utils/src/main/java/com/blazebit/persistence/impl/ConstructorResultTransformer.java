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

package com.blazebit.persistence.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.transform.ResultTransformer;

/**
 *
 * @author ccbem
 */
public class ConstructorResultTransformer implements ResultTransformer {
    private Constructor<?> constructor;

    public ConstructorResultTransformer(Constructor<?> constructor) {
        this.constructor = constructor;
    }
    
    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        if(constructor.getParameterTypes().length != tuple.length){
            throw new RuntimeException("Constructor expects " + constructor.getParameterTypes().length + " arguments but " + tuple.length + " arguments were queried");
        }
        try {
            return constructor.newInstance(tuple);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List transformList(List collection) {
        return collection;
    }
    
}
