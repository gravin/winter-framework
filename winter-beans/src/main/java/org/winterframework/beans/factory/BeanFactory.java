package org.winterframework.beans.factory;

public interface BeanFactory {

    Object getBean(String name);

    boolean containsBean(String name);

    String[] getAliases(String name);
}
