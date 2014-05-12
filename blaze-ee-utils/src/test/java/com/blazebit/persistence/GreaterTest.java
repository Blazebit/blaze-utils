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
public class GreaterTest {
    @Test
    public void testGt(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.where("d.age").gt(20);
        
        assertEquals("FROM Document d WHERE d.age > :param_0", criteria.getQueryString());
    }
    
    @Test(expected = NullPointerException.class)
    public void testGtNull(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.where("d.age").gt(null);        
    }
    
    @Test
    public void testGtExpression(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.where("d.age").gtExpression("d.owner.age");
        
        assertEquals("FROM Document d LEFT JOIN d.owner owner WHERE d.age > owner.age", criteria.getQueryString());
    }
    
    @Test(expected = NullPointerException.class)
    public void testGtExpressionNull(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.where("d.age").gtExpression(null);        
    }
    
    @Test
    public void testGe(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.where("d.age").ge(20);
        
        assertEquals("FROM Document d WHERE d.age >= :param_0", criteria.getQueryString());
    }
    
    @Test(expected = NullPointerException.class)
    public void testGeNull(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.where("d.age").ge(null);        
    }
    
    @Test
    public void testGeExpression(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.where("d.age").geExpression("d.owner.age");
        
        assertEquals("FROM Document d LEFT JOIN d.owner owner WHERE d.age >= owner.age", criteria.getQueryString());
    }
    
    @Test(expected = NullPointerException.class)
    public void testGeExpressionNull(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.where("d.age").geExpression(null);        
    }
}
