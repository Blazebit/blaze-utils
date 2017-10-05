package com.blazebit.message.apt;

import javax.lang.model.element.TypeElement;
import java.util.Collection;


public class DefaultInterfaceInfo<T extends InterfaceMethodInfo> implements InterfaceInfo<T> {

    private final TypeElement element;
    private final String qualifiedName;
    private final String packageName;
    private final String simpleName;
    private final String absolutePath;
    private final long lastModified;
    private final Collection<T> interfaceMethodInfos;

    public DefaultInterfaceInfo(TypeElement element, String qualifiedName, String packageName, String simpleName, String absolutePath, long lastModified, Collection<T> interfaceMethodInfos) {
        this.element = element;
        this.qualifiedName = qualifiedName;
        this.packageName = packageName;
        this.simpleName = simpleName;
        this.absolutePath = absolutePath;
        this.lastModified = lastModified;
        this.interfaceMethodInfos = interfaceMethodInfos;
    }

    @Override
    public TypeElement getElement() {
        return element;
    }

    @Override
    public String getQualifiedName() {
        return qualifiedName;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String getSimpleName() {
        return simpleName;
    }

    @Override
    public String getAbsolutePath() {
        return absolutePath;
    }

    @Override
    public long getLastModified() {
        return lastModified;
    }

    @Override
    public Collection<T> getInterfaceMethodInfos() {
        return interfaceMethodInfos;
    }
}
