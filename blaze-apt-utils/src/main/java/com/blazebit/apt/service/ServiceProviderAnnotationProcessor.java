/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.apt.service;

import com.blazebit.apt.AnnotationProcessingUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

/**
 * Constraint Validator classes must be available in compiled form!
 *
 * @author Christian Beikov
 * @since 0.1.2
 */
@SupportedAnnotationTypes("com.blazebit.apt.service.ServiceProvider")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ServiceProviderAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return true;
        }

        Map<String, List<String>> serviceProviders = new HashMap<String, List<String>>();

        for (Element e : roundEnv
                .getElementsAnnotatedWith(ServiceProvider.class)) {
            TypeElement typeElement = (TypeElement) e;
            AnnotationMirror annotation = AnnotationProcessingUtils
                    .findAnnotationMirror(processingEnv, typeElement,
                            ServiceProvider.class);

            if (annotation == null) {
                // Workaround a strange bug...
                continue;
            }

            String service = AnnotationProcessingUtils
                    .getAnnotationElementValue(processingEnv, annotation,
                            "value").getValue().toString();

            List<String> providers = serviceProviders.get(service);

            if (providers == null) {
                providers = new ArrayList<String>();
                serviceProviders.put(service, providers);
            }

            providers.add(typeElement.getQualifiedName().toString());
        }

        String serviceFile = null;

        try {
            for (Map.Entry<String, List<String>> entry : serviceProviders
                    .entrySet()) {
                serviceFile = entry.getKey();
                FileObject providerFileObject = processingEnv.getFiler()
                        .createResource(StandardLocation.SOURCE_OUTPUT, "",
                                "META-INF/services/" + serviceFile);

                List<String> services = entry.getValue();
                BufferedWriter writer = null;

                try {
                    writer = new BufferedWriter(providerFileObject.openWriter());

                    for (int i = 0; i < services.size(); i++) {
                        writer.append(services.get(i)).append('\n');
                    }
                } finally {
                    if (writer != null) {
                        writer.close();
                    }
                }
            }
        } catch (Exception ex) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ex.printStackTrace(new PrintStream(baos));
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Could not create service file '" + serviceFile + "'\n"
                            + baos.toString());
            return false;
        }

        return true;
    }
}
