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

package com.blazebit.persistence.view.impl.proxy;

import com.blazebit.persistence.entity.Person;
import com.blazebit.persistence.view.impl.EntityViewConfiguration;
import com.blazebit.persistence.view.impl.proxy.model.DocumentClassView;
import com.blazebit.persistence.view.impl.proxy.model.DocumentInterfaceView;
import com.blazebit.persistence.view.metamodel.ViewMetamodel;
import com.blazebit.persistence.view.metamodel.ViewType;
import com.blazebit.reflection.ReflectionUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cpbec
 */
public class ProxyFactoryTest {
    
    private ViewMetamodel getViewMetamodel() {
        EntityViewConfiguration cfg = new EntityViewConfiguration();
        cfg.addEntityView(DocumentInterfaceView.class);
        cfg.addEntityView(DocumentClassView.class);
        return cfg.createEntityViewManagerFactory().getMetamodel();
    }
    
    @Test
    public void testInterfaceProxy() throws Exception {
        ViewType<DocumentInterfaceView> viewType = getViewMetamodel().view(DocumentInterfaceView.class);
        Class<? extends DocumentInterfaceView> proxyClass = ProxyFactory.getProxy(viewType);
        
        // The parameter order is contacts, firstContactPerson, id, name
        Constructor<? extends DocumentInterfaceView> constructor = proxyClass.getConstructor(Map.class, Person.class, Long.class, String.class);
        
        Map<Integer, Person> expectedContacts = new HashMap<Integer, Person>();
        Person expectedFirstContactPerson = new Person("pers");
        Long expectedId = 1L;
        String expectedName = "doc";
        
        DocumentInterfaceView instance = constructor.newInstance(expectedContacts, expectedFirstContactPerson, expectedId, expectedName);
        
        assertTrue(expectedContacts == instance.getContacts());
        assertTrue(expectedFirstContactPerson == instance.getFirstContactPerson());
        assertTrue(expectedId == instance.getId());
        assertTrue(expectedName == instance.getName());
        
        expectedContacts = new HashMap<Integer, Person>();
        expectedId = 2L;
        
        instance.setContacts(expectedContacts);
        instance.setId(expectedId);
        
        assertTrue(expectedContacts == instance.getContacts());
        assertTrue(expectedId == instance.getId());
    }
    
    @Test
    public void testClassProxy() throws Exception {
        ViewType<DocumentClassView> viewType = getViewMetamodel().view(DocumentClassView.class);
        Class<? extends DocumentClassView> proxyClass = ProxyFactory.getProxy(viewType);
        
        // The parameter order is contacts, firstContactPerson, id, name
        Constructor<? extends DocumentClassView> constructor = proxyClass.getConstructor(Map.class, Person.class, Long.class, String.class, long.class);
        
        Map<Integer, Person> expectedContacts = new HashMap<Integer, Person>();
        Person expectedFirstContactPerson = new Person("pers");
        Long expectedId = 1L;
        String expectedName = "doc";
        long expectedAge = 10;
        
        DocumentClassView instance = constructor.newInstance(expectedContacts, expectedFirstContactPerson, expectedId, expectedName, expectedAge);
        
        assertTrue(expectedContacts == instance.getContacts());
        assertTrue(expectedFirstContactPerson == instance.getFirstContactPerson());
        assertTrue(expectedId == instance.getId());
        assertTrue(expectedName == instance.getName());
        assertTrue(expectedAge == instance.getAge());
        
        expectedContacts = new HashMap<Integer, Person>();
        expectedId = 2L;
        
        instance.setContacts(expectedContacts);
        instance.setId(expectedId);
        
        assertTrue(expectedContacts == instance.getContacts());
        assertTrue(expectedId == instance.getId());
    }
    
