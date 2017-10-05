package com.blazebit.message.apt;

import javax.lang.model.element.ExecutableElement;
import java.util.List;

public interface InterfaceMethodInfo {

    public ExecutableElement getElement();

    public String getName();

    public String getQualifiedReturnTypeName();

    public List<String> getQualifiedParameterTypeNames();
}
