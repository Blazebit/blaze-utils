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
public class DistinctTest {

    @Test
    public void testDistinct() {
        CriteriaBuilder<Document> criteria = CriteriaProvider.from(Document.class, "d");
        criteria.select("d.partners.name").distinct();
        
        assertEquals("SELECT DISTINCT partners.name FROM Document d LEFT JOIN d.partners partners", criteria.getQueryString());
    }
    
    @Test(expected = IllegalStateException.class)
    public void testDistinctWithoutSelect() {
        CriteriaBuilder<Document> criteria = CriteriaProvider.from(Document.class, "d");
        criteria.distinct();     
    }
    
    
}
