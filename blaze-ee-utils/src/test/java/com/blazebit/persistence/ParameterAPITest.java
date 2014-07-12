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
import com.blazebit.persistence.expression.ExpressionUtils;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author ccbem
 */
public class ParameterAPITest extends AbstractPersistenceTest{
    @Test(expected = javax.jms.IllegalStateException.class)
    public void testIsParameterSet_noSuchParamter(){
        CriteriaBuilder<Document> crit = CriteriaProvider.from(em, Document.class);
        crit.isParameterSet("index");
    }
    
    @Test(expected = javax.jms.IllegalStateException.class)
    public void testSetParameter_noSuchParamter(){
        CriteriaBuilder<Document> crit = CriteriaProvider.from(em, Document.class);
        crit.setParameter("index", 0);
    }
    
    @Test(expected = javax.jms.IllegalStateException.class)
    public void testSetDateParameter_noSuchParamter(){
        CriteriaBuilder<Document> crit = CriteriaProvider.from(em, Document.class);
        crit.setParameter("index", new Date(), TemporalType.DATE);
    }
    
    @Test(expected = javax.jms.IllegalStateException.class)
    public void testSetCalendarParameter_noSuchParamter(){
        CriteriaBuilder<Document> crit = CriteriaProvider.from(em, Document.class);
        crit.setParameter("index", Calendar.getInstance(), TemporalType.DATE);
    }
    
    @Test
    public void test(){
        ExpressionUtils.parse("age > :minAge");
        CriteriaBuilder<Document> crit = CriteriaProvider.from(em, Document.class);
//        crit.isParameterSet("index")
        crit.select("contacts[:index]")
                .where("contacts[:where_index]").isNotNull()
                .where("name").eq("MyDoc")
                .where("lastModified").lt().expression(":lastModifiedFilter")
                .groupBy("age")
                .having("age > :minAge");
        
        assertFalse(crit.isParameterSet("index"));
        assertFalse(crit.isParameterSet("where_index"));
        assertFalse(crit.isParameterSet("minAge"));
        assertFalse(crit.isParameterSet("lastModifiedFilter"));
        assertTrue(crit.isParameterSet("param_0"));
        
        Set<? extends Parameter> params  = crit.getParameters();
        assertTrue(params.size() == 5);
        for(Parameter p : params){
            if("param_0".equals(p.getName())){
                assertTrue(p.getParameterType().equals(String.class));
            }else{
                assertTrue(p.getParameterType() == null);
            }
            assertTrue(p.getPosition() == null);
        }
        crit.setParameter("index", 1);
        crit.setParameter("where_index", 2);
        crit.setParameter("minAge", 3);
        crit.setParameter("lastModifiedFilter", new Date(), TemporalType.TIMESTAMP);
        
        assertTrue(crit.isParameterSet("index"));
        assertTrue(crit.isParameterSet("where_index"));
        assertTrue(crit.isParameterSet("minAge"));
        assertTrue(crit.isParameterSet("lastModifiedFilter"));
    }
}
