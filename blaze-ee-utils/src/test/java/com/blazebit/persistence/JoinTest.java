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
public class JoinTest {
    
    final String defaultDocumentAlias = "document";
    
    @Test
    public void testDefaultAlias(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class);
        assertEquals("FROM Document " + defaultDocumentAlias, criteria.getQueryString());
    }
    
    @Test
    public void testRightJoinFetch(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.rightJoinFetch("owner", "o");
        criteria.rightJoinFetch("version", "v");
        
        assertEquals("FROM Document d RIGHT JOIN FETCH d.owner o RIGHT JOIN FETCH d.version v", criteria.getQueryString());
    }
    
    @Test
    public void testRightJoin(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.rightJoin("owner", "o");
        criteria.rightJoin("version", "v");
        
        assertEquals("FROM Document d RIGHT JOIN d.owner o RIGHT JOIN d.version v", criteria.getQueryString());
    }
    
    @Test
    public void testLeftJoinFetch(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.leftJoinFetch("owner", "o");
        criteria.leftJoinFetch("version", "v");
        
        assertEquals("FROM Document d LEFT JOIN FETCH d.owner o LEFT JOIN FETCH d.version v", criteria.getQueryString());
    }
    
    @Test
    public void testLeftJoin(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.leftJoin("owner", "o");
        criteria.leftJoin("version", "v");
        
        assertEquals("FROM Document d LEFT JOIN d.owner o LEFT JOIN d.version v", criteria.getQueryString());
    }
    
    @Test
    public void testInnerJoinFetch(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.innerJoinFetch("owner", "o");
        criteria.innerJoinFetch("version", "v");
        
        assertEquals("FROM Document d JOIN FETCH d.owner o JOIN FETCH d.version v", criteria.getQueryString());
    }
    
    @Test
    public void testInnerJoin(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.innerJoin("owner", "o");
        criteria.innerJoin("version", "v");
        
        assertEquals("FROM Document d JOIN d.owner o JOIN d.version v", criteria.getQueryString());
    }
    
    @Test
    public void testOuterJoinFetch(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.outerJoinFetch("owner", "o");
        criteria.outerJoinFetch("version", "v");
        
        assertEquals("FROM Document d OUTER JOIN FETCH d.owner o OUTER JOIN FETCH d.version v", criteria.getQueryString());
    }
    
    @Test
    public void testOuterJoin(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.outerJoin("owner", "o");
        criteria.outerJoin("version", "v");
        
        assertEquals("FROM Document d OUTER JOIN d.owner o OUTER JOIN d.version v", criteria.getQueryString());
    }
    
    @Test
    public void testJoinMethodEquivalences(){
        final String qInnerJoin = CriteriaBuilder.from(Document.class, "d").join("owner", "o", JoinType.INNER, false).getQueryString();
        final String qInnerJoinFetch = CriteriaBuilder.from(Document.class, "d").join("owner", "o", JoinType.INNER, true).getQueryString();
        final String qLeftJoin = CriteriaBuilder.from(Document.class, "d").join("owner", "o", JoinType.LEFT, false).getQueryString();
        final String qLeftJoinFetch = CriteriaBuilder.from(Document.class, "d").join("owner", "o", JoinType.LEFT, true).getQueryString();
        final String qRightJoin = CriteriaBuilder.from(Document.class, "d").join("owner", "o", JoinType.RIGHT, false).getQueryString();
        final String qRightJoinFetch = CriteriaBuilder.from(Document.class, "d").join("owner", "o", JoinType.RIGHT, true).getQueryString();
        final String qOuterJoin = CriteriaBuilder.from(Document.class, "d").join("owner", "o", JoinType.OUTER, false).getQueryString();
        final String qOuterJoinFetch = CriteriaBuilder.from(Document.class, "d").join("owner", "o", JoinType.OUTER, true).getQueryString();
        
        assertEquals(CriteriaBuilder.from(Document.class, "d").innerJoin("owner", "o").getQueryString(),
                qInnerJoin);
        assertEquals(CriteriaBuilder.from(Document.class, "d").innerJoinFetch("owner", "o").getQueryString(),
                qInnerJoinFetch);
        assertEquals(CriteriaBuilder.from(Document.class, "d").rightJoin("owner", "o").getQueryString(),
                qRightJoin);
        assertEquals(CriteriaBuilder.from(Document.class, "d").rightJoinFetch("owner", "o").getQueryString(),
                qRightJoinFetch);
        assertEquals(CriteriaBuilder.from(Document.class, "d").leftJoin("owner", "o").getQueryString(),
                qLeftJoin);
        assertEquals(CriteriaBuilder.from(Document.class, "d").leftJoinFetch("owner", "o").getQueryString(),
                qLeftJoinFetch);
        assertEquals(CriteriaBuilder.from(Document.class, "d").outerJoin("owner", "o").getQueryString(),
                qOuterJoin);
        assertEquals(CriteriaBuilder.from(Document.class, "d").outerJoinFetch("owner", "o").getQueryString(),
                qOuterJoinFetch);
    }
    
    @Test
    public void testNestedJoin(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.join("owner.employees.contacts", "cont", JoinType.LEFT, false);
        criteria.join("owner.employees.contacts.name", "contName", JoinType.RIGHT, true);
        criteria.join("partner", "p", JoinType.INNER, true);
        
        String q = criteria.getQueryString();
        assertEquals("FROM Document d LEFT JOIN d.owner owner LEFT JOIN owner.employees employees LEFT JOIN employees.contacts cont RIGHT JOIN FETCH cont.name contName JOIN FETCH d.partner p", q);
    }
    
    @Test(expected = NullPointerException.class)
    public void testConstructorAliasNull(){
        CriteriaBuilder.from(Document.class, null);
    }
    
    @Test(expected = NullPointerException.class)
    public void testConstructorClassNull(){
        CriteriaBuilder.from(null, "d");
    }
    
    @Test(expected = NullPointerException.class)
    public void testJoinNullPath(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class);
        criteria.join(null, "o", JoinType.LEFT, true);
    }
    
    @Test(expected = NullPointerException.class)
    public void testJoinNullAlias(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class);
        criteria.join("owner", null, JoinType.LEFT, true);
    }
    
    @Test(expected = NullPointerException.class)
    public void testJoinNullJoinType(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class);
        criteria.join("owner", "o", null, true);
    }
    
