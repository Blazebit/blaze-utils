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
public class HavingTest {
    @Test
    public void testHaving(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.groupBy("d.owner").having("d.age").gt(0);
        
        assertEquals("FROM Document d GROUP BY d.owner HAVING d.age > :param_0", criteria.getQueryString());
    }
    
    @Test
    public void testHavingPropertyExpression(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.groupBy("d.owner").having("d.age + 1").gt(0);
        
        assertEquals("FROM Document d GROUP BY d.owner HAVING d.age + 1 > :param_0", criteria.getQueryString());
    }
    
    @Test
    public void testHavingPath(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.groupBy("d.owner").having("d.partners.age").gt(0);
        
        assertEquals("FROM Document d LEFT JOIN d.partners partners GROUP BY d.owner HAVING partners.age > :param_0", criteria.getQueryString());
    }
    
    @Test
    public void testHavingPathExpression(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.groupBy("d.owner").having("d.partners.age + 1").gt(0);
        
        assertEquals("FROM Document d LEFT JOIN d.partners partners GROUP BY d.owner HAVING partners.age > :param_0", criteria.getQueryString());
    }

    @Test
    public void testHavingAnd(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.groupBy("d.owner").having("d.partners.age").gt(0).having("d.locations.url").like("http://%");     
        
        assertEquals("FROM Document d LEFT JOIN d.partners partners LEFT JOIN d.locations locations GROUP BY d.owner HAVING partners.age > :param_0 AND locations.url LIKE :param_1", criteria.getQueryString());
    }
    
    @Test
    public void testHavingOr(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.groupBy("d.owner").havingOr().having("d.partners.age").gt(0).having("d.locations.url").like("http://%").endOr();   
        
        assertEquals("FROM Document d LEFT JOIN d.partners partners LEFT JOIN d.locations locations GROUP BY d.owner HAVING partners.age > :param_0 OR locations.url LIKE :param_1", criteria.getQueryString());
    }
    
    @Test
    public void testHavingOrAnd(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.groupBy("d.owner").havingOr().havingAnd().having("d.partners.age").gt(0).having("d.locations.url").like("http://%").endAnd().havingAnd().having("d.locations.age").lt(10).having("d.locations.url").like("ftp://%").endAnd().endOr();   
        
        assertEquals("FROM Document d LEFT JOIN d.partners partners LEFT JOIN d.locations locations GROUP BY d.owner HAVING (partners.age > :param_0 AND locations.url LIKE :param_1) OR (partners.age > :param_2 AND locations.url LIKE :param_3)", criteria.getQueryString());
    }
    
    @Test
    public void testHavingAndOr(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.groupBy("d.owner").havingOr().having("d.partners.age").gt(0).having("d.locations.url").like("http://%").endOr().havingOr().having("d.locations.age").lt(10).having("d.locations.url").like("ftp://%").endOr();   
        
        System.out.println(criteria.getQueryString());
        assertEquals("FROM Document d LEFT JOIN d.partners partners LEFT JOIN d.locations locations GROUP BY d.owner HAVING (partners.age > :param_0 OR locations.url LIKE :param_1) AND (partners.age > :param_2 OR locations.url LIKE :param_3)", criteria.getQueryString());
    }
    
    @Test
    public void testHavingOrSingleClause(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.groupBy("d.owner").havingOr().having("d.partners.age").gt(0).endOr();   
        
        assertEquals("FROM Document d LEFT JOIN d.partners partners GROUP BY  d.owner HAVING partners.age > :param_0", criteria.getQueryString());
    }
    
    @Test
    public void testHavingOrHavingAndSingleClause(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.groupBy("d.owner").havingOr().havingAnd().having("d.partners.age").gt(0).endAnd().endOr();   
        
        assertEquals("FROM Document d LEFT JOIN d.partners partners GROUP BY d.owner HAVING partners.age > :param_0", criteria.getQueryString());
    }
    
    @Test(expected = IllegalStateException.class)
    public void testHavingWithoutGroupBy(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.having("d.partners.age");   
    }
    
    @Test(expected = NullPointerException.class)
    public void testHavingNull(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.groupBy("d.owner").having(null);      
    }
}
