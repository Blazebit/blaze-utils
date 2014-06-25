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

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.PagedList;
import com.blazebit.persistence.PaginatedCriteriaBuilder;
import com.blazebit.persistence.QueryBuilder;
import com.blazebit.persistence.SelectObjectBuilder;
import javax.persistence.EntityManager;

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
        
        countQuery.append("SELECT COUNT(*)");
        countQuery.append("FROM ").append(clazz.getSimpleName()).append(' ').append(rootAliasInfo.getAlias());
        applyJoins(countQuery, rootAliasInfo, rootNode.getNodes());
//        applyWhere(queryGenerator, sb);
//        applyGroupBys(queryGenerator, sb, groupByInfos);
//        applyHavings(queryGenerator, sb);
        return countQuery.toString();
    }

    @Override
    public String getIdQueryString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <Y> SelectObjectBuilder<PaginatedCriteriaBuilder<Y>> selectNew(Class<Y> clazz) {
        return (SelectObjectBuilder<PaginatedCriteriaBuilder<Y>>) super.selectNew(clazz);
    }
    
}