    @Test
    public void testInterfaceProxyStructure() throws Exception {
        ViewType<DocumentInterfaceView> viewType = getViewMetamodel().view(DocumentInterfaceView.class);
        Class<? extends DocumentInterfaceView> proxyClass = ProxyFactory.getProxy(viewType);
        
        assertEquals(1, proxyClass.getDeclaredConstructors().length);
        assertNotNull(proxyClass.getDeclaredConstructor(Map.class, Person.class, Long.class, String.class));
        
        assertEquals(4, proxyClass.getDeclaredFields().length);
        // 4 Getters, 2 Setter, 1 Bridge-Getter, 1 Bridge-Setter
        assertEquals(8, proxyClass.getDeclaredMethods().length);
        assertAttribute(proxyClass, "contacts", Modifier.PRIVATE, Map.class, Integer.class, Person.class);
        assertAttribute(proxyClass, "firstContactPerson", Modifier.PRIVATE | Modifier.FINAL, Person.class);
        assertAttribute(proxyClass, "id", Modifier.PRIVATE, Long.class);
        assertAttribute(proxyClass, "name", Modifier.PRIVATE | Modifier.FINAL, String.class);
    }
    
    @Test
    public void testClassProxyStructure() throws Exception {
        ViewType<DocumentClassView> viewType = getViewMetamodel().view(DocumentClassView.class);
        Class<? extends DocumentClassView> proxyClass = ProxyFactory.getProxy(viewType);
        
        assertEquals(1, proxyClass.getDeclaredConstructors().length);
        assertNotNull(proxyClass.getDeclaredConstructor(Map.class, Person.class, Long.class, String.class, long.class));
        
        assertEquals(4, proxyClass.getDeclaredFields().length);
        // 4 Getters, 2 Setter, 1 Bridge-Getter, 1 Bridge-Setter
        assertEquals(8, proxyClass.getDeclaredMethods().length);
        assertAttribute(proxyClass, "contacts", Modifier.PRIVATE, Map.class, Integer.class, Person.class);
        assertAttribute(proxyClass, "firstContactPerson", Modifier.PRIVATE | Modifier.FINAL, Person.class);
        assertAttribute(proxyClass, "id", Modifier.PRIVATE, Long.class);
        assertAttribute(proxyClass, "name", Modifier.PRIVATE | Modifier.FINAL, String.class);
    }
    
    private void assertAttribute(Class<?> proxyClass, String fieldName, int modifiers, Class<?> type, Class<?>... typeArguments) throws Exception {
        assertField(proxyClass, fieldName, modifiers, type, typeArguments);
        assertGetter(proxyClass, fieldName, type, typeArguments);
        if ((modifiers & Modifier.FINAL) == 0) {
            assertSetter(proxyClass, fieldName, type, typeArguments);
        }
    }
    
    private void assertField(Class<?> proxyClass, String fieldName, int modifiers, Class<?> type, Class<?>... typeArguments) throws Exception {
        Field field = proxyClass.getDeclaredField(fieldName);
        assertNotNull(field);
        assertEquals(modifiers, field.getModifiers());
        assertEquals(type, ReflectionUtils.getResolvedFieldType(proxyClass, field));
        assertArrayEquals(typeArguments, ReflectionUtils.getResolvedFieldTypeArguments(proxyClass, field));
    }
    
    private void assertGetter(Class<?> proxyClass, String attributeName, Class<?> type, Class<?>... typeArguments) throws Exception {
        Method method = ReflectionUtils.getGetter(proxyClass, attributeName);
        assertNotNull(method);
        assertEquals("Getter modifiers: " + attributeName, Modifier.PUBLIC, method.getModifiers());
        assertEquals("Getter return type of: " + attributeName, type, ReflectionUtils.getResolvedMethodReturnType(proxyClass, method));
        assertArrayEquals("Getter return type arguments of: " + attributeName, typeArguments, ReflectionUtils.getResolvedMethodReturnTypeArguments(proxyClass, method));
    }
    
    private void assertSetter(Class<?> proxyClass, String attributeName, Class<?> type, Class<?>... typeArguments) throws Exception {
        Method method = ReflectionUtils.getSetter(proxyClass, attributeName);
        assertNotNull(method);
        assertEquals("Setter modifiers: " + attributeName, Modifier.PUBLIC, method.getModifiers());
        assertEquals("Setter parameter type of: " + attributeName, type, ReflectionUtils.getResolvedMethodParameterTypes(proxyClass, method)[0]);
        assertArrayEquals("Setter parameter type arguments of: " + attributeName, typeArguments, ReflectionUtils.getResolvedMethodParameterTypesArguments(proxyClass, method)[0]);
    }
}
