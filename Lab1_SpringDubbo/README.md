##中间件技术实验：远程过程调用 ##

###一、引言###

- **实验目的**

&emsp;&emsp;掌握远程过程调用原理，基于java RMI进行远程编程和控制。要求定义远程接口类及实现类：定义相应的处理方法；客户端利用RMI实现远程调用服务。同时，在在两台机器之间验证结果正确

- **实验内容**

&emsp;&emsp;利用RMI技术对远程文件夹进行控制：可以增加文件（文本文件）、修改文件（文本文件）、删除文件、列出文件；统计该文件夹具有多少个文件、占用磁盘空间的总大小。

- **扩展内容**

&emsp;&emsp;Dubbo是 阿里巴巴公司开源的一个高性能优秀的服务框架，使得应用可通过高性能的 RPC 实现服务的输出和输入功能，可以和Spring框架无缝集成。它提供了三大核心能力：面向接口的远程方法调用，智能容错和负载均衡，以及服务自动注册和发现。用dubbo实现以上功能。

###二、相关环境###
- 操作系统：Windows10 X64
- IDE:     IDEA 2018.2.6
- 其他：Zookeeper、Dubbo、Maven、Spring

###三、具体过程###
#### 1.RMI实现####

- **项目结构**
>**├── RMI_File**  
 │&ensp;├── pom.xml  
 **├── FileClient**  
 │&ensp;└── src  
 │&ensp;&ensp;└── main  
 │&ensp;&emsp;&emsp;└── java  
 │&ensp;&emsp;&emsp;&emsp;└── com.lab.yao.fileclient  
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── Application  
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── RmiConnection  
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── FileController  
 **├── FileServer**  
 │&ensp;└── src  
 │&ensp;&ensp;└── main  
 │&ensp;&emsp;&emsp;└── java  
 │&ensp;&emsp;&emsp;&emsp;└── com.lab.yao.fileserver  
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── FileServiceImpl  
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── FileServiceMain  
 **├── Service**  
 │&ensp;└── src  
 │&ensp;&ensp;└── main  
 │&ensp;&emsp;&emsp;└── java  
 │&ensp;&emsp;&emsp;&emsp;└── com.lab.yao.fileservice  
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── FileService  

- **文件操作服务的接口定义**
 
  第一步就是建立和编译服务接口的Java代码,这个接口定义了所有的提供远程服务的功能，接口必须继承`Remote`类，每一个定义地方法都要抛出`RemoteException`异常对象下面是源程序  

**FileService.java**

    import java.rmi.Remote;  
    import java.rmi.RemoteException;   
    public interface FileService extends Remote {
       Boolean addFile(String fileName) throws RemoteException;  
       Boolean modifyFile(String fileName,String oldStr,String replaceStr) throws RemoteException ;  
       Boolean deleteFile(String fileName) throws RemoteException;  
       String[] listAllFile() throws RemoteException ; 
       String[] listFile() throws RemoteException; 
       Integer countFile() throws RemoteException; 
       String folderSize() throws RemoteException; 
    }   




- **接口的具体实现**

  第二步就是对于上面的接口进行实现，在FileServer中的FileServiceImpl中实现

- **定义Server端的主程序入口**

服务器端提供远程对象服务
Registry是个接口，他继承了Remote，此方法返回本地主机在默认注册表端口上创建的远程对象 Registry 的引用。  
     
    Registry registry=LocateRegistry.createRegistry(int port);

绑定对此注册表中指定 service 的远程引用。  
  
    Naming.rebind(“service”,service);

  **FileServerMain.java**

    import java.rmi.Naming;  
    import java.rmi.registry.LocateRegistry;
    
    public class FileServerMain {  
    public static void main(String[] argv){  
     try{  
       FileService fileService = new FileServiceImpl();
       LocateRegistry.createRegistry(1099);  
       Naming.rebind("File",fileService);  
       System.out.println("Server Start");  
     }catch (Exception e){  
        e.printStackTrace();  
      }  
     }  
    }  


- **在Client端的请求服务**

客户机端向服务器提供相应的服务请求。

       Naming.lookup(String url)
url 格式如下"rmi://localhost/"+远程对象引用  

定义一个模板类，返回指定名称的服务

**RmiConnection.java** 

    import java.rmi.Naming;
    public class RmiConnection<T> {
     private T t;
     RmiConnection(String name){
      try {
        t=(T) Naming.lookup(name);
       }
      catch (Exception e){
         System.out.println("Error find Service");
         e.printStackTrace();
       }
      }
      public T getT() { return t;}
    }

</br>
</br>
####2.Dubbo实现 ####

- **项目结构**
>**├── DubboFile**  
 │&ensp;├── pom.xml  
 **├── Consumer**  
 │&ensp;└── src  
 │&ensp;&ensp;└── main  
 │&ensp;&emsp;&emsp;└── java  
 │&ensp;&emsp;&emsp;&emsp;└── com.lab.yao.fileclient  
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── Application  
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── Consumer  
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── FileController  
 │&ensp;&emsp;&emsp;&emsp;└── resource   
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── consumer.xml  
 **├── Provider**  
 │&ensp;└── src  
 │&ensp;&ensp;└── main  
 │&ensp;&emsp;&emsp;└── java  
 │&ensp;&emsp;&emsp;&emsp;└── com.lab.yao.fileserver  
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── FileServiceImpl  
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── Provider 
 │&ensp;&emsp;&emsp;&emsp;└── resource  
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── provider.xml  
 **├── Service**  
 │&ensp;└── src  
 │&ensp;&ensp;└── main  
 │&ensp;&emsp;&emsp;└── java  
 │&ensp;&emsp;&emsp;&emsp;└── com.lab.yao.fileservice  
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── FileService  


