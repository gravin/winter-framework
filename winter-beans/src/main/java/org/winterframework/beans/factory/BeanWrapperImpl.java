package org.winterframework.beans.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.winterframework.beans.*;
import org.winterframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;

public class BeanWrapperImpl implements BeanWrapper {

    private static final Logger logger = LoggerFactory.getLogger(BeanWrapperImpl.class);

    private Object wrappedInstance;


    /**
     * Cached introspections results for this object, to prevent encountering
     * the cost of JavaBeans introspection every time.
     */
    private CachedIntrospectionResults cachedIntrospectionResults;


    public BeanWrapperImpl(Object object) {
        setWrappedInstance(object);
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    public void setWrappedInstance(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }


    @Override
    public Object getPropertyValue(String propertyName) {
        return null;
    }

    @Override
    public void setPropertyValue(String propertyName, Object value) {

    }

    @Override
    public void setPropertyValue(PropertyValue pv) {

    }

    @Override
    public void setPropertyValues(PropertyValues pvs) {

    }

    public boolean isWritableProperty(String propertyName) {
        try {
            PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
            if (pd != null) {
                if (pd.getWriteMethod() != null) {
                    return true;
                }
            } else {
                // Maybe an indexed/mapped property...
                getPropertyValue(propertyName);
                return true;
            }
        } catch (InvalidPropertyException ex) {
            // Cannot be evaluated, so can't be writable.
        }
        return false;
    }


    protected PropertyDescriptor getPropertyDescriptorInternal(String propertyName) throws BeansException {
        Assert.notNull(propertyName, "Property name must not be null");
        BeanWrapperImpl nestedBw = getBeanWrapperForPropertyPath(propertyName);
        return null;
//        return nestedBw.getCachedIntrospectionResults().getPropertyDescriptor(propertyName);
    }


    private CachedIntrospectionResults getCachedIntrospectionResults() {
        Assert.state(this.wrappedInstance != null, "BeanWrapper does not hold a bean instance");
        if (this.cachedIntrospectionResults == null) {
//            this.cachedIntrospectionResults = CachedIntrospectionResults.forClass(getWrappedClass());
        }
        return this.cachedIntrospectionResults;
    }

    protected BeanWrapperImpl getBeanWrapperForPropertyPath(String propertyPath) {
        int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(propertyPath);
        // Handle nested properties recursively.
        if (pos > -1) {
            throw new RuntimeException("currently not support nested property");
        } else {
            return this;
        }
    }

    @Override
    public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {

    }

    @Override
    public void registerCustomEditor(Class<?> requiredType, String propertyPath, PropertyEditor propertyEditor) {

    }

    @Override
    public PropertyEditor findCustomEditor(Class<?> requiredType, String propertyPath) {
        return null;
    }

    @Override
    public <T> T convertIfNecessary(Object value, Class<T> requiredType) {
        return null;
    }
}
