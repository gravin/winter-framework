package org.winterframework.beans.factory;

import com.codeanalysis.MyTestBean;
import org.springframework.core.io.ClassPathResource;

public class Application {
    public static void main(String[] args) {
        BeanFactory bf= new XmlBeanFactory(new ClassPathResource("spring/beanFactoryTest.xml"));
        MyTestBean bean= (MyTestBean) bf.getBean("myTestBean");
        System.out.println(bean.getTestStr());
    }
}
