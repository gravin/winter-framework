package org.winterframework.beans.factory;


import org.springframework.core.io.Resource;

public interface BeanDefinitionReader {

    BeanDefinitionRegistry getRegistry();

    int loadBeanDefinitions(Resource resource);

}
