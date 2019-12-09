package org.winterframework.beans.factory;

import org.springframework.core.io.Resource;

public class XmlBeanFactory extends DefaultListableBeanFactory {

    private final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this);

    public XmlBeanFactory(Resource resource)  {
        this(resource, null);
    }

    public XmlBeanFactory(Resource resource, BeanFactory parentBeanFactory)  {
        this.reader.loadBeanDefinitions(resource);
    }
}
