package com.lab.yao.client;

import com.lab.yao.service.FileService;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 * @author Y Jiang
 * @date  2019/3/17
 */
public class Consumer<T> {
    private T t;

    Consumer(String name){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("consumer.xml");
        context.start();
        try{
        t = (T)context.getBean(FileService.class);}
        catch (Exception e){
            System.out.println("Bond Service Error");
            e.printStackTrace();
        }
    }

    public T getT() {
        return t;
    }
}
