package com.study.yao.aop;

import com.study.yao.model.Alumni;
import com.study.yao.model.User;
import com.study.yao.service.AlumniService;
import org.apache.log4j.Category;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

/**
 *  @author Y Jiang
 *  Web层日志切面
 *
 *  对于所有的Alumni表的查询操作，
 *  记录各个操作的时间、用户，读取内容，存入ReadLog表格中
 *
 *  对于所有的Alumni表的更新（更新和删除）操作，
 *  记录各个操作的时间、用户，修改的新值和旧值，存入UpdateLog表格中（删除的新值为null）
 *
 */
@Aspect
@Component
public class WebLogAspect{

    @Autowired
    AlumniService alumniService;

    private Category readLogger = Category.getInstance("SelectCategory");
    private Category updateLogger = Category.getInstance("ModifyCategory");
    //private Category logger3 =  Category.class.getDeclaredConstructor(String.class).newInstance("MyCategory");

    ThreadLocal<Long> startTime = new ThreadLocal<>();


    @Pointcut("execution(* com.study.yao.controller.AlumniController.getAlumniList(..))")
    public void read(){}

    @Pointcut("execution(* com.study.yao.controller.AlumniController.deleteAlumni(..))" +
            "|| execution(* com.study.yao.controller.AlumniController.putAlumni(..))"+
            "|| execution(* com.study.yao.controller.AlumniController.postAlumni(..))")
    public void modify(){}

    @Around("read()")
    public Object aroundRead(ProceedingJoinPoint joinpoint) throws  Throwable{
        StringBuilder message = new StringBuilder();
        Object result=null;
        try{
            //记录各个操作的用户
            long start = System.currentTimeMillis();
            message.append("用户: "+new User().hashCode()+"\n");

            //记录读取内容
            result =  joinpoint.proceed();
            message.append("查询结果: "+result+"\n");

            //记录各个操作的时间
            message.append("操作时间: "+(System.currentTimeMillis()-start)+"毫秒"+"\n");

            readLogger.info(message);

        }catch (Throwable t) {
            System.out.println("ReadLog出现错误");
        }
        return result;
    }

    @Around("modify()")
    public Object aroundModify(ProceedingJoinPoint joinpoint) throws  Throwable{
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        StringBuilder message = new StringBuilder();
        Object result=null;
        try{
            //记录各个操作的用户
            long start = System.currentTimeMillis();
            message.append("用户: "+new User().hashCode()+"\n");

            //获得修改前的值
            Alumni a;
            try{
                a = (Alumni)joinpoint.getArgs()[0];
                a = alumniService.findById(a.getId());
            }catch (Exception e){
                a = null;
            }
            message.append("修改旧值: "+a+"\n");

            result = joinpoint.proceed();

            try {
                result = alumniService.findById(((Alumni) result).getId());
            }catch (Exception e){
                result = null;
            }
            message.append("修改新值: "+result+"\n");

            //记录各个操作的时间
            message.append("操作时间: "+(System.currentTimeMillis()-start)+"毫秒"+"\n");

            updateLogger.info(message);

        }catch (Throwable t) {
            t.printStackTrace();
            System.out.println("UpdateLog 出现错误");
        }
        return result;
    }

}


