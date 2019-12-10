package org.winterframework.beans.factory;


import org.winterframework.core.io.Resource;

public interface BeanDefinitionReader {

    BeanDefinitionRegistry getRegistry();

    int loadBeanDefinitions(Resource resource);

}