- **Zookeeper**

&emsp;&emsp;Dubbo能与Zookeeper做到集群部署，当提供者出现断电等异常停机时，Zookeeper注册中心能自动删除提供者信息，当提供者重启时，能自动恢复注册数据，以及订阅请求  

&emsp;&emsp;安装Zookeeper参考
[Zookeeper安装和使用Windows环境](http://blog.csdn.net/tlk20071/article/details/52028945)


- **POM文件** 
 
在DubboFile中定义，否则分包定义会版本不一致导致各种问题
使用的依赖

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- https://mvnrepository.com/artifact/io.dubbo.springboot/spring-boot-starter-dubbo -->
        <dependency>
            <groupId>io.dubbo.springboot</groupId>
            <artifactId>spring-boot-starter-dubbo</artifactId>
            <version>1.0.0</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/com.101tec/zkclient -->
        <dependency>
            <groupId>com.101tec</groupId>
            <artifactId>zkclient</artifactId>
            <version>0.10</version>
        </dependency>

- **定义服务接口**  

该接口需单独打包，在服务提供方和消费方共享,同RMI部分的Service定义


- **Provider**

使用Spring Xml配置暴露服务，已注解  

**provider.xml**

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://code.alibabatech.com/schema/dubbo
       http://code.alibabatech.com/schema/dubbo/dubbo.xsd">

    <!-- 提供方应用信息，用于计算依赖关系 -->
    <dubbo:application name="FileService-Provider" />

    <!-- 这里使用的注册中心是zookeeper -->
    <dubbo:registry address="zookeeper://127.0.0.1:2181"/>

    <!-- 用dubbo协议在20880端口暴露服务 -->
    <dubbo:protocol name="dubbo" port="20880" />

    <!-- 将该接口暴露到dubbo中 -->
    <dubbo:service interface="com.lab.yao.service.FileService" ref="fileService" protocol="dubbo" />

    <!-- 将具体的实现类加入到Spring容器中 -->
    <bean id="fileService" class="com.lab.yao.serviceimpl.FileServiceImpl" />

    <!-- 监控的配置 -->
    <dubbo:monitor protocol="registry"/>

    </beans>

</br>
运行Context提供服务 
 
**Provider.java**  

    public class Provider {
    public static void main(String[] argv) throws IOException {
       ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("provider.xml");
       System.out.println(context.getDisplayName()+" :Here");
       context.start();
       System.out.println("Service Start");
       System.in.read();
     }
    }



- **Consumer**

通过Spring配置引用远程服务  
  
**consumer.xml**

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://code.alibabatech.com/schema/dubbo
       http://code.alibabatech.com/schema/dubbo/dubbo.xsd">
    
    <!-- 提供方应用信息，用于计算依赖关系 -->
    <dubbo:application name="dubbo-a-consumer" />
    
    <!--向 zookeeper 订阅 provider 的地址，由 zookeeper 定时推送-->
    <dubbo:registry address="zookeeper://localhost:2181"/>
    
    <!--使用 dubbo 协议调用定义好的 api.fileService 接口-->
    <dubbo:reference id="fileService" interface="com.lab.yao.service.FileService"/>
    
    </beans>

</br>

同样使用模板类加载Context调用接口

    public class Consumer<T> {
      private T t; 
      Consumer(String name){
         ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("provider.xml");
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



###四、实验结果###

Dubbo、Rmi结果一致，以下省略RMI，用Dubbo在Zookeeper注册服务
由PostMan发起请求，验证结果  

**以G:\test下文件夹为例**  

已有的文件目录结构
>├── test  
 │&ensp;├── test   
 │&ensp;&ensp;&ensp;└── test.txt  
 │&ensp;└── list.txt  
 │&ensp;└── test1.txt  

- **开启Zookeeper**
![Zookeeper](/image/Zookeeper.png)

- **运行Provider** 
![Provider](/image/Provider.png)

- **运行Consumer的Application**
![Application](/image/Application.png)

- **创建一个文件**
![create](/image/create.png)

- **列出文件夹的**
![fileList](/image/fileList.png)

- **该文件夹下所有的文件（包括子文件夹下）**
![all](/image/all.png)

- **计数该文件夹下文件数**
![count](/image/count.png)

- **文件夹大小**
![size](/image/size.png)



###五、实验总结###

基本了解了RPC的原理，RMI和Dubbo两种技术的基本流程

**遇到的问题**  

1.引入第三方包Dubbo的和Spring依赖冲突，如日志等

![size](/image/dependency.png)

运用依赖树找到冲突，exclude掉



- **工程代码**  
[RMI实现远程文件操作](https://github.com/Man1usaka/JavaRmi)  
[Dubbo实现远程文件操作](https://github.com/Man1usaka/Spring_Dubbo)

- **参考**   
[Dubbo入门---搭建一个最简单的Demo框架](https://blog.csdn.net/noaman_wgs/article/details/70214612/)   
[Java文件操作大全](https://www.cnblogs.com/fnlingnzb-learner/p/6010165.html)


