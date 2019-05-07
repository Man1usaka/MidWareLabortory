#中间件技术实验四：Spring和AOP编程 #

</br>
##一、引言

###实验目的

&emsp;&emsp;通过Spring AOP编程进一步熟悉spring或者spring boot基于MVC的编程

###实验内容

&emsp;&emsp;利用Spring技术实现实验二中的校友信息网站。要求采用MVC框架，同时构建一个日志记录的切面。要求  

&emsp;&emsp;a. 对于所有的Alumni表的查询操作，  记录各个操作的时间、用户，读取内容，存入ReadLog表格中  

&emsp;&emsp;b. 对于所有的Alumni表的更新（更新和删除）操作，记录各个操作的时间、用户，修改的新值和旧值，存入UpdateLog表格中（删除的新值为null）

</br>
##二、相关环境
- 操作系统：Windows10 X64
- IDE:     IDEA 2018.2.6
- 其他：Spring、log4j、slf4j、logback

</br>
##三、具体过程
###1. 项目结构
>**├── SpringAop**  
 │&ensp;├── pom.xml  
 │&ensp;└── **src**  
 │&ensp;&ensp;└── **main**  
 │&ensp;&emsp;&emsp;└── **java**  
 │&ensp;&emsp;&emsp;&emsp;└── **com.lab.yao**  
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── **aop**  
 │&ensp;&emsp;&emsp;&emsp;&emsp;&emsp;└──WebLogAspect.java  
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── **controller**  
 │&ensp;&emsp;&emsp;&emsp;&emsp;&emsp;└──AlumniController.java  
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── **service**  
 │&ensp;&emsp;&emsp;&emsp;&emsp;&emsp;└──AlumniService.java  
 │&ensp;&emsp;&emsp;&emsp;&emsp;&emsp;└──AlumniServiceImpl.java  
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── **model**  
 │&ensp;&emsp;&emsp;&emsp;&emsp;&emsp;└──User.java  
 │&ensp;&emsp;&emsp;&emsp;&emsp;&emsp;└──Alumni.java  
 │&ensp;&emsp;&emsp;&emsp;&emsp;&emsp;└──AlumniRepository.java  
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── WebApplication.java  

###2. 引入依赖，修改POM
 
&emsp;&emsp;使用`spring-boot-starter-web`引入SpringMvc相关依赖,使用`spring-boot-starter-data-jpa`和`mysql-connector-java`引入依赖便可以使用JPA调用Hibernate接口访问Mysql数据库，最后使用`spring-boot-starter-log4j`引入 log4j 日志的依赖


&emsp;&emsp;最后一步引入新的`log4j`会出现日志的冲突
`SLF4J: Class path contains multiple SLF4J bindings.`的冲突，原因是`spring-boot-starter-web`引入了`lonback`记录日志，而我在这选择log4j用在切面记录日志，可以通过 `exclusions` 消除冲突，但这里还是默认绑定了logback `SLF4J: Actual binding is of type[ch.qos.logback.classic.util.ContextSelectorStaticBinder]` ，Spring的日志可以正常记录，通过后文的设置可以两个一起使用，所以没有`exclusions` 。


**pom.xml**

    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.study.yao</groupId>
    <artifactId>SpringAop</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-parent</artifactId>
        <version>2.1.0.RELEASE</version>

    </parent>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>2.1.0.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-log4j</artifactId>
            <version>1.3.7.RELEASE</version>
        </dependency>
        
    </dependencies>

    </project>


