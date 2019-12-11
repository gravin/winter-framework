package org.winterframework.beans.factory;

public interface BeanWrapper extends ConfigurablePropertyAccessor {

    public Object getWrappedInstance();

    public void setWrappedInstance(Object wrappedInstance);
}
