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
public class IsMemberOfTest {
    @Test
    public void testIsMemberOf(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.where("d.name").isMemberOf("d.parentDocuments.childDocuments.name");
        
        assertEquals("FROM Document d LEFT JOIN d.parentDocuments parentDocuments LEFT JOIN parentDocuments.childDocuments childDocuments WHERE d.name MEMBER OF childDocuments", criteria.getQueryString());
    }
    
    @Test(expected = NullPointerException.class)
    public void testIsMemberOfNull(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.where("d.name").isMemberOf(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testIsMemberOfEmpty(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.where("d.name").isMemberOf("");
    }
    
    @Test
    public void testIsNotMemberOf(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.where("d.name").isNotMemberOf("d.parentDocuments.childDocuments.name");
        
        assertEquals("FROM Document d LEFT JOIN d.parentDocuments parentDocuments LEFT JOIN parentDocuments.childDocuments childDocuments WHERE d.name NOT MEMBER OF childDocuments", criteria.getQueryString());
    }
    
    @Test(expected = NullPointerException.class)
    public void testIsNotMemberOfNull(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.where("d.name").isNotMemberOf(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testIsNotMemberOfEmpty(){
        CriteriaBuilderImpl<Document> criteria = CriteriaBuilderImpl.from(Document.class, "d");
        criteria.where("d.name").isNotMemberOf("");
    }
}
