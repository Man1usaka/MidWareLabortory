package com.lab.yao.client;

import com.lab.yao.service.FileService;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * @author Y Jiang
 * @date  2019/3/17
 */
public class Test {
        public static void main(String[] args) {
            //测试常规服务
            ClassPathXmlApplicationContext context =
                    new ClassPathXmlApplicationContext("consumer.xml");
            context.start();
            System.out.println("consumer start");
            FileService demoService = context.getBean(FileService.class);
            System.out.println("consumer");
            System.out.println(demoService.countFile());
            context.close();
        }
}
