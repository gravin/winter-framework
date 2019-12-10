package org.winterframework.beans.factory;


import org.winterframework.util.Assert;

public class RuntimeBeanReference implements BeanReference {

    private final String beanName;

    private final boolean toParent;

    private Object source;

    public RuntimeBeanReference(String beanName) {
        this(beanName, false);
    }

    public RuntimeBeanReference(String beanName, boolean toParent) {
        Assert.hasText(beanName, "'beanName' must not be empty");
        this.beanName = beanName;
        this.toParent = toParent;
    }

    @Override
    public String getBeanName() {
        return beanName;
    }
}
