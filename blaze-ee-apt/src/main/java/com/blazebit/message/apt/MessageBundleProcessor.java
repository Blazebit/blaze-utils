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

import com.blazebit.i18n.LocaleUtils;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import org.apache.deltaspike.core.api.message.MessageBundle;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.*;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Christian
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public abstract class MessageBundleProcessor extends AbstractInterfaceProcessor<MessageBundleElementInfo, MessageBundleInfo2> {


    @Override
    protected Set<Class<? extends Annotation>> getAnnotationsToProcess() {
        Set<Class<? extends Annotation>> classes = new HashSet<Class<? extends Annotation>>();
        classes.add(MessageBundle.class);
        return classes;
    }

    @Override
    protected MessageBundleInfo2 processElement(InterfaceInfo<MessageBundleElementInfo> interfaceInfo, Class<? extends Annotation> annotationClass) {
        if (interfaceInfo.getElement().getKind() != ElementKind.INTERFACE) {
            processingEnv.getMessager().printMessage(Kind.ERROR, "The annotation '" + annotationClass.getName() + "' can only be applied on interfaces!", interfaceInfo.getElement());
        }

        TypeMirror serializableType = processingEnv.getElementUtils().getTypeElement(Serializable.class.getName()).asType();
        TypeMirror interfaceType = interfaceInfo.getElement().asType();

        if (!processingEnv.getTypeUtils().isSubtype(interfaceType, serializableType)) {
            processingEnv.getMessager().printMessage(Kind.ERROR, "The message bundle interface must extend java.io.Serializable!", interfaceInfo.getElement());
        }

        String qualifiedEnumClassName = getQualifiedEnumClassName(interfaceInfo);
        String simpleEnumClassName = getSimpleEnumClassName(interfaceInfo);
        String propertiesBasePath = getPropertiesBasePath(interfaceInfo);
        String propertiesBaseName = getPropertiesBaseName(interfaceInfo);
        String templateLocation = getTemplateLocation(interfaceInfo);
        Collection<Locale> locales = getLocales(interfaceInfo);
        MessageBundleInfo2 messageBundleInfo = new MessageBundleInfo2(interfaceInfo, qualifiedEnumClassName, simpleEnumClassName, propertiesBasePath, propertiesBaseName, templateLocation, locales);

        validatePropertiesFiles(messageBundleInfo);

        return messageBundleInfo;
    }

    protected File getResourceFile(String propertiesBasePath, String propertiesFileName) {
        try {
            FileObject fileObject = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, propertiesBasePath, propertiesFileName);
            return new File(fileObject.toUri());
        } catch (FileNotFoundException ex) {
            throw new IllegalArgumentException("Could not find the properties file '" + propertiesFileName + "' at the location '" + propertiesBasePath + "'!", ex);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not load the properties file '" + propertiesFileName + "' from the location '" + propertiesBasePath + "'!", ex);
        }
    }

    protected String getPropertiesFileName(String propertiesBaseName, Locale locale) {
        StringBuilder sb = new StringBuilder(propertiesBaseName);

        if (locale.getLanguage() != null && !locale.getLanguage().isEmpty()) {
            sb.append('_').append(locale.getLanguage());
        }

        if (locale.getCountry() != null && !locale.getCountry().isEmpty()) {
            sb.append('_').append(locale.getCountry());
        }

        sb.append(".properties");

        return sb.toString();
    }

    protected String getQualifiedEnumClassName(InterfaceInfo<MessageBundleElementInfo> interfaceInfo) {
        return interfaceInfo.getQualifiedName() + "Enum";
    }

    protected String getSimpleEnumClassName(InterfaceInfo<MessageBundleElementInfo> interfaceInfo) {
        return interfaceInfo.getSimpleName() + "Enum";
    }

    protected String getTemplateLocation(InterfaceInfo<MessageBundleElementInfo> interfaceInfo) {
        MessageBundleConfig config = interfaceInfo.getElement().getAnnotation(MessageBundleConfig.class);
        return config.templateLocation();
    }

    protected String getPropertiesBasePath(InterfaceInfo<MessageBundleElementInfo> interfaceInfo) {
        MessageBundleConfig config = interfaceInfo.getElement().getAnnotation(MessageBundleConfig.class);
        String basePath = config.base();

        if (basePath.isEmpty()) {
            basePath = interfaceInfo.getQualifiedName().replaceAll("\\.", "/");
        }

        int slashIndex = basePath.lastIndexOf('/');

        if (slashIndex == -1) {
            return "";
        }

        return basePath.substring(0, slashIndex);
    }

    protected String getPropertiesBaseName(InterfaceInfo<MessageBundleElementInfo> interfaceInfo) {
        MessageBundleConfig config = interfaceInfo.getElement().getAnnotation(MessageBundleConfig.class);
        String basePath = config.base();

        if (basePath.isEmpty()) {
            basePath = interfaceInfo.getQualifiedName().replaceAll("\\.", "/");
        }

        int slashIndex = basePath.lastIndexOf('/');

        if (slashIndex == -1) {
            return basePath;
        }

        return basePath.substring(slashIndex + 1);
    }

    protected Collection<Locale> getLocales(InterfaceInfo<MessageBundleElementInfo> interfaceInfo) {
        MessageBundleConfig config = interfaceInfo.getElement().getAnnotation(MessageBundleConfig.class);
        Collection<Locale> locales = new ArrayList<Locale>(config.locales().length);

        for (String localeString : config.locales()) {
            locales.add(LocaleUtils.getLocale(localeString));
        }

        return locales;
    }

    @Override
    protected MessageBundleElementInfo processMethod(InterfaceMethodInfo methodInfo) {
        String enumKey = getEnumKey(methodInfo);
        MessageBundleElementInfo messageBundleElementInfo = new MessageBundleElementInfo(methodInfo, enumKey);

        if (validateMethod(messageBundleElementInfo)) {
            return messageBundleElementInfo;
        }

        return null;
    }

    protected boolean validateMethod(MessageBundleElementInfo messageBundleElementInfo) {
        String returnTypeName = messageBundleElementInfo.getElement().getReturnType().toString();
        String expectedReturnTypeName = String.class.getName();

        if (!messageBundleElementInfo.getName().startsWith("get") || !returnTypeName.equals(expectedReturnTypeName)) {
            String msg = "Only getter methods with the return type '" + expectedReturnTypeName + "' are allowed!";
            processingEnv.getMessager().printMessage(Kind.ERROR, msg, messageBundleElementInfo.getElement());
            return false;
        }

        return true;
    }

    protected abstract String getEnumKey(InterfaceMethodInfo methodInfo);

    @Override
    protected void processInterfaceInfo(MessageBundleInfo2 messageBundleInfo) {
        File enumClassJavaFile = getJavaSourceFile(messageBundleInfo.getPackageName(), messageBundleInfo.getSimpleEnumClassName());

        if (enumClassJavaFile.lastModified() == messageBundleInfo.getLastModified()) {
            // Skip unchanged files
            return;
        }

        Writer writer = null;

        try {
            JavaFileObject jfo = processingEnv.getFiler().createSourceFile(messageBundleInfo.getQualifiedEnumClassName());
            writer = jfo.openWriter();
            generateEnumClass(messageBundleInfo, writer);
        } catch (Exception ex) {
            String msg = "Error while generating enum class!";
            printMessage(Kind.ERROR, msg, messageBundleInfo.getElement(), ex);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

        enumClassJavaFile.setLastModified(messageBundleInfo.getLastModified());
    }

    protected void generateEnumClass(MessageBundleInfo2 info, Writer writer) throws Exception {
        Map<String, Object> parameters = getTemplateParameters(info);
        Template template = getTemplate(info);
        template.process(parameters, writer);
    }

    protected Template getTemplate(MessageBundleInfo2 info) throws IOException {
        final Configuration configuration = new Configuration();
        configuration.setTemplateLoader(new ClassTemplateLoader(MessageBundleProcessor.class, "/"));
        configuration.setAutoFlush(true);
        configuration.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
        return configuration.getTemplate(info.getTemplateLocation());
    }

    protected Map<String, Object> getTemplateParameters(MessageBundleInfo2 info) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("packageName", info.getPackageName());
        parameters.put("baseName", new StringBuilder(info.getPropertiesBasePath()).append('/').append(info.getPropertiesBaseName()));
        parameters.put("enumName", info.getSimpleEnumClassName());

        List<String> locales = new ArrayList<String>(info.getLocales().size());
        for (Locale locale : info.getLocales()) {
            locales.add(locale.toString());
        }
        Collections.sort(locales);
        parameters.put("locales", locales);

        List<String> keys = new ArrayList<String>(info.getInterfaceMethodInfos().size());
        for (MessageBundleElementInfo elementInfo : info.getInterfaceMethodInfos()) {
            keys.add(elementInfo.getEnumKey());
        }
        Collections.sort(keys);
        parameters.put("keys", keys);
        return parameters;
    }

    protected void validatePropertiesFiles(MessageBundleInfo2 messageBundleInfo) {
        for (Locale locale : messageBundleInfo.getLocales()) {
            String propertiesFileName = getPropertiesFileName(messageBundleInfo.getPropertiesBaseName(), locale);

            try {
                File propertiesFile = getResourceFile(messageBundleInfo.getPropertiesBasePath(), propertiesFileName);
                Properties properties = new Properties();
                properties.load(new InputStreamReader(new FileInputStream(propertiesFile), "UTF-8"));

                for (MessageBundleElementInfo elementInfo : messageBundleInfo.getInterfaceMethodInfos()) {
                    String enumKey = elementInfo.getEnumKey();
                    String propertiesValue = properties.remove(enumKey).toString();

                    if (propertiesValue == null) {
                        String msg = "The entry for the enum key '" + enumKey + "' is missing in the properties file '" + propertiesFileName + "'!";
                        processingEnv.getMessager().printMessage(Kind.ERROR, msg, elementInfo.getElement());
                    } else {
                        validatePropertiesFileEntry(elementInfo, locale, propertiesValue);
                    }
                }

                for (Object propertiesKey : properties.keySet()) {
                    String msg = "The entry '" + propertiesKey + "' in the properties file '" + propertiesFileName + "' has no corresponding enum key!";
                    processingEnv.getMessager().printMessage(Kind.ERROR, msg, messageBundleInfo.getElement());
                }
            } catch (Exception ex) {
                printMessage(Kind.ERROR, "Error while loading properties files.", messageBundleInfo.getElement(), ex);
            }
        }
    }

    protected void validatePropertiesFileEntry(MessageBundleElementInfo elementInfo, Locale locale, String propertiesValue) {
        int methodParameterCount = elementInfo.getQualifiedParameterTypeNames().size();
        int propertiesValueParameterCount = getParameterCount(propertiesValue);

        if (methodParameterCount != propertiesValueParameterCount) {
            String msg = "The method accepts " + methodParameterCount + " parameters, but the properties entry '"
                    + elementInfo.getEnumKey() + "' for the locale '" + locale.toString() + "' requires " + propertiesValueParameterCount + " parameters!";
            processingEnv.getMessager().printMessage(Kind.ERROR, msg, elementInfo.getElement());
        }
    }

    private static final Pattern PROPERTY_VALUE_PARAMETER_PATTERN = Pattern.compile("\\{(.*?)\\}");

    private static int getParameterCount(final String value) {
        int count = 0;
        if (value != null) {
            final Matcher matcher = PROPERTY_VALUE_PARAMETER_PATTERN.matcher(value);
            while (matcher.find()) {
                count++;
            }
        }
        return count;
    }
}
