package com.blazebit.message.apt;

import java.util.Collection;

import javax.lang.model.element.TypeElement;

public interface InterfaceInfo<T extends InterfaceMethodInfo> { 
    
    public TypeElement getElement();
    
    public String getQualifiedName();
    
    public String getPackageName();
    
    public String getSimpleName();
    
    public String getAbsolutePath();
    
    public long getLastModified();
    
    public Collection<T> getInterfaceMethodInfos();
}
