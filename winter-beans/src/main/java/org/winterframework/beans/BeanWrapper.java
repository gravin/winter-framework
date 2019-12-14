package org.winterframework.beans;

import org.winterframework.beans.factory.ConfigurablePropertyAccessor;

import java.beans.PropertyDescriptor;

public interface BeanWrapper extends ConfigurablePropertyAccessor {

    Object getWrappedInstance();

    void setWrappedInstance(Object wrappedInstance);

    PropertyDescriptor getPropertyDescriptor(String propertyName) throws InvalidPropertyException;
}
