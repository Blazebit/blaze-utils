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
public class ExtendedCriteriaBuilderTest {
    
    final String defaultDocumentAlias = "document";
    
    @Test
    public void testDefaultAlias(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class);
        assertEquals("FROM Document " + defaultDocumentAlias, criteria.getQueryString());
    }
    
    @Test
    public void testRightJoinFetch(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.rightJoinFetch("owner", "o");
        criteria.rightJoinFetch("version", "v");
        
        assertEquals("FROM Document d RIGHT JOIN FETCH d.owner o RIGHT JOIN FETCH d.version v", criteria.getQueryString());
    }
    
    @Test
    public void testRightJoin(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.rightJoin("owner", "o");
        criteria.rightJoin("version", "v");
        
        assertEquals("FROM Document d RIGHT JOIN d.owner o RIGHT JOIN d.version v", criteria.getQueryString());
    }
    
    @Test
    public void testLeftJoinFetch(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.leftJoinFetch("owner", "o");
        criteria.leftJoinFetch("version", "v");
        
        assertEquals("FROM Document d LEFT JOIN FETCH d.owner o LEFT JOIN FETCH d.version v", criteria.getQueryString());
    }
    
    @Test
    public void testLeftJoin(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.leftJoin("owner", "o");
        criteria.leftJoin("version", "v");
        
        assertEquals("FROM Document d LEFT JOIN d.owner o LEFT JOIN d.version v", criteria.getQueryString());
    }
    
    @Test
    public void testInnerJoinFetch(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.innerJoinFetch("owner", "o");
        criteria.innerJoinFetch("version", "v");
        
        assertEquals("FROM Document d JOIN FETCH d.owner o JOIN FETCH d.version v", criteria.getQueryString());
    }
    
    @Test
    public void testInnerJoin(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.innerJoin("owner", "o");
        criteria.innerJoin("version", "v");
        
        assertEquals("FROM Document d JOIN d.owner o JOIN d.version v", criteria.getQueryString());
    }
    
    @Test
    public void testOuterJoinFetch(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.outerJoinFetch("owner", "o");
        criteria.outerJoinFetch("version", "v");
        
        assertEquals("FROM Document d OUTER JOIN FETCH d.owner o OUTER JOIN FETCH d.version v", criteria.getQueryString());
    }
    
    @Test
    public void testOuterJoin(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.outerJoin("owner", "o");
        criteria.outerJoin("version", "v");
        
        assertEquals("FROM Document d OUTER JOIN d.owner o OUTER JOIN d.version v", criteria.getQueryString());
    }
    
    @Test
    public void testJoinMethodEquivalences(){
        final String qInnerJoin = CriteriaBuilderImpl.from(Document.class, "d").join("owner", "o", JoinType.INNER, false).getQueryString();
        final String qInnerJoinFetch = CriteriaBuilderImpl.from(Document.class, "d").join("owner", "o", JoinType.INNER, true).getQueryString();
        final String qLeftJoin = CriteriaBuilderImpl.from(Document.class, "d").join("owner", "o", JoinType.LEFT, false).getQueryString();
        final String qLeftJoinFetch = CriteriaBuilderImpl.from(Document.class, "d").join("owner", "o", JoinType.LEFT, true).getQueryString();
        final String qRightJoin = CriteriaBuilderImpl.from(Document.class, "d").join("owner", "o", JoinType.RIGHT, false).getQueryString();
        final String qRightJoinFetch = CriteriaBuilderImpl.from(Document.class, "d").join("owner", "o", JoinType.RIGHT, true).getQueryString();
        final String qOuterJoin = CriteriaBuilderImpl.from(Document.class, "d").join("owner", "o", JoinType.OUTER, false).getQueryString();
        final String qOuterJoinFetch = CriteriaBuilderImpl.from(Document.class, "d").join("owner", "o", JoinType.OUTER, true).getQueryString();
        
        assertEquals(CriteriaBuilderImpl.from(Document.class, "d").innerJoin("owner", "o").getQueryString(),
                qInnerJoin);
        assertEquals(CriteriaBuilderImpl.from(Document.class, "d").innerJoinFetch("owner", "o").getQueryString(),
                qInnerJoinFetch);
        assertEquals(CriteriaBuilderImpl.from(Document.class, "d").rightJoin("owner", "o").getQueryString(),
                qRightJoin);
        assertEquals(CriteriaBuilderImpl.from(Document.class, "d").rightJoinFetch("owner", "o").getQueryString(),
                qRightJoinFetch);
        assertEquals(CriteriaBuilderImpl.from(Document.class, "d").leftJoin("owner", "o").getQueryString(),
                qLeftJoin);
        assertEquals(CriteriaBuilderImpl.from(Document.class, "d").leftJoinFetch("owner", "o").getQueryString(),
                qLeftJoinFetch);
        assertEquals(CriteriaBuilderImpl.from(Document.class, "d").outerJoin("owner", "o").getQueryString(),
                qOuterJoin);
        assertEquals(CriteriaBuilderImpl.from(Document.class, "d").outerJoinFetch("owner", "o").getQueryString(),
                qOuterJoinFetch);
    }
    
    @Test
    public void testNestedJoin(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.join("owner.employees.contacts", "cont", JoinType.LEFT, false);
        criteria.join("owner.employees.contacts.name", "contName", JoinType.RIGHT, true);
        criteria.join("partner", "p", JoinType.INNER, true);
        
        String q = criteria.getQueryString();
        assertEquals("FROM Document d LEFT JOIN d.owner owner LEFT JOIN owner.employees employees LEFT JOIN employees.contacts cont RIGHT JOIN FETCH cont.name contName JOIN FETCH d.partner p", q);
    }
    
    @Test(expected = NullPointerException.class)
    public void testConstructorAliasNull(){
        CriteriaBuilderImpl.from(Document.class, null);
    }
    
    @Test(expected = NullPointerException.class)
    public void testJoinNullPath(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class);
        criteria.join(null, "o", JoinType.LEFT, true);
    }
    
    @Test(expected = NullPointerException.class)
    public void testJoinNullAlias(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class);
        criteria.join("owner", null, JoinType.LEFT, true);
    }
    
    @Test(expected = NullPointerException.class)
    public void testJoinNullJoinType(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class);
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
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class);
        criteria.join("owner", "", JoinType.LEFT, true);
    }

    
    
    @Test
    public void testWhereProperty(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.where("d.age").ge(25);

        assertEquals("FROM Document d d.age >= :param_0", criteria.getQueryString());
    }
    
    @Test
    public void testWhereExpression(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.where("d.age + 1").ge(25);

        assertEquals("FROM Document d d.age + 1 >= :param_0", criteria.getQueryString());
    }
    
    @Test
    public void testWherePath(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.where("d.owner.partners.age + 1").ge(25);

        assertEquals("FROM Document d LEFT JOIN d.owner owner LEFT JOIN owner.partners partners WHERE partners.age + 1 >= :param_0", criteria.getQueryString());
    }
    
    @Test(expected = javax.jms.IllegalStateException.class)
    public void testWhereNotClosed(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.where("d.age");
        criteria.where("d.owner");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWhereEmptyExpression(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.where("");
    
    }
    
    @Test(expected = NullPointerException.class)
    public void testWhereNullExpression(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.where(null);
    }
    
    
    
    
}
