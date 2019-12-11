package org.winterframework.beans.factory;


public interface PropertyValues {

    PropertyValue[] getPropertyValues();

    PropertyValue getPropertyValue(String propertyName);

    boolean contains(String propertyName);

    boolean isEmpty();
}