###3. 配置日志Log4j

  &emsp;&emsp;定义了两个Category的日志，记录级别大于等于 `INFO` 的记录，`selectFile，stdout` 分别代表两种方式，`stdout`使用了`org.apache.log4j.ConsoleAppender`即在控制台输出，而`org.apache.log4j.DailyRollingFileAppender`代表在文件输出，DailyRolling表示每天更换，其余的就是设置文件，格式之类的

  `log4j.appender.*.layout=org.apache.log4j.HTMLLayout` 可以设置输出到表格,且可以自己重新创建一个类定义输出的格式(参考链接)

  `log4j.additivity.*`可以不继承父类的级别,让其独立输出到指定日志文件,可以解决上文提到的冲突(参考链接)

  `log4j.appender.*.file` 可以设置输出的文件

    # 对于所有的Alumni表的查询操作， 记录各个操作的时间、用户，读取内容，存入ReadLog表格中
    log4j.additivity.SelectCategory=false
    log4j.category.SelectCategory = INFO ,selectFile,stdout
    
    # 对于所有的Alumni表的更新（更新和删除）操作，记录各个操作的时间、用户，修改的新值和旧值，存入UpdateLog表格中（删除的新值为null）
    log4j.additivity.ModifyCategory=false
    log4j.category.ModifyCategory = INFO ,modifyFile,stdout
    
    # 控制台输出
    log4j.appender.stdout=org.apache.log4j.ConsoleAppender
    log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
    log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss,SSS} %5p %c{1}:%L - %m%n
    
    # 查询日志输出
    log4j.appender.selectFile=org.apache.log4j.DailyRollingFileAppender
    log4j.appender.selectFile.file=G:/logs/ReadLog.html
    log4j.appender.selectFile.DatePattern='.'yyyy-MM-dd
    log4j.appender.selectFile.layout=org.apache.log4j.HTMLLayout

    
    # 修改日志输出
    log4j.appender.modifyFile=org.apache.log4j.DailyRollingFileAppender
    log4j.appender.modifyFile.file=G:/logs/UpdateLog.html
    log4j.appender.modifyFile.DatePattern='.'yyyy-MM-dd
    log4j.appender.modifyFile.Threshold = ERROR
    log4j.appender.modifyFile.layout=org.apache.log4j.HTMLLayout


###4. 定义切面
####4.1 查询操作的切面
  
- **定义切点PointCut**

 @PointCut

    @Pointcut("execution(* com.study.yao.controller.AlumniController.getAlumniList(..))")
    public void read(){}


 对应到Controller的两个查询方法


    @GetMapping(value = "/{id}")
    public Alumni getAlumniList(@PathVariable Long id) {
    return alumniService.findById(id);
    }
    
    @GetMapping(value = "/all")
    public List<Alumni> getAlumniList() {
    return alumniService.findAll();
    }

- **采用环绕通知**

  `joinpoint.proceed()`用于执行被环绕的方法，`result`接受方法的返回值可以获得查询的信息

  由于数据库只有一个user，这里通过采用`new User().hashCode()`新建user并获得其hashcode来模拟

**@Around**

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

####4.2 更改操作的切面


- **定义切点PointCut**

**@Pointcut**

    @Pointcut("execution(* com.study.yao.controller.AlumniController.deleteAlumni(..))" +
            "|| execution(* com.study.yao.controller.AlumniController.putAlumni(..))"+
            "|| execution(* com.study.yao.controller.AlumniController.postAlumni(..))")
    public void modify(){}

- **采用环绕通知**
  
存在一个问题，不知道怎么获得修改前的值，只能这里通过`joinpoint.getArgs()`可以获得传入的值，即要修改的对象，再用该对象的id去数据库查一遍

    Alumni a = (Alumni)joinpoint.getArgs()[0];
    a = alumniService.findById(a.getId());

   其他同第一部分


**@Around**

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


</br>

###5. 完善SpringMvc


- **Controller层**

**AlumniController.java**

    @RestController
    @RequestMapping("/alumni")
    public class AlumniController {

	    @Autowired
	    AlumniService alumniService;
	
	    /**
	     * 获取 Alumni
	     * 处理 "/Alumni" 的 GET 请求，用来获取 Alumni 列表
	     */
	    @GetMapping(value = "/{id}")
	    public Alumni getAlumniList(@PathVariable Long id) {
	        return alumniService.findById(id);
	    }
	
	    /**
	     * 获取 Alumni 列表
	     * 处理 "/Alumni" 的 GET 请求，用来获取 Alumni 列表
	     */
	    @GetMapping(value = "/all")
	    public List<Alumni> getAlumniList() {
	        return alumniService.findAll();
	    }
	
	
	    /**
	     * 创建 Alumni
	     * 处理 "/Alumni/create" 的 POST 请求，用来新建 Alumni 信息
	     * 通过 @ModelAttribute 绑定表单实体参数，也通过 @RequestParam 传递参数
	     */
	    @PostMapping(value = "")
	    public Alumni postAlumni(@RequestBody Alumni alumni) {
	        return alumniService.insertByAlumni(alumni);
	    }
	
	
	    /**
	     * 更新 Alumni
	     * 处理 "/update" 的 PUT 请求，用来更新 Alumni 信息
	     */
	    @PutMapping(value = "")
	    public Alumni putAlumni(@RequestBody Alumni alumni) {
	        return alumniService.update(alumni);
	    }
	
	    /**
	     * 删除 Alumni
	     * 处理 "/Alumni/{id}" 的 GET 请求，用来删除 Alumni 信息
	     */
	    @DeleteMapping(value = "")
	    public Alumni deleteAlumni(@RequestBody Alumni alumni) {
	        return alumniService.delete(alumni.getId());
	    }
    }

