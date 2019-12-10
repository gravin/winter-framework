package org.winterframework.beans.factory;

public class BeanWrapperImpl implements BeanWrapper {
    private Object wrappedInstance;

    public BeanWrapperImpl(Object object) {
        setWrappedInstance(object);
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    public void setWrappedInstance(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }
}
