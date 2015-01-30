package com.blazebit.message.apt;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

public class MessageBundleInfo2 extends DefaultInterfaceInfo<MessageBundleElementInfo> {
    
    private final String qualifiedEnumClassName;
    private final String simpleEnumClassName;
    private final String propertiesBasePath;
    private final String propertiesBaseName;
    private final String templateLocation;
	private final Collection<Locale> locales;

    public MessageBundleInfo2(InterfaceInfo<MessageBundleElementInfo> parent, String qualifiedEnumClassName, String simpleEnumClassName, String propertiesBasePath, String propertiesBaseName, String templateLocation, Collection<Locale> locales) {
        super(parent.getElement(), parent.getQualifiedName(), parent.getPackageName(), parent.getSimpleName(), parent.getAbsolutePath(), parent.getLastModified(), parent.getInterfaceMethodInfos());
        this.qualifiedEnumClassName = qualifiedEnumClassName;
        this.simpleEnumClassName = simpleEnumClassName;
        this.propertiesBasePath = propertiesBasePath;
        this.propertiesBaseName = propertiesBaseName;
        this.templateLocation = templateLocation;
        this.locales = locales;
    }

    public String getQualifiedEnumClassName() {
        return qualifiedEnumClassName;
    }

    public String getSimpleEnumClassName() {
        return simpleEnumClassName;
    }
    
    public String getPropertiesBasePath() {
        return propertiesBasePath;
    }
    
    public String getPropertiesBaseName() {
        return propertiesBaseName;
    }

    public String getTemplateLocation() {
        return templateLocation;
    }

    public Collection<Locale> getLocales() {
        return locales;
    }
}
