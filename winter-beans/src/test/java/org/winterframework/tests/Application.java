package org.winterframework.tests;


import org.winterframework.beans.factory.BeanFactory;
import org.winterframework.beans.factory.XmlBeanFactory;
import org.winterframework.core.io.ClassPathResource;

public class Application {
    public static void main(String[] args) {
        BeanFactory bf= new XmlBeanFactory(new ClassPathResource("spring/beanFactoryTest.xml"));
        MyTestBean bean= (MyTestBean) bf.getBean("myTestBean");
        System.out.println(bean.getTestStr());
    }
}
