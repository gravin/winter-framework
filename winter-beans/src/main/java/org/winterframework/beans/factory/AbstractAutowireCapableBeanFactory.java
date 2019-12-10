package org.winterframework.beans.factory;



public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory
        implements AutowireCapableBeanFactory {

    private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

    @Override
    protected Object createBean(String beanName, RootBeanDefinition mbd, Object[] args) {
        Object beanInstance = doCreateBean(beanName, mbd, args);
        return beanInstance;
    }

    protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final Object[] args) {
// Instantiate the bean.
        BeanWrapper instanceWrapper = null;

        if (instanceWrapper == null) {
            instanceWrapper = createBeanInstance(beanName, mbd, args);
        }
        final Object bean = (instanceWrapper != null ? instanceWrapper.getWrappedInstance() : null);

        // Initialize the bean instance.
        Object exposedObject = bean;

        return exposedObject;
    }


    protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, Object[] args) {
        // Make sure bean class is actually resolved at this point.
        Class<?> beanClass = mbd.getBeanClass();

        // No special handling: simply use no-arg constructor.
        return instantiateBean(beanName, mbd);
    }

    protected BeanWrapper instantiateBean(final String beanName, final RootBeanDefinition mbd) {
        try {
            Object beanInstance;
            final BeanFactory parent = this;

            beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, parent);

            BeanWrapper bw = new BeanWrapperImpl(beanInstance);
            return bw;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    protected InstantiationStrategy getInstantiationStrategy() {
        return this.instantiationStrategy;
    }

}
