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
public class EqTest {
    @Test
    public void testEqualTo(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.age").eq(20);
        
        assertEquals("FROM Document d WHERE d.age = :param_0", criteria.getQueryString());
    }
    
    @Test(expected = NullPointerException.class)
    public void testEqualToNull(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.age").eq(null);
    }
    
    @Test
    public void testEqualToExpression(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.age").eqExpression("d.age2 + 1");
        
        assertEquals("FROM Document d WHERE d.age = d.age2 + 1", criteria.getQueryString());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testEqualToEmptyExpression(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.age").eqExpression("");        
    }
    
    @Test(expected = NullPointerException.class)
    public void testEqualToNullExpression(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.age").eqExpression(null);        
    }
    
    // TODO: for subqueries
//    @Test
//    public void testEqualToAll(){
//        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
//        criteria.where("d.age").eq().all().expression("d.partners.age");
//        
//        assertEquals("FROM Document d LEFT JOIN d.partners partners WHERE d.age = ALL(partners.age)", criteria.getQueryString());
//    }
//    
//    @Test
//    public void testEqualToAny(){
//        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
//        criteria.where("d.age").eq().any().expression("d.partners.age");
//        
//        assertEquals("FROM Document d LEFT JOIN d.partners partners WHERE d.age = ANY(partners.age)", criteria.getQueryString());
//    }
    
    @Test
    public void testNotEqualTo(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.age").notEq(20);
        
        assertEquals("FROM Document d WHERE NOT d.age = :param_0", criteria.getQueryString());
    }
    
     @Test(expected = NullPointerException.class)
    public void testNotEqualToNull(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.age").notEq(null);
    }
    
    @Test
    public void testNotEqualToExpression(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.age").notEqExpression("d.age2 + 1");
        
        assertEquals("FROM Document d WHERE NOT d.age = d.age2 + 1", criteria.getQueryString());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNotEqualToEmptyExpression(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.age").notEqExpression("");        
    }
    
    @Test(expected = NullPointerException.class)
    public void testNotEqualToNullExpression(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.age").notEqExpression(null);        
    }
    
    // TODO: for subqueries
//    @Test
//    public void testNotEqualToAll(){
//        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
//        criteria.where("d.age").notEq().all().expression("d.partners.age");
//        
//        assertEquals("FROM Document d LEFT JOIN d.partners partners WHERE d.age != ALL(partners.age)", criteria.getQueryString());
//    }
//    
//    @Test
//    public void testNotEqualToAny(){
//        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
//        criteria.where("d.age").notEq().any().expression("d.partners.age");
//        
//        assertEquals("FROM Document d LEFT JOIN d.partners partners WHERE d.age != ANY(partners.age)", criteria.getQueryString());
//    }
}
