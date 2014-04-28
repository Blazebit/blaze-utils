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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author ccbem
 */
public class IsEmptyTest {
    @Test
    public void testIsEmpty(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.name").isEmpty();
        
        assertEquals("FROM Document d WHERE d.name IS EMPTY", criteria.getQueryString());
    }
    
    @Test
    public void testIsNotEmpty(){
        CriteriaBuilder<Document> criteria = CriteriaBuilder.from(Document.class, "d");
        criteria.where("d.name").isNotEmpty();
        
        assertEquals("FROM Document d WHERE d.name IS NOT EMPTY", criteria.getQueryString());
    }
}