//    @Test(expected = InvalidAliasException.class)
//    public void testJoinInvalidAlias1(){
//        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
//        criteria.join("owner", "d.owner", JoinType.LEFT, true);
//    }
//    
//    @Test(expected = InvalidAliasException.class)
//    public void testJoinInvalidAlias2(){
//        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
//        criteria.join("owner", ".", JoinType.LEFT, true);
//    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testJoinEmptyAlias(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class);
        criteria.join("owner", "", JoinType.LEFT, true);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testUnresolvedAlias1(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "a");
        
        criteria.where("z.c.x").eq(0).leftJoin("a.b", "b");
        
        criteria.getQueryString();
    }
    
    @Test(expected = IllegalStateException.class)
    public void testUnresolvedAlias2(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "a");
        
        criteria.where("z").eq(0);
        
        criteria.getQueryString();
    }
    
    @Test(expected = IllegalStateException.class)
    public void testUnresolvedAliasInOrderBy(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "a");
        
        criteria.orderByAsc("z");
        
        criteria.getQueryString();
    }
    
    @Test
    public void testImplicitRootRelativeAlias(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "a");
        
        criteria.where("name.c.x").eq(0).leftJoin("a.b", "b");
        
        System.out.println(criteria.getQueryString());
        assertEquals("FROM Document a LEFT JOIN a.b b LEFT JOIN a.name name LEFT JOIN name.c c WHERE c.x = :param_0", criteria.getQueryString());
    }
    
    @Test
    public void testCallOrderInvariance(){
        CriteriaBuilder<Document> criteria1 = CriteriaBuilder.from(Document.class, "a");
        CriteriaBuilder<Document> criteria2 = CriteriaBuilder.from(Document.class, "a");
        
        criteria1.where("b.c.x").eq(0).leftJoin("a.b", "b");
        criteria2.leftJoin("a.b", "b").where("b.c.x").eq(0);
        
        System.out.println(criteria1.getQueryString());
        assertEquals("FROM Document a LEFT JOIN a.b b LEFT JOIN b.c c WHERE c.x = :param_0", criteria1.getQueryString());
        assertEquals("FROM Document a LEFT JOIN a.b b LEFT JOIN b.c c WHERE c.x = :param_0", criteria2.getQueryString());
    }
}
