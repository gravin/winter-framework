package org.winterframework.beans.factory;

public class TypedStringValue {
    private String value;

    private volatile Object targetType;

    private Object source;

    private String specifiedTypeName;

    private volatile boolean dynamic;

    public TypedStringValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Object getTargetType() {
        return targetType;
    }

    public void setTargetType(Object targetType) {
        this.targetType = targetType;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public String getSpecifiedTypeName() {
        return specifiedTypeName;
    }

    public void setSpecifiedTypeName(String specifiedTypeName) {
        this.specifiedTypeName = specifiedTypeName;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }
}