- **Service层**
 
**AlumniService.java**

    public interface AlumniService {
	    List<Alumni> findAll();
	
	    Alumni insertByAlumni(Alumni Alumni);
	
	    Alumni update(Alumni Alumni);
	
	    Alumni delete(Long id);
	
	    Alumni findById(Long id);
    }

**AlumniServiceImpl.java**

    public class AlumniServiceImpl implements AlumniService {

	    @Autowired
	    AlumniRepository AlumniRepository;
	
	    @Override
	    public List<Alumni> findAll() {
	        return AlumniRepository.findAll();
	    }
	
	    @Override
	    public Alumni insertByAlumni(Alumni Alumni) {
	        return AlumniRepository.save(Alumni);
	    }
	
	    @Override
	    public Alumni update(Alumni Alumni) {
	        return AlumniRepository.save(Alumni);
	    }
	
	    @Override
	    public Alumni delete(Long id) {
	        Alumni Alumni = AlumniRepository.findById(id).get();
	        AlumniRepository.delete(Alumni);
	        return Alumni;
	    }
	
	    @Override
	    public Alumni findById(Long id) {
	        return AlumniRepository.findById(id).get();
	    }
    }

##四、实验结果

####由PostMan发起请求，验证结果  

- **使用实验二已有的数据**
![db](/image/db.png)

- **运行WebApplication** 
![app](/image/app.png)

- **PostMan发起请求，查询id为8的校友**
![select](/image/select.png)

**可以见到定义在控制台输出的日志**
![select](/image/select1.png)

- **PostMan发起请求，查询所有校友**
![select](/image/selectAll.png)

- **PostMan发起请求，更改id为10的校友信息**
![put](/image/put.png)

- **PostMan发起请求，删除id为9的校友信息**
![delete](/image/delete.png)

- **PostMan发起请求，新增一个校友**
![post](/image/post.png)

####验证日志记录

- **在定义的路径下找到日志**
![log](/image/log.png)

- **已经记录下刚才的查询**
![readlog](/image/readlog.png)

- **已经记录下刚才的更新**
![updatelog](/image/updatelog.png)

</br>

##五、实验总结

实现了要求的功能

&emsp;&emsp;a. 对于所有的Alumni表的查询操作，  记录各个操作的时间、用户，读取内容，存入ReadLog表格中  

&emsp;&emsp;b. 对于所有的Alumni表的更新（更新和删除）操作，记录各个操作的时间、用户，修改的新值和旧值，存入UpdateLog表格中（删除的新值为null）

中间通过解决问题深一步了解了AOP的原理 以及Spring 的日志系统

**遇到的问题**  

&emsp;&emsp;1.日志的多重绑定问题

&emsp;&emsp;2.日志的定向输出问题

&emsp;&emsp;3.记录修改前的值

&emsp;&emsp;都已经在第三部分（过程中）解释并解决

</br>

- **工程代码**  


&emsp;&emsp;[Spring AOP](https://github.com/Man1usaka/JavaRmi)  

- **参考**

&emsp;&emsp;[Spring Guide](https://spring.io/guides)
[了解log4j、slf4j、logback的联系和区别](https://www.cnblogs.com/hanszhao/p/9754419.html)  

&emsp;&emsp;[解决SLF4J的多重绑定](https://blog.csdn.net/qq_29479041/article/details/82863022)  

&emsp;&emsp;[Log4j自定义HTMLLayout](https://blog.csdn.net/drift_away/article/details/7410038)  

&emsp;&emsp;[Log4j设置additivity](https://blog.csdn.net/junshao90/article/details/8364812)