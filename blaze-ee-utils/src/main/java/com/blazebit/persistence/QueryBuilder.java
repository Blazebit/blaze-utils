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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

/**
 *
 * @author ccbem
 */
public interface QueryBuilder<T, X extends QueryBuilder<T, X>> extends Aggregateable<RestrictionBuilder<? extends X>>, Filterable<RestrictionBuilder<? extends X>> {
    
    public TypedQuery<T> getQuery(EntityManager em);

    public String getQueryString();
    
    public List<T> getResultList(EntityManager em);

    public PaginatedCriteriaBuilder<T> page(int page, int objectsPerPage);

    /*
     * Join methods
     */
    public X join(String path, String alias, JoinType type, boolean fetch);

    public X innerJoin(String path, String alias);

    public X innerJoinFetch(String path, String alias);

    public X leftJoin(String path, String alias);

    public X leftJoinFetch(String path, String alias);

    public X outerJoin(String path, String alias);

    public X outerJoinFetch(String path, String alias);

    public X rightJoin(String path, String alias);

    public X rightJoinFetch(String path, String alias);

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

    public <Y> SelectObjectBuilder<? extends QueryBuilder<Y, ?>> selectNew(Constructor<Y> constructor);

    public SelectObjectBuilder<? extends X> selectNew(ObjectBuilder<? extends T> builder);
    
    /*
     * Order by methods
     */
    public X orderBy(String expression, boolean ascending, boolean nullFirst);

    public X orderByAsc(String expression);

    public X orderByAsc(String expression, boolean nullFirst);

    public X orderByDesc(String expression);

    public X orderByDesc(String expression, boolean nullFirst);

    /*
     * Where methods
     */
    @Override
    public RestrictionBuilder<? extends X> where(String expression);

    public WhereOrBuilder<? extends X> whereOr();

    /*
     * Group by methods
     */
    public X groupBy(String... expressions);

    public X groupBy(String expression);

    /*
     * Having methods
     */
    @Override
    public RestrictionBuilder<? extends X> having(String expression);

    public HavingOrBuilder<? extends X> havingOr();
}
