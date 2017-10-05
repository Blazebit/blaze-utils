package com.blazebit.message.apt;


public class MessageBundleElementInfo extends DefaultInterfaceMethodInfo {

    private final String enumKey;

    public MessageBundleElementInfo(InterfaceMethodInfo parent, String enumKey) {
        super(parent.getElement(), parent.getName(), parent.getQualifiedReturnTypeName(), parent.getQualifiedParameterTypeNames());
        this.enumKey = enumKey;
    }

    public String getEnumKey() {
        return enumKey;
    }
}
