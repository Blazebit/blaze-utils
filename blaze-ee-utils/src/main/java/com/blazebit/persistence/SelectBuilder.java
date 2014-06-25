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

import java.lang.reflect.Constructor;

/**
 *
 * @author ccbem
 */
public interface SelectBuilder<T, X extends QueryBuilder<T, X>> {
    /*
     * Select methods
     */
    public X distinct();
    
    public CaseWhenBuilder<? extends X> selectCase();

    /* CASE caseOperand (WHEN scalarExpression THEN scalarExpression)+ ELSE scalarExpression END */
    public SimpleCaseWhenBuilder<? extends X> selectCase(String expression);

    public X select(String... expressions);

    public X select(String expression);

    public X select(String expression, String alias);

    public X select(Class<? extends T> clazz);

    public X select(Constructor<? extends T> constructor);

    public X select(ObjectBuilder<? extends T> builder);

    public <Y> SelectObjectBuilder<? extends QueryBuilder<Y, ?>> selectNew(Class<Y> clazz);

    public SelectObjectBuilder<? extends X> selectNew(Constructor<?> constructor);

    public SelectObjectBuilder<? extends X> selectNew(ObjectBuilder<? extends T> builder);
}
