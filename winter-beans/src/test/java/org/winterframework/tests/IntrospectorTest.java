package org.winterframework.tests;


import com.alibaba.fastjson.JSON;
import com.sun.beans.finder.MethodFinder;
import org.apache.commons.collections.CollectionUtils;
import sun.reflect.misc.ReflectUtil;

import java.beans.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class IntrospectorTest {


    public static class Person {


        private String name;

        private int age;


        public int getAge() {
            return age;
        }


        public void setAge(int age) {
            this.age = age;
        }


        public String getName() {
            return name;
        }


        public void setName(String name) {
            this.name = name;
        }

    }

    public static void main(String[] args) throws IntrospectionException {
        Method[] publicDeclaredMethods = getPublicDeclaredMethods(Test2.class);
        if (publicDeclaredMethods == null || publicDeclaredMethods.length == 0) {
            System.out.println("none");
        } else {
            System.out.println(Arrays.stream(publicDeclaredMethods).map(m -> m.getName()).collect(Collectors.joining(",")));
        }
        System.exit(0);
        // TODO Auto-generated methodstub
        BeanInfo beanInfo = Introspector.getBeanInfo(Person.class, Introspector.IGNORE_ALL_BEANINFO);
        System.out.println(JSON.toJSONString(beanInfo));

        System.out.println("BeanDescriptor===========================================");
        BeanDescriptor beanDesc = beanInfo.getBeanDescriptor();
        Class cls = beanDesc.getBeanClass();
        System.out.println(cls.getName());

        System.out.println("MethodDescriptor===========================================");
        MethodDescriptor[] methodDescs = beanInfo.getMethodDescriptors();
        for (int i = 0; i < methodDescs.length; i++) {
            Method method = methodDescs[i].getMethod();
            System.out.println(method.getName());
        }

        System.out.println("PropertyDescriptor===========================================");
        PropertyDescriptor[] propDescs = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propDescs.length; i++) {
            Method methodR = propDescs[i].getReadMethod();
            if (methodR != null) {
                System.out.println(methodR.getName());
            }
            Method methodW = propDescs[i].getWriteMethod();
            if (methodW != null) {
                System.out.println(methodW.getName());
            }
        }
    }

    private static Method[] getPublicDeclaredMethods(Class<?> clz) {
        // Looking up Class.getDeclaredMethods is relatively expensive,
        // so we cache the results.
        if (!ReflectUtil.isPackageAccessible(clz)) {
            return new Method[0];
        }

        Method[] result = clz.getMethods();
        for (int i = 0; i < result.length; i++) {
            Method method = result[i];
            if (!method.getDeclaringClass().equals(clz)) {
                result[i] = null; // ignore methods declared elsewhere
            } else {
                try {
                    method = MethodFinder.findAccessibleMethod(method);
                    Class<?> type = method.getDeclaringClass();
                    result[i] = type.equals(clz) || type.isInterface()
                            ? method
                            : null; // ignore methods from superclasses
                } catch (NoSuchMethodException exception) {
                    // commented out because of 6976577
                    // result[i] = null; // ignore inaccessible methods
                }
            }
        }
        return result;
    }
}
