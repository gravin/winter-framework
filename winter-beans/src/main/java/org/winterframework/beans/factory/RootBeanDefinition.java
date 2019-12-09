package org.winterframework.beans.factory;

public class RootBeanDefinition extends AbstractBeanDefinition {
    public String getParentName() {
        return null;
    }

    @Override
    public void setParentName(String parentName) {
        if (parentName != null) {
            throw new IllegalArgumentException("Root bean cannot be changed into a child bean with parent reference");
        }
    }

    RootBeanDefinition(BeanDefinition original) {
        super(original);
    }
}
