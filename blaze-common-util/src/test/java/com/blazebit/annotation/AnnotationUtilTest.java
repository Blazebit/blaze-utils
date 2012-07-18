/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.annotation;

import com.blazebit.reflection.ReflectionUtil;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.inject.Stereotype;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Christian Beikov
 */
public class AnnotationUtilTest {

    @Retention(RetentionPolicy.RUNTIME)
    private static @interface Anno{
        String value();
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface Anno2{}
    
    @Retention(RetentionPolicy.RUNTIME)
    @Stereotype
    @Anno("stereotyped")
    private static @interface StereotypeAnno{}
    
    @Anno("classA")
    @Anno2
    private static class ClassA{
        
        @StereotypeAnno
        @Anno2
        public void m1(){}
    }
    
    @StereotypeAnno
    @Anno("classB")
    private static class ClassB extends ClassA{
        
    }
    
    @Test
    public void testGetAllAnnotationsOnClassLevel() throws Exception{
        // Test ClassA
        
        Set<Annotation> annotations = AnnotationUtil.getAllAnnotations(ClassA.class);
        assertFalse(annotations.isEmpty());
        
        Set<String> expectedValues = new HashSet<String>(Arrays.asList("classA"));
        Set<String> values = new HashSet<String>(getMemberValues(annotations, Anno.class, String.class, "value"));
        
        assertFalse(findAnnotation(annotations, Anno2.class).isEmpty());
        assertEquals(expectedValues, values);
        
        // Test ClassB
        
        annotations = AnnotationUtil.getAllAnnotations(ClassB.class);
        assertFalse(annotations.isEmpty());
        
        expectedValues = new HashSet<String>(Arrays.asList("classA", "classB", "stereotyped"));
        values = new HashSet<String>(getMemberValues(annotations, Anno.class, String.class, "value"));
        
        assertEquals(expectedValues, values);
        assertFalse(findAnnotation(annotations, Anno2.class).isEmpty());
    }
    
    @Test
    public void testGetAllAnnotationsOnMethodLevel() throws Exception{
        Set<Annotation> annotations = AnnotationUtil.getAllAnnotations(ReflectionUtil.getMethod(ClassA.class, "m1"));
        List<String> expected = Arrays.asList("stereotyped");
        List<String> values = getMemberValues(annotations, Anno.class, String.class, "value");
        
        assertFalse(annotations.isEmpty());
        assertFalse(findAnnotation(annotations, Anno.class).isEmpty());
        assertFalse(findAnnotation(annotations, Anno2.class).isEmpty());
        assertEquals(expected, values);
    }
    
    @Test
    public void testFindAnnotationOnClassLevel() throws Exception{
        assertNotNull(AnnotationUtil.findAnnotation(ClassA.class, Anno.class));
        assertNotNull(AnnotationUtil.findAnnotation(ClassA.class, Anno2.class));
        assertEquals("classA", AnnotationUtil.findAnnotation(ClassA.class, Anno.class).value());
        
        assertNotNull(AnnotationUtil.findAnnotation(ClassB.class, Anno.class));
        assertNotNull(AnnotationUtil.findAnnotation(ClassB.class, Anno2.class));
        assertEquals("classB", AnnotationUtil.findAnnotation(ClassB.class, Anno.class).value());
    }
    
    @Test
    public void testFindAnnotationOnMethodLevel() throws Exception{
        assertNotNull(AnnotationUtil.findAnnotation(ReflectionUtil.getMethod(ClassA.class, "m1"), Anno.class));
        assertNotNull(AnnotationUtil.findAnnotation(ReflectionUtil.getMethod(ClassA.class, "m1"), Anno2.class));
        assertEquals("stereotyped", AnnotationUtil.findAnnotation(ReflectionUtil.getMethod(ClassA.class, "m1"), Anno.class).value());
    }
    
    private <T extends Annotation> List<T> findAnnotation(Set<Annotation> annos, Class<T> annotationType){
        List<T> l = new ArrayList<T>();
        
        for(Annotation a : annos){
            if(a.annotationType().equals(annotationType)){
                l.add(annotationType.cast(a));
            }
        }
        
        return l;
    }
    
    private <T extends Annotation, V> List<V> getMemberValues(Set<Annotation> annos, Class<T> annotationType, Class<V> valueType, String memberName) throws Exception{
        List<V> l = new ArrayList<V>();
        
        for(Annotation a : annos){
            if(a.annotationType().equals(annotationType)){
                l.add(valueType.cast(ReflectionUtil.getMethod(annotationType, memberName).invoke(a)));
            }
        }
        
        return l;
    }
}
