/*
 * Copyright 2013 Blazebit.
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
package com.blazebit.message.apt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * 
 * @author Christian
 */
public abstract class AbstractInterfaceProcessor<M extends InterfaceMethodInfo, T extends InterfaceInfo<M>> extends AbstractProcessor {
    
    protected abstract Set<Class<? extends Annotation>> getAnnotationsToProcess();
    
    protected abstract void processInterfaceInfo(T interfaceInfo) throws IOException;
    
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotationTypes = new HashSet<String>();
        
        for (Class<? extends Annotation> annotationClass : getAnnotationsToProcess()) {
            supportedAnnotationTypes.add(annotationClass.getName());
        }
        
        return supportedAnnotationTypes;
    }
    
    protected void printMessage(Kind kind, String message, Element e, Throwable t) {
        StringWriter sw = new StringWriter();
        sw.append(message);
        sw.append('\n');
        t.printStackTrace(new PrintWriter(sw));
        processingEnv.getMessager().printMessage(kind, sw.getBuffer(), e);
    }

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (annotations.isEmpty()) {
			return false;
		}
		
		for (Class<? extends Annotation> annotationClass : getAnnotationsToProcess()) {
		    for (Element e : roundEnv.getElementsAnnotatedWith(annotationClass)) {
		        if (e instanceof TypeElement) {
		            TypeElement typeElement = (TypeElement) e;
		            T info = processElement(typeElement, annotationClass);
		            
		            if (info != null) {
		                try {
		                    processInterfaceInfo(info);
		                } catch (Exception ex) {
		                    printMessage(Kind.ERROR, "Could not process interface info for type '" + info.getElement().getQualifiedName() + "'!", info.getElement(), ex);
		                }
		            }
		        }
		    }
		}

		return false;
	}

	private T processElement(TypeElement e, Class<? extends Annotation> annotationClass) {
	    String packageName = ((PackageElement) e.getEnclosingElement()).getQualifiedName().toString();
        String qualifiedName = e.getQualifiedName().toString();
        String simpleName = e.getSimpleName().toString();
        List<M> methodInfos = new ArrayList<M>();
    
        for (Element method : e.getEnclosedElements()) {
            if (method instanceof ExecutableElement) {
                M methodInfo = processMethod((ExecutableElement) method);
                if (methodInfo != null) {
                    methodInfos.add(methodInfo);
                }
            }
        }
    
        File javaSourceFile = getJavaSourceFile(packageName, simpleName);
        long lastModified = javaSourceFile.lastModified();
        return processElement(new DefaultInterfaceInfo<M>(e, qualifiedName, packageName, simpleName, javaSourceFile.getAbsolutePath(), lastModified, methodInfos), annotationClass);
    }

    protected T processElement(InterfaceInfo<M> interfaceInfo, Class<? extends Annotation> annotationClass) {
        return (T) interfaceInfo;
    }

    private M processMethod(ExecutableElement method) {
        String name = method.getSimpleName().toString();
        String qualifiedReturnTypeName = method.getReturnType().toString();
        List<? extends VariableElement> parameters = method.getParameters();
        List<String> qualifiedParameterTypeNames = new ArrayList<String>(parameters.size());
        
        for (VariableElement parameter : parameters) {
            qualifiedParameterTypeNames.add(parameter.asType().toString());
        }
        
        return processMethod(new DefaultInterfaceMethodInfo(method, name, qualifiedReturnTypeName, qualifiedParameterTypeNames));
    }

    protected M processMethod(InterfaceMethodInfo methodInfo) {
        return (M) methodInfo;
    }

    protected File getJavaSourceFile(String packageName, String className) {
        try {
            FileObject fileObject = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, packageName, className + ".java");
            return new File(fileObject.toUri());
        } catch(FileNotFoundException ex) {
            throw new IllegalArgumentException("Could not find the source file '" + className + ".java' that actually triggered the enum generation process", ex);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not find the source file '" + className + ".java' that actually triggered the enum generation process", ex);
        }
    }
}
