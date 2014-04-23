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

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cpbec
 */
public class CriteriaBuilderTest {
    
    @Test
    public void testJoin1() {
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class);
        criteria.join("owner", "owner", JoinType.INNER, false);
        criteria.join("owner.test", "test", JoinType.INNER, false);
        criteria.join("owner.test.bla", "bla", JoinType.INNER, false);
        criteria.join("owner.test.bla", "bla", JoinType.LEFT, true);
        System.out.println(criteria.getQueryString());
    }
    
    @Test
    public void testJoin2() {
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class);
        criteria.join("owner", "owner", JoinType.INNER, false);
        criteria.join("owner.test", "test", JoinType.INNER, false);
        criteria.join("test.bla", "bla", JoinType.RIGHT, false);
        System.out.println(criteria.getQueryString());
    }
    
    @Test
    public void testJoin3() {
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class);
        criteria.join("document.owner", "owner", JoinType.INNER, false);
        criteria.join("document.owner.test", "test", JoinType.INNER, false);
        criteria.join("document.owner.test.bla", "bla", JoinType.OUTER, false);
        System.out.println(criteria.getQueryString());
    }
    
    @Test
    public void testJoin4() {
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class);
        criteria.join("document.owner", "owner123", JoinType.INNER, false);
        criteria.join("document.owner.test", "test123", JoinType.INNER, false);
        criteria.join("document.owner.test.bla", "bla123", JoinType.INNER, false);
        System.out.println(criteria.getQueryString());
    }
    
    @Test
    public void testJoin5() {
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class);
        criteria.join("owner", "owner123", JoinType.INNER, false);
        criteria.join("owner.test", "test123", JoinType.INNER, false);
        criteria.join("owner.test.bla", "bla123", JoinType.INNER, false);
        System.out.println(criteria.getQueryString());
    }
    
    @Test
    public void testOrderBy1() {
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class);
        criteria.orderBy("name", true, true);
        System.out.println(criteria.getQueryString());
    }
    @Test
    public void testOrderBy2() {
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class);
        criteria.orderBy("owner.test", true, true);
        System.out.println(criteria.getQueryString());
    }
    @Test
    public void testOrderBy3() {
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class);
        criteria.join("owner", "owner123", JoinType.INNER, false);
        criteria.orderBy("owner123.test", true, true);
        System.out.println(criteria.getQueryString());
    }
    @Test
    public void testOrderBy4() {
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class);
        criteria.join("owner", "owner123", JoinType.INNER, false);
        criteria.join("owner.test", "test123", JoinType.INNER, false);
        criteria.orderBy("test123.bla", true, true);
        System.out.println(criteria.getQueryString());
    }
    @Test
    public void testOrderBy5() {
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class);
        criteria.join("owner", "owner123", JoinType.INNER, false);
        criteria.join("owner.test", "test123", JoinType.INNER, false);
        criteria.orderBy("owner.test.bla", true, true);
        System.out.println(criteria.getQueryString());
    }
    @Test
    public void testOrderBy6() {
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class);
        criteria.orderBy("owner.test.bla", true, true);
        System.out.println(criteria.getQueryString());
    }
    @Test
    public void testWhere1() {
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class);
        /*
         WHERE
          (owner.id = 1 OR owner.id IS NULL)
         AND
          name LIKE 'Test'
        */
//        criteria.where(
//            cb.or(
//                cb.eq("owner.id", 1),
//                cb.isNull("owner.id")
//            ),
//            cb.like("name", "Test"),
//            cb.exists
//        );
        
        criteria
            .whereOr()
                .where("owner.id").equalTo(1)
                .whereAnd()
                    .where("owner.id").isNull()
                    .where("owner.isGlobal").equalTo(true)
                .endAnd()
            .endOr()
            .where("name").like("Test");
//            .where("owner").in(CriteriaBuilder.from(CompanyCategoryEntry.class)
//                .where("owner.active").eq(true)
//            )
        
//        criteria.where("(owner.id = :id OR owner.id IS NULL) AND name LIKE :name")
            
        System.out.println(criteria.getQueryString());
    }
    
    public static class Document {
        
    }
}
