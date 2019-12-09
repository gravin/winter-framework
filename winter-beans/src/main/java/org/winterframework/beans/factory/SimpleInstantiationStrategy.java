package org.winterframework.beans.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class SimpleInstantiationStrategy implements InstantiationStrategy {
    @Override
    public Object instantiate(RootBeanDefinition beanDefinition, String beanName, BeanFactory owner) {

        Constructor<?> constructorToUse = null;

        final Class<?> clazz = beanDefinition.getBeanClass();
        if (clazz.isInterface()) {
            throw new RuntimeException("Specified class is an interface");
        }
        try {
            constructorToUse = clazz.getDeclaredConstructor((Class[]) null);
        } catch (Exception ex) {
            throw new RuntimeException("No default constructor found", ex);
        }

        Constructor<?> ctor = constructorToUse;
        try {
            if ((!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers()))
                    && !ctor.isAccessible()) {
                ctor.setAccessible(true);
            }
            return ctor.newInstance();
        }catch (Throwable t){
            throw new RuntimeException("constructor exception");
        }
    }

    @Override
    public Object instantiate(RootBeanDefinition beanDefinition, String beanName, BeanFactory owner, Constructor<?> ctor, Object[] args) {
        return null;
    }

    @Override
    public Object instantiate(RootBeanDefinition beanDefinition, String beanName, BeanFactory owner, Object factoryBean, Method factoryMethod, Object[] args) {
        return null;
    }
}
