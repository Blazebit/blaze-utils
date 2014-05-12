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
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author cpbec
 */
public interface CriteriaBuilder<T> extends Aggregateable<RestrictionBuilder<CriteriaBuilder<T>>>, Filterable<RestrictionBuilder<CriteriaBuilder<T>>> {

    public TypedQuery<T> getQuery(EntityManager em);

    public String getQueryString();
    
//    public CriteriaBuilder<T> page(int page, int objectsPerPage);

    /*
     * Join methods
     */
    public CriteriaBuilder<T> join(String path, String alias, JoinType type, boolean fetch);
    
    public CriteriaBuilder<T> innerJoin(String path, String alias);

    public CriteriaBuilder<T> innerJoinFetch(String path, String alias);

    public CriteriaBuilder<T> leftJoin(String path, String alias);

    public CriteriaBuilder<T> leftJoinFetch(String path, String alias);

    public CriteriaBuilder<T> outerJoin(String path, String alias);

    public CriteriaBuilder<T> outerJoinFetch(String path, String alias);

    public CriteriaBuilder<T> rightJoin(String path, String alias);

    public CriteriaBuilder<T> rightJoinFetch(String path, String alias);

    /*
     * Order by methods
     */

    public CriteriaBuilder<T> orderBy(String expression, boolean ascending, boolean nullFirst);

    public CriteriaBuilder<T> orderByAsc(String expression);

    public CriteriaBuilder<T> orderByAsc(String expression, boolean nullFirst);
    
    public CriteriaBuilder<T> orderByDesc(String expression);

    public CriteriaBuilder<T> orderByDesc(String expression, boolean nullFirst);

    /*
     * Select methods
     */
    public CriteriaBuilder<T> distinct();
    
    public CriteriaBuilder<T> select(String... expressions);

    public CriteriaBuilder<T> select(String expression);

    public CriteriaBuilder<T> select(String expression, String alias);
    
    public CriteriaBuilder<T> select(Class<? extends T> clazz);
    
    public CriteriaBuilder<T> select(Constructor<? extends T> constructor);
    
    public CriteriaBuilder<T> select(ObjectBuilder<? extends T> builder);
    
    public SelectObjectBuilder<CriteriaBuilder<T>> selectNew(Class<? extends T> clazz);
    
    public SelectObjectBuilder<CriteriaBuilder<T>> selectNew(Constructor<? extends T> constructor);
    
    public SelectObjectBuilder<CriteriaBuilder<T>> selectNew(ObjectBuilder<? extends T> builder);

    /*
     * Where methods
     */
    @Override
    public RestrictionBuilder<CriteriaBuilder<T>> where(String expression);

    public WhereOrBuilder<CriteriaBuilder<T>> whereOr();

    /*
     * Group by methods
     */
    public CriteriaBuilder<T> groupBy(String... expressions);

    public CriteriaBuilder<T> groupBy(String expression);

    /*
     * Having methods
     */
    @Override
    public RestrictionBuilder<CriteriaBuilder<T>> having(String expression);

    public HavingOrBuilder<CriteriaBuilder<T>> havingOr();
    
}
