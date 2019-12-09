package org.winterframework.beans.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public interface InstantiationStrategy {


    Object instantiate(RootBeanDefinition beanDefinition, String beanName, BeanFactory owner);


    Object instantiate(RootBeanDefinition beanDefinition, String beanName, BeanFactory owner,
                       Constructor<?> ctor, Object[] args) ;

    Object instantiate(RootBeanDefinition beanDefinition, String beanName, BeanFactory owner,
                       Object factoryBean, Method factoryMethod, Object[] args) ;

}
