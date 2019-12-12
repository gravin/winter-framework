package org.winterframework.beans.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.winterframework.beans.BeansException;
import org.winterframework.beans.CachedIntrospectionResults;
import org.winterframework.beans.InvalidPropertyException;
import org.winterframework.beans.PropertyAccessorUtils;
import org.winterframework.util.Assert;

import java.beans.PropertyDescriptor;
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
        return nestedBw.getCachedIntrospectionResults().getPropertyDescriptor(propertyName);
    }


    private CachedIntrospectionResults getCachedIntrospectionResults() {
        Assert.state(this.wrappedInstance != null, "BeanWrapper does not hold a bean instance");
        if (this.cachedIntrospectionResults == null) {
            this.cachedIntrospectionResults = CachedIntrospectionResults.forClass(getWrappedClass());
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
}
