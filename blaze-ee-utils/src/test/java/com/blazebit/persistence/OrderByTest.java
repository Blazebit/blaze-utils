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

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author ccbem
 */
public class OrderByTest {
    @Test
    public void testOrderByAscNullsFirst(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.orderBy("d.age", true, true);
        assertEquals("FROM Document d ORDER BY d.age ASC NULLS FIRST", criteria.getQueryString());
    }
    
    @Test
    public void testOrderByAscNullsLast(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.orderBy("d.age", true, false);
        assertEquals("FROM Document d ORDER BY d.age ASC NULLS LAST", criteria.getQueryString());
    }
    
    @Test
    public void testOrderByDescNullsFirst(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.orderBy("d.age", false, true);
        assertEquals("FROM Document d ORDER BY d.age DESC NULLS FIRST", criteria.getQueryString());
    }
    
    @Test
    public void testOrderByDescNullsLast(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.orderBy("d.age", false, false);
        assertEquals("FROM Document d ORDER BY d.age DESC NULLS LAST", criteria.getQueryString());
    }
    
    @Test
    public void testOrderByNested(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.orderBy("d.employees.contacts.age", false, false);
        assertEquals("FROM Document d LEFT JOIN d.employees employees LEFT JOIN employees.contacts contacts ORDER BY contacts.age DESC NULLS LAST", criteria.getQueryString());
    }
    
    @Test
    public void testOrderByMultiple(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.orderBy("d.employees.contacts.age", false, false).orderBy("d.employees.supervisors.joinDate", true, true);

        assertEquals("FROM Document d LEFT JOIN d.employees employees LEFT JOIN employees.contacts contacts LEFT JOIN employees.supervisors supervisors ORDER BY contacts.age DESC NULLS LAST, supervisors.joinDate ASC NULLS FIRST", criteria.getQueryString());
    }
    
    @Test(expected = NullPointerException.class)
    public void testOrderByNullAlias(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.orderBy(null, false, false);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testOrderByEmptyAlias(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.orderBy("", false, false);
    }
}
