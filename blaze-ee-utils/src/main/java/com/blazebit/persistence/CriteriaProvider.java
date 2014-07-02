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

import com.blazebit.lang.StringUtils;
import com.blazebit.persistence.impl.CriteriaBuilderImpl;
import javax.persistence.EntityManager;

/**
 *
 * @author ccbem
 */
public class CriteriaProvider {
    public static <T> CriteriaBuilder<T> from(EntityManager em, Class<T> clazz) {
        return new CriteriaBuilderImpl<T>(em, clazz, StringUtils.firstToLower(clazz.getSimpleName()));
    }

    public static <T> CriteriaBuilder<T> from(EntityManager em, Class<T> clazz, String alias) {
        if (clazz == null || alias == null) {
            throw new NullPointerException();
        }
        return new CriteriaBuilderImpl<T>(em, clazz, alias);
    }
}
