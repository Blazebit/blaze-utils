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
package com.blazebit.persistence;

/**
 *
 * @author cpbec
 */
public abstract class AbstractRestrictionBuilder<T> implements RestrictionBuilder<T> {
    
    private final String property;

    public AbstractRestrictionBuilder(String property) {
        this.property = property;
    }
    
    protected String getProperty() {
        return property;
    }
    
    protected abstract T getResult();

    @Override
    public T eq(Object value) {
        return getResult();
    }
    
    @Override
    public T isNull() {
        return getResult();
    }
    
    @Override
    public T like(Object value) {
        return getResult();
    }
    
}