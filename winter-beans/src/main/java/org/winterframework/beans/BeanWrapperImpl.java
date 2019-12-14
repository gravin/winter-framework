package org.winterframework.beans;

import com.sun.corba.se.impl.io.TypeMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.winterframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;

public class BeanWrapperImpl implements BeanWrapper {

    private static final Logger logger = LoggerFactory.getLogger(BeanWrapperImpl.class);

    /**
     * The wrapped object
     */
    private Object object;


    /**
     * Cached introspections results for this object, to prevent encountering
     * the cost of JavaBeans introspection every time.
     */
    private CachedIntrospectionResults cachedIntrospectionResults;


    public BeanWrapperImpl(Object object) {
        setWrappedInstance(object);
    }

    public Object getWrappedInstance() {
        return object;
    }

    public final Class<?> getWrappedClass() {
        return (this.object != null ? this.object.getClass() : null);
    }

    public void setWrappedInstance(Object object) {
        this.object = object;
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
        return nestedBw.getCachedIntrospectionResults().getPropertyDescriptor(propertyName);
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

    private CachedIntrospectionResults getCachedIntrospectionResults() {
        Assert.state(this.object != null, "BeanWrapper does not hold a bean instance");
        if (this.cachedIntrospectionResults == null) {
            this.cachedIntrospectionResults = CachedIntrospectionResults.forClass(getWrappedClass());
        }
        return this.cachedIntrospectionResults;
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

    public Object convertForProperty(Object value, String propertyName) throws TypeMismatchException {
        PropertyDescriptor pd = getCachedIntrospectionResults().getPropertyDescriptor(propertyName);
        if (pd == null) {
            throw new InvalidPropertyException(value.getClass(), propertyName,
                    "No property '" + propertyName + "' found");
        }
        return convertForProperty(propertyName, null, value, new TypeDescriptor(property(pd)));
    }

    private Object convertForProperty(String propertyName, Object oldValue, Object newValue, TypeDescriptor td)
            throws TypeMismatchException {

        return convertIfNecessary(propertyName, oldValue, newValue, td.getType(), td);
    }
}
