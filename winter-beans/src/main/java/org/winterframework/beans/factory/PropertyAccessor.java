package org.winterframework.beans.factory;

public interface PropertyAccessor {

    Object getPropertyValue(String propertyName);
    void setPropertyValue(String propertyName, Object value);
    void setPropertyValue(PropertyValue pv);
    void setPropertyValues(PropertyValues pvs);

    boolean isWritableProperty(String propertyName);
}
