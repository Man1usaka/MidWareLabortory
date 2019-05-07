package com.lab.yao.serviceimpl;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * @author Y Jiang
 * @date  2019/3/17
 */
public class Provider {
    public static void main(String[] argv) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("provider.xml");
        System.out.println(context.getDisplayName()+" :Here");
        context.start();
        System.out.println("Service Start");
        System.in.read();
    }
}
