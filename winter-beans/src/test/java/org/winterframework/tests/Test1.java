package org.winterframework.tests;

public abstract class Test1 implements ITest {
    public static void main(String[] args) {
        System.out.println(Boolean.class.getName());
    }

    @Override
    public void sayHello(String name) {
        System.out.println("hello test1 " + name);
    }


    public void sayHello(String name, String title) {
        System.out.println("hello test1 " + title + " " + name);
    }

    @Override
    public void pay(long m) {
        doPay(m);
    }

    protected abstract void doPay(long m);
}
