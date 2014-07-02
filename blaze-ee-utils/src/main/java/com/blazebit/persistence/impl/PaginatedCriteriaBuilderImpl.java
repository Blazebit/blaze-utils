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

import com.blazebit.persistence.PagedList;
import com.blazebit.persistence.PaginatedCriteriaBuilder;
import com.blazebit.persistence.QueryBuilder;
import com.blazebit.persistence.SelectObjectBuilder;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

/**
 *
 * @author ccbem
 */
public class PaginatedCriteriaBuilderImpl<T> extends AbstractCriteriaBuilder<T, PaginatedCriteriaBuilder<T>> implements PaginatedCriteriaBuilder<T> {
    private final int page;
    private final int objectsPerPage;
    
    public PaginatedCriteriaBuilderImpl(AbstractCriteriaBuilder<T, ? extends QueryBuilder<T, ?>> baseBuilder, int page, int objectsPerPage) {
        super(baseBuilder);
        this.page = page;
        this.objectsPerPage = objectsPerPage;
//        this.parameters = baseBuilder.parameters;
//        super.paramNameGenerator = baseBuilder.paramNameGenerator;
    }

    @Override
    public PagedList<T> getResultList(EntityManager em) {
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCountQueryString() {
        StringBuilder countQuery = new StringBuilder();
        
        applyImplicitJoins();
        
        countQuery.append("SELECT COUNT(*) ");
        countQuery.append("FROM ").append(clazz.getSimpleName()).append(' ').append(joinManager.getRootAlias());
        countQuery.append(joinManager.buildJoins(false));
        countQuery.append(whereManager.buildClause());
        countQuery.append(groupByManager.buildGroupBy());        
        countQuery.append(havingManager.buildClause());
//        applyWhere(queryGenerator, sb);
//        applyGroupBys(queryGenerator, sb, groupByInfos);
//        applyHavings(queryGenerator, sb);
        return countQuery.toString();
    }

    @Override
    public String getIdQueryString() {
        StringBuilder idQuery = new StringBuilder();
        Metamodel m = em.getMetamodel();
        EntityType<T> entityType = m.entity(clazz);
        String idName = entityType.getId(entityType.getIdType().getJavaType()).getName();
        
        idQuery.append("SELECT ").append(idName);
        idQuery.append(" FROM ").append(clazz.getSimpleName()).append(' ').append(joinManager.getRootAlias());
        idQuery.append(joinManager.buildJoins(false));
        idQuery.append(whereManager.buildClause());
        idQuery.append(groupByManager.buildGroupBy());        
        idQuery.append(havingManager.buildClause());
        idQuery.append(orderByManager.buildOrderBy());
        
        return idQuery.toString();
    }

    @Override
    public <Y> SelectObjectBuilder<PaginatedCriteriaBuilder<Y>> selectNew(Class<Y> clazz) {
        return (SelectObjectBuilder<PaginatedCriteriaBuilder<Y>>) super.selectNew(clazz);
    }
   
}
