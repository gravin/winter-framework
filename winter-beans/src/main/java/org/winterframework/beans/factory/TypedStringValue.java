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

    public boolean hasTargetType() {
        return (this.targetType instanceof Class);
    }

    public Class<?> getTargetType() {
        Object targetTypeValue = this.targetType;
        if (!(targetTypeValue instanceof Class)) {
            throw new IllegalStateException("Typed String value does not carry a resolved target type");
        }
        return (Class) targetTypeValue;
    }

    public void setTargetType(Object targetType) {
        this.targetType = targetType;
    }

    public Class<?> resolveTargetType() throws ClassNotFoundException {
        if (this.targetType == null) {
            return null;
        }
        Class<?> resolvedClass = Class.forName(getTargetTypeName());
        this.targetType = resolvedClass;
        return resolvedClass;
    }

    public String getTargetTypeName() {
        Object targetTypeValue = this.targetType;
        if (targetTypeValue instanceof Class) {
            return ((Class) targetTypeValue).getName();
        }
        else {
            return (String) targetTypeValue;
        }
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
        return this.dynamic;
    }

    public void setDynamic() {
        this.dynamic = true;
    }
}
