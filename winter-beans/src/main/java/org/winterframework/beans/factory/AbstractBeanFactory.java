package org.winterframework.beans.factory;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {

    private TypeConverter typeConverter;

    protected TypeConverter getCustomTypeConverter() {
        return this.typeConverter;
    }

    private final Map<String, RootBeanDefinition> mergedBeanDefinitions =
            new ConcurrentHashMap<String, RootBeanDefinition>(64);

    @Override
    public Object getBean(String name) {
        return doGetBean(name, null, null, false);
    }

    protected <T> T doGetBean(
            final String name, final Class<T> requiredType, final Object[] args, boolean typeCheckOnly) {
        final String beanName = name;
        Object bean = null;

        Object sharedInstance = getSingleton(beanName);
        if (sharedInstance != null && args == null) {
            bean = sharedInstance;
        } else {

            try {
                final RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);

                if (mbd.isSingleton()) {
                    sharedInstance = getSingleton(beanName, new ObjectFactory<Object>() {
                        public Object getObject() {
                            try {
                                return createBean(beanName, mbd, args);
                            } catch (RuntimeException ex) {
                                destroySingleton(beanName);
                                throw ex;
                            }
                        }
                    });
                    bean = sharedInstance;
                } else if (mbd.isPrototype()) {

                } else {

                }
            } catch (RuntimeException ex) {
                throw ex;
            }
        }

        return (T) bean;
    }

    @Override
    public boolean containsBean(String name) {
        return false;
    }

    @Override
    public String[] getAliases(String name) {
        return new String[0];
    }

    protected RootBeanDefinition getMergedLocalBeanDefinition(String beanName) {
        // Quick check on the concurrent map first, with minimal locking.
        RootBeanDefinition mbd = this.mergedBeanDefinitions.get(beanName);
        if (mbd != null) {
            return mbd;
        }
        return getMergedBeanDefinition(beanName, getBeanDefinition(beanName));
    }

    protected RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd) {
        synchronized (this.mergedBeanDefinitions) {
            RootBeanDefinition mbd = null;

            if (mbd == null) {
                if (bd.getParentName() == null) {
                    mbd = new RootBeanDefinition(bd);
                }

                // Set default singleton scope, if not configured before.
                if (StringUtils.isBlank(mbd.getScope())) {
                    mbd.setScope("singleton");
                }

                this.mergedBeanDefinitions.put(beanName, mbd);
            }

            return mbd;
        }
    }

    protected abstract BeanDefinition getBeanDefinition(String beanName);

    protected abstract Object createBean(String beanName, RootBeanDefinition mbd, Object[] args);
}
