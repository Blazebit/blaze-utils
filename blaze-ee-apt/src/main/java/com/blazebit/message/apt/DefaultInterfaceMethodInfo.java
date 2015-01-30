package com.blazebit.message.apt;

import java.util.List;

import javax.lang.model.element.ExecutableElement;

public class DefaultInterfaceMethodInfo implements InterfaceMethodInfo {
    
    private final ExecutableElement element;
	private final String name;
	private final String qualifiedReturnTypeName;
	private final List<String> qualifiedParameterTypeNames;
    
	public DefaultInterfaceMethodInfo(ExecutableElement element, String name, String qualifiedReturnTypeName, List<String> qualifiedParameterTypeNames) {
        this.element = element;
	    this.name = name;
        this.qualifiedReturnTypeName = qualifiedReturnTypeName;
        this.qualifiedParameterTypeNames = qualifiedParameterTypeNames;
    }

    @Override
    public ExecutableElement getElement() {
        return element;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getQualifiedReturnTypeName() {
        return qualifiedReturnTypeName;
    }

    @Override
    public List<String> getQualifiedParameterTypeNames() {
        return qualifiedParameterTypeNames;
    }
}
