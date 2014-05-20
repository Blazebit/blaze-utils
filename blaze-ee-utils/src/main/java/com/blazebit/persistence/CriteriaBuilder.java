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

import com.blazebit.persistence.impl.CriteriaBuilderImpl;
import com.blazebit.lang.StringUtils;
import java.lang.reflect.Constructor;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author cpbec
 */
public abstract class CriteriaBuilder<T> implements Aggregateable<RestrictionBuilder<CriteriaBuilder<T>>>, Filterable<RestrictionBuilder<CriteriaBuilder<T>>> {

    public static <T> CriteriaBuilder<T> from(Class<T> clazz) {
        return new CriteriaBuilderImpl<T>(clazz, StringUtils.firstToLower(clazz.getSimpleName()));
    }

    public static <T> CriteriaBuilder<T> from(Class<T> clazz, String alias) {
        if (clazz == null || alias == null) {
            throw new NullPointerException();
        }
        return new CriteriaBuilderImpl<T>(clazz, alias);
    }

    public abstract TypedQuery<T> getQuery(EntityManager em);

    public abstract String getQueryString();

    public abstract CriteriaBuilder<T> page(int page, int objectsPerPage);

    /*
     * Join methods
     */
    public abstract CriteriaBuilder<T> join(String path, String alias, JoinType type, boolean fetch);

    public abstract CriteriaBuilder<T> innerJoin(String path, String alias);

    public abstract CriteriaBuilder<T> innerJoinFetch(String path, String alias);

    public abstract CriteriaBuilder<T> leftJoin(String path, String alias);

    public abstract CriteriaBuilder<T> leftJoinFetch(String path, String alias);

    public abstract CriteriaBuilder<T> outerJoin(String path, String alias);

    public abstract CriteriaBuilder<T> outerJoinFetch(String path, String alias);

    public abstract CriteriaBuilder<T> rightJoin(String path, String alias);

    public abstract CriteriaBuilder<T> rightJoinFetch(String path, String alias);

    /*
     * Order by methods
     */
    public abstract CriteriaBuilder<T> orderBy(String expression, boolean ascending, boolean nullFirst);

    public abstract CriteriaBuilder<T> orderByAsc(String expression);

    public abstract CriteriaBuilder<T> orderByAsc(String expression, boolean nullFirst);

    public abstract CriteriaBuilder<T> orderByDesc(String expression);

    public abstract CriteriaBuilder<T> orderByDesc(String expression, boolean nullFirst);

    /*
     * Select methods
     */
    public abstract CriteriaBuilder<T> distinct();

    public abstract CriteriaBuilder<T> select(String... expressions);

    public abstract CriteriaBuilder<T> select(String expression);

    public abstract CriteriaBuilder<T> select(String expression, String alias);

    public abstract CriteriaBuilder<T> select(Class<? extends T> clazz);

    public abstract CriteriaBuilder<T> select(Constructor<? extends T> constructor);

    public abstract CriteriaBuilder<T> select(ObjectBuilder<? extends T> builder);

    public abstract <X> SelectObjectBuilder<X> selectNew(Class<X> clazz);

    public abstract SelectObjectBuilder<T> selectNew(Constructor<?> constructor);

    public abstract SelectObjectBuilder<T> selectNew(ObjectBuilder<? extends T> builder);

    /*
     * Where methods
     */
    @Override
    public abstract RestrictionBuilder<CriteriaBuilder<T>> where(String expression);

    public abstract WhereOrBuilder<CriteriaBuilder<T>> whereOr();

    /*
     * Group by methods
     */
    public abstract CriteriaBuilder<T> groupBy(String... expressions);

    public abstract CriteriaBuilder<T> groupBy(String expression);

    /*
     * Having methods
     */
    @Override
    public abstract RestrictionBuilder<CriteriaBuilder<T>> having(String expression);

    public abstract HavingOrBuilder<CriteriaBuilder<T>> havingOr();

}
