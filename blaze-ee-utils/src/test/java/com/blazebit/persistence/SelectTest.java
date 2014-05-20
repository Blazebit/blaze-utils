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
public class SelectTest {
    @Test
    public void testSelectSingle(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select("d.partners");
        
        assertEquals("SELECT d.partners FROM Document d", criteria.getQueryString());
    }
    
    @Test
    public void testSelectScalarExpression(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select("d.partners + 1");
        
        assertEquals("SELECT d.partners + 1 FROM Document d", criteria.getQueryString());
    }
    
    @Test
    public void testSelectMultiple(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select(new String[]{"d.partners", "d.children"});
        
        assertEquals("SELECT d.partners, d.children FROM Document d", criteria.getQueryString());
    }
    
    @Test
    public void testSelectAlias(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select("d.partners", "p").where("p").eq(2);
        
        assertEquals("SELECT d.partners AS p FROM Document d WHERE p = :param_0", criteria.getQueryString());
    }
    
    @Test
    public void testSelectAliasJoin(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select("d.partners", "p").where("p").eq(2);
        
        assertEquals("SELECT d.partners AS p FROM Document d WHERE p = :param_0", criteria.getQueryString());
    }
    
    @Test
    public void testSelectAliasJoin2(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select("d.children.name", "x").where("d.dwarfs.size").eq(2);
        
        System.out.println(criteria.getQueryString());
        assertEquals("SELECT children.name AS x FROM Document d LEFT JOIN d.children children LEFT JOIN d.dwarfs dwarfs WHERE dwarfs.size = :param_0", criteria.getQueryString());
    }
    
    @Test
    public void testSelectAliasJoin3(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select("d.children.name", "x").where("d.dwarfs.size").eq(2);
        
        System.out.println(criteria.getQueryString());
        assertEquals("SELECT children.name AS x FROM Document d LEFT JOIN d.children children LEFT JOIN children.name name WHERE x.size = :param_0", criteria.getQueryString());
        //TODO: how would the query actually look like? (x/name)
    }
        
    @Test(expected = IllegalArgumentException.class)
    public void testSelectSingleEmpty(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select("");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSelectMultipleEmpty(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select("", "");
    }
    
    @Test(expected = NullPointerException.class)
    public void testSelectNull(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select((String) null);
    }
    
    @Test(expected = NullPointerException.class)
    public void testSelectArrayNull(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select((String[]) null);
    }
}
