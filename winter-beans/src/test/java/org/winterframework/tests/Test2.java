package org.winterframework.tests;

public class Test2 extends Test1 {


    @Override
    public void sayHello(String name) {
        System.out.println("hello test2 " + name);
    }

    @Override
    protected void doPay(long m) {

    }
}
