package org.winterframework.beans.factory;


public interface BeanDefinition {
    ConstructorArgumentValues getConstructorArgumentValues();
    MutablePropertyValues getPropertyValues();
    String getParentName();
    void setParentName(String parentName);
    String getScope();
    void setScope(String scope);
    boolean isSingleton();
    boolean isPrototype();
}
