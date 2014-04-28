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

import com.blazebit.persistence.expression.ExpressionUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author ccbem
 */
public class LikeTest {
    @Test
    public void testLikeCaseInsensitive(){
        final String pattern = "te%t";
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.name").like(pattern, false, null);
        
        assertEquals("FROM Document d WHERE " + getCaseInsensitiveLike("d.name", ":param_0"), criteria.getQueryString());
    }
    
    @Test
    public void testLikeCaseSensitive(){
        final String pattern = "te%t";
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.name").like(pattern, true, null);
        
        assertEquals("FROM Document d WHERE d.name LIKE :param_0", criteria.getQueryString());
    }
    
    @Test
    public void testLikeEscaped(){
        final String pattern = "t\\_e%t";
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.name").like(pattern, true, '\\');
        
        assertEquals("FROM Document d WHERE d.name LIKE :param_0 ESCAPE '\\'", criteria.getQueryString());
    }
    
    @Test(expected = NullPointerException.class)
    public void testLikeNull(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.name").like(null, true, null);
    }
    
    @Test
    public void testLikeExpressionCaseInsensitive(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.name").likeExpression("d.owner.namePattern", false, null);
        
        assertEquals("FROM Document d WHERE " + getCaseInsensitiveLike("d.name", "d.owner.namePattern"), criteria.getQueryString());
    }
    
    @Test
    public void testLikeExpressionCaseSensitive(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.name").likeExpression("d.owner.namePattern", true, null);
        
        assertEquals("FROM Document d WHERE d.name LIKE d.owner.namePattern", criteria.getQueryString());
    }
    
    @Test
    public void testLikeExpressionEscaped(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.name").likeExpression("d.owner.namePattern", true, '\\');
        
        assertEquals("FROM Document d WHERE d.name LIKE d.owner.namePattern ESCAPE '\\'", criteria.getQueryString());
    }
    
    @Test(expected = NullPointerException.class)
    public void testLikeExpressionNull(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.name").likeExpression(null, true, null);
    }
    
    /***** NOT LIKE *****/
    
    @Test
    public void testNotLikeCaseInsensitive() {
        final String pattern = "te%t";
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.name").notLike(pattern, false, null);
        
        assertEquals("FROM Document d WHERE " + getCaseInsensitiveNotLike("d.name", ":param_0"), criteria.getQueryString());
    }
    
    @Test
    public void testNotLikeCaseSensitive(){
        final String pattern = "te%t";
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.name").notLike(pattern, true, null);
        
        assertEquals("FROM Document d WHERE d.name NOT LIKE :param_0", criteria.getQueryString());
    }
    
    @Test
    public void testNotLikeEscaped(){
        final String pattern = "t\\_e%t";
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.name").notLike(pattern, true, '\\');
        
        assertEquals("FROM Document d WHERE d.name NOT LIKE :param_0 ESCAPE '\\'", criteria.getQueryString());
    }
    
    @Test(expected = NullPointerException.class)
    public void testNotLikeNull(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.name").notLike(null, true, null);
    }
    
    @Test
    public void testNotLikeExpressionCaseInsensitive(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.name").notLikeExpression("d.owner.namePattern", false, null);
        
        assertEquals("FROM Document d WHERE " + getCaseInsensitiveNotLike("d.name", "d.owner.namePattern"), criteria.getQueryString());
    }
    
    @Test
    public void testNotLikeExpressionCaseSensitive(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.name").notLikeExpression("d.owner.namePattern", true, null);
        
        assertEquals("FROM Document d WHERE d.name NOT LIKE d.owner.namePattern", criteria.getQueryString());
    }
    
    @Test
    public void testNotLikeExpressionEscaped(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.name").notLikeExpression("d.owner.namePattern", true, '\\');
        
        assertEquals("FROM Document d WHERE d.name NOT LIKE d.owner.namePattern ESCAPE '\\'", criteria.getQueryString());
    }
    
    @Test(expected = NullPointerException.class)
    public void testNotLikeExpressionNull(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.name").notLikeExpression(null, true, null);
    }
    
    private String getCaseInsensitiveLike(String property, String likeParam){
        return property + " LIKE UPPER(" + likeParam + ")";
    }
    
    private String getCaseInsensitiveNotLike(String property, String likeParam){
        return property + " NOT LIKE UPPER(" + likeParam + ")";
    }
}
