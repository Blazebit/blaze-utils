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

import com.blazebit.persistence.entity.Document;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author ccbem
 */
public class WhereTest {
    
    @Test
    public void testWhereProperty(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.age").ge(25);

        assertEquals("FROM Document d WHERE d.age >= :param_0", criteria.getQueryString());
    }
    
    @Test
    public void testWherePropertyExpression(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.age + 1").ge(25);

        assertEquals("FROM Document d WHERE d.age+1 >= :param_0", criteria.getQueryString());
    }
    
    
    
    @Test
    public void testWherePath(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.partners.age").gt(0);
        
        assertEquals("FROM Document d LEFT JOIN d.partners partners WHERE partners.age > :param_0", criteria.getQueryString());
    }
    
    @Test
    public void testWherePathExpression(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.owner.partners.age + 1").ge(25);

        assertEquals("FROM Document d LEFT JOIN d.owner owner LEFT JOIN owner.partners partners WHERE partners.age+1 >= :param_0", criteria.getQueryString());
    }

    @Test
    public void testWhereAnd(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.partners.age").gt(0).where("d.versions.url").like("http://%");     
        
        assertEquals("FROM Document d LEFT JOIN d.partners partners LEFT JOIN d.versions versions WHERE partners.age > :param_0 AND versions.url LIKE :param_1", criteria.getQueryString());
    }
    
    @Test
    public void testWhereOr(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.whereOr().where("d.partners.age").gt(0).where("d.versions.url").like("http://%").endOr();   
        
        assertEquals("FROM Document d LEFT JOIN d.partners partners LEFT JOIN d.versions versions WHERE partners.age > :param_0 OR versions.url LIKE :param_1", criteria.getQueryString());
    }
    
    @Test
    public void testWhereOrAnd(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.whereOr()
                    .whereAnd()
                        .where("d.partners.age").gt(0)
                        .where("d.versions.url").like("http://%")
                    .endAnd()
                    .whereAnd()
                        .where("d.versions.age").lt(10)
                        .where("d.versions.url").like("ftp://%")
                    .endAnd()
                .endOr();   
        
        assertEquals("FROM Document d LEFT JOIN d.partners partners LEFT JOIN d.versions versions WHERE (partners.age > :param_0 AND versions.url LIKE :param_1) OR (versions.age < :param_2 AND versions.url LIKE :param_3)", criteria.getQueryString());
    }
    
    @Test
    public void testWhereAndOr(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.whereOr()
                    .where("d.partners.age").gt(0)
                    .where("d.versions.url").like("http://%")
                .endOr()
                .whereOr()
                    .where("d.versions.age").lt(10)
                    .where("d.versions.url").like("ftp://%")
                .endOr();   
        
        assertEquals("FROM Document d LEFT JOIN d.partners partners LEFT JOIN d.versions versions WHERE (partners.age > :param_0 OR versions.url LIKE :param_1) AND (versions.age < :param_2 OR versions.url LIKE :param_3)", criteria.getQueryString());
    }
    
    @Test
    public void testWhereOrSingleClause(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.whereOr().where("d.partners.age").gt(0).endOr();   
        
        assertEquals("FROM Document d LEFT JOIN d.partners partners WHERE partners.age > :param_0", criteria.getQueryString());
    }
    
    @Test
    public void testWhereOrWhereAndSingleClause(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.whereOr().whereAnd().where("d.versions.age").gt(0).endAnd().endOr();   
        
        assertEquals("FROM Document d LEFT JOIN d.versions versions WHERE versions.age > :param_0", criteria.getQueryString());
    }
    
    @Test(expected = NullPointerException.class)
    public void testWhereNull(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWhereEmpty(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("").gt(0);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testWhereNotClosed(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.age");
        criteria.where("d.owner");
    }
    
    @Test(expected = IllegalStateException.class)
    public void testWhereOrNotClosed(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.whereOr().where("d.partners.age").gt(0);        
        criteria.where("d.partners.age");
    }
    
    @Test(expected = IllegalStateException.class)
    public void testWhereAndNotClosed(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.whereOr().whereAnd().where("d.partners.age").gt(0);
        criteria.where("d.partners.age");
    }
}
