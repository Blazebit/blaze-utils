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
import com.blazebit.persistence.entity.Person;
import com.blazebit.persistence.entity.Version;
import com.blazebit.persistence.model.DocumentViewModel;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import org.hibernate.ejb.Ejb3Configuration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author ccbem
 */
public class PaginationTest {
    
    private static EntityManager em;
    
    @BeforeClass
    public static void init() {
        Properties properties = new Properties();
        properties.put("javax.persistence.provider", "org.hibernate.ejb.HibernatePersistence");
        properties.put("javax.persistence.transactionType", "RESOURCE_LOCAL");
        properties.put("hibernate.connection.url", "jdbc:h2:mem:test");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.connection.driver_class", "org.h2.Driver");
        properties.put("hibernate.connection.password", "admin");
        properties.put("hibernate.connection.username", "admin");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");

        Ejb3Configuration cfg = new Ejb3Configuration();
        cfg.addProperties(properties);
        cfg.addAnnotatedClass(Document.class);
        cfg.addAnnotatedClass(Version.class);
        cfg.addAnnotatedClass(Person.class);

        EntityManagerFactory factory = cfg.buildEntityManagerFactory();
        em = factory.createEntityManager();
    }

    @Before
    public void setUp() {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Document doc1 = new Document("doc1");
            Document doc2 = new Document("Doc2");
            Document doc3 = new Document("doC3");
            Document doc4 = new Document("dOc4");
            Document doc5 = new Document("DOC5");
            Document doc6 = new Document("bdoc");
            Document doc7 = new Document("adoc");
            
            Person o1 = new Person("Karl1");
            Person o2 = new Person("Karl2");
            
            doc1.setOwner(o1);
            doc2.setOwner(o1);
            doc3.setOwner(o1);
            doc4.setOwner(o2);
            doc5.setOwner(o2);
            doc6.setOwner(o2);
            doc7.setOwner(o2);
            
            em.persist(o1);
            em.persist(o2);
            
            em.persist(doc1);
            em.persist(doc2);
            em.persist(doc3);
            em.persist(doc4);
            em.persist(doc5);
            em.persist(doc6);
            em.persist(doc7);
            
            em.flush();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            tx.rollback();
        }
    }
    
    @Test
    public void simpleTest() {
        CriteriaBuilder<DocumentViewModel> crit = CriteriaProvider.from(Document.class, "d")
                .selectNew(DocumentViewModel.class)
                    .with("d.name")
                    .with("CONCAT(d.owner.name, ' user')")
                    .with("COALESCE(d.owner.localized[1],'no item')")
                    .with("d.owner.partnerDocument.name")
                .end();
        crit.where("d.name").like("doc%", false, null);
        crit.where("d.owner.name").like("%arl%", true, null);
        crit.where("d.owner.localized[1]").like("a%", false, null);
        crit.orderByAsc("d.id");
        
        //TODO: introduce default ordering for pagination: by @id ASC NULLS LAST
        
        // do not include joins that are only needed for the select clause
        String expectedCountQuery = "SELECT COUNT(*) FROM Document d LEFT JOIN d.owner owner LEFT JOIN owner.localized localized "
                + "WHERE UPPER(d.name) LIKE UPPER('doc%') AND owner.name LIKE '%arl%' AND KEY(localized) = 1 AND UPPER(VALUE(localized)) LIKE UPPER('a%')";
        
        // limit this query using setFirstResult() and setMaxResult() according to the parameters passed to page()
        String expectedIdQuery = "SELECT d.id FROM Document d LEFT JOIN d.owner owner LEFT JOIN owner.localized localized "
                + "WHERE UPPER(d.name) LIKE UPPER('doc%') AND owner.name LIKE '%arl%' AND KEY(localized) = 1 AND UPPER(VALUE(localized)) LIKE UPPER('a%') "
                + "ORDER BY d.id ASC NULLS LAST GROUP BY d.id";
        
        String expectedObjectQuery = "SELECT d.name, CONCAT(owner.name, ' user'), COALESCE(VALUE(localized), 'no item'), partnerDocument.name FROM Document d "
                + "LEFT JOIN d.owner owner LEFT JOIN owner.localized localized LEFT JOIN owner.partnerDocument partnerDocument "
                + "WHERE KEY(localized) = 1 AND d.id IN (:ids) "
                + "ORDER BY d.id ASC NULLS LAST";
        
        System.out.println(crit.getQueryString());
        PaginatedCriteriaBuilder<DocumentViewModel> pcb = crit.page(1, 2);
        
        assertEquals(expectedCountQuery, pcb.getCountQueryString());
        assertEquals(expectedIdQuery, pcb.getIdQueryString());
        assertEquals(expectedObjectQuery, pcb.getQueryString());
        
        // crit.setParameter
        
        PagedList<DocumentViewModel> result = pcb.getResultList(em);
        assertEquals(2, result.size());
        assertEquals(3, result.totalSize());
        assertEquals("doc1", result.get(0).getName());
        assertEquals("doc2", result.get(1).getName());
    }
}
