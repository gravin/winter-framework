package org.winterframework.beans.factory;

public class GenericBeanDefinition extends AbstractBeanDefinition {

    private String parentName;

    protected GenericBeanDefinition(BeanDefinition original) {
        super(original);
    }

    public GenericBeanDefinition() {
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
