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
public class SelectTest {
    @Test
    public void testSelectNonJoinable(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select("nonJoinable");
        
        assertEquals("SELECT d.nonJoinable FROM Document d", criteria.getQueryString());
    }
    
    @Test
    public void testSelectNonJoinablePrefixed(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select("d.nonJoinable");
        
        assertEquals("SELECT d.nonJoinable FROM Document d", criteria.getQueryString());
    }
    
    @Test
    public void testSelectJoinable(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select("partners");
        
        assertEquals("SELECT partners FROM Document d LEFT JOIN d.partners partners", criteria.getQueryString());
    }
    
    @Test
    public void testSelectJoinablePrefixed(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select("d.partners");
        
        assertEquals("SELECT partners FROM Document d LEFT JOIN d.partners partners", criteria.getQueryString());
    }
    
    @Test
    public void testSelectScalarExpression(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select("d.partners + 1");
        
        assertEquals("SELECT partners+1 FROM Document d LEFT JOIN d.partners partners", criteria.getQueryString());
    }
    
    @Test
    public void testSelectMultiple(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select(new String[]{"d.partners", "d.versions"});
        
        assertEquals("SELECT partners, versions FROM Document d LEFT JOIN d.partners partners LEFT JOIN d.versions versions", criteria.getQueryString());
    }
    
    @Test
    public void testSelectAlias(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select("d.partners", "p").where("p").eq(2);
        
        assertEquals("SELECT partners AS p FROM Document d LEFT JOIN d.partners partners WHERE p = :param_0", criteria.getQueryString());
    }
    
    @Test
    public void testSelectAliasReplacement(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select("d.partners", "p").where("partners").eq(2);
        
        assertEquals("SELECT partners AS p FROM Document d LEFT JOIN d.partners partners WHERE p = :param_0", criteria.getQueryString());
    }
    
    @Test
    public void testSelectAliasJoin(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select("d.partners", "p").where("p").eq(2);
        
        assertEquals("SELECT partners AS p FROM Document d LEFT JOIN d.partners partners WHERE p = :param_0", criteria.getQueryString());
    }
    
    @Test
    public void testSelectAliasJoin2(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select("d.versions.name", "x").where("d.partners.size").eq(2);
        
        System.out.println(criteria.getQueryString());
        assertEquals("SELECT versions.name AS x FROM Document d LEFT JOIN d.partners partners LEFT JOIN d.versions versions WHERE partners.size = :param_0", criteria.getQueryString());
    }
    
    @Test
    public void testSelectAliasJoin3(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "a");
        criteria.select("a").select("C.x").innerJoin("a.b", "B").innerJoin("B.c", "C");
        
        System.out.println(criteria.getQueryString());
        assertEquals("SELECT a, C.x FROM Document a JOIN a.b B JOIN B.c C", criteria.getQueryString());
    }
    
    @Test
    public void testSelectAliasJoin4(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "a");
        criteria.select("a").select("C.x", "X").innerJoin("a.b", "B").innerJoin("B.c", "C").where("X").eqExpression("B.z");
        
        System.out.println(criteria.getQueryString());
        assertEquals("SELECT a, C.x AS X FROM Document a JOIN a.b B JOIN B.c C WHERE X = B.z", criteria.getQueryString());
    }
    
    @Test
    public void testSelectAliasJoin5(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.select("d.partners.field");
        
        System.out.println(criteria.getQueryString());
        assertEquals("SELECT partners.field FROM Document d LEFT JOIN d.partners partners", criteria.getQueryString());
    }
    
    @Test
    public void testSelectAliasJoin6(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "a");
        criteria.select("a.versions");
        
        System.out.println(criteria.getQueryString());
        assertEquals("SELECT versions FROM Document a LEFT JOIN a.versions versions", criteria.getQueryString());
    }
    
    @Test
    public void testSelectAliasJoin7(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "a");
        
        // we have already solved this for join aliases so we should also solve it here
        criteria.select("test.field", "fieldAlias").where("test.field").eq("bla").join("blub", "test", JoinType.LEFT, false);
        
        System.out.println(criteria.getQueryString());
        assertEquals("SELECT test.field AS fieldAlias FROM Document a LEFT JOIN a.blub test WHERE fieldAlias = :param_0", criteria.getQueryString());
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
