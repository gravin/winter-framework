package org.winterframework.tests;

public class Test1 implements ITest {
    public static void main(String[] args) {
        System.out.println(Boolean.class.getName());
    }

    @Override
    public void sayHello(String name) {
        System.out.println("hello test1 " + name);
    }
}
