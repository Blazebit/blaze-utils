package com.blazebit.message.apt;

import java.util.List;

import javax.lang.model.element.ExecutableElement;

public interface InterfaceMethodInfo {
    
    public ExecutableElement getElement();
    
    public String getName();
    
    public String getQualifiedReturnTypeName();
    
    public List<String> getQualifiedParameterTypeNames();
}
