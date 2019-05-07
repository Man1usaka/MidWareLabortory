##中间件技术实验：远程过程调用 ##

###一、引言###

- **实验目的**

&emsp;&emsp;理解EJB，利用wildfly服务器容器进行远程调用。

- **实验内容**

&emsp;&emsp;建立有状态的Java Bean，实现以下功能：  
&emsp;&emsp;操作用户（录入人员）登陆后，显示本次登陆的次数和上一次登陆的时间  
&emsp;&emsp;操作用户登录后，可进行校友的检索、修改、删除、统计等功能  
&emsp;&emsp;5分钟如果没有操作，则自动登出系统  
&emsp;&emsp;操作用户退出时，显示用户连接的时间长度，并把此次登陆记录到数据库  
&emsp;&emsp;在2台机器上模拟2个录入员，生成1000个校友用户，并进行各种增删改的操作。

- **扩展内容**

&emsp;&emsp;构建一个校友生成器，生成示例的校友个人信息。甚至使得校友资料达到“以假乱真”的地步。

###二、相关环境###
- 操作系统：Windows10 X64
- IDE:     IDEA 2018.2.6
- 其他： WildFly16.0

###三、具体过程###
####1.&emsp;项目结构####

>**├── ejb-remote**  
 │&ensp;├── pom.xml  
 **├── client**  
 │&ensp;└── src  
 │&ensp;&ensp;└── main  
 │&ensp;&emsp;&emsp;└── java  
 │&ensp;&emsp;&emsp;&emsp;└── yao.study.client  
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── Client  
 **├── server-side**  
 │&ensp;└── src  
 │&ensp;&ensp;└── main  
 │&ensp;&emsp;&emsp;└── java  
 │&ensp;&emsp;&emsp;&emsp;└── yao.study.remote  
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── model  
 │&ensp;&emsp;&emsp;&emsp;&emsp;&emsp;└── Admin  
 │&ensp;&emsp;&emsp;&emsp;&emsp;&emsp;└── Alumni    
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── stateful  
 │&ensp;&emsp;&emsp;&emsp;&emsp;&emsp;└── EntryClerkBean    
 │&ensp;&emsp;&emsp;&emsp;&emsp;&emsp;└── RemoteEntryClerk        
 │&ensp;&emsp;&emsp;&emsp;&emsp;└── tool  
 │&ensp;&emsp;&emsp;&emsp;&emsp;&emsp;└── InfoGenerator  

####2.&emsp;WildFly相关####

- **定义需要使用的数据源**

  &emsp;参照[](https://blog.csdn.net/kylinsoong/article/details/17042245)

  1.在路径下添加module(module.xml和mysql Jar包)

  **如图**  
  ![module](/image/module.png)

  **module.xml的配置**  
    <?xml version='1.0' encoding='UTF-8'?>
    <module xmlns="urn:jboss:module:1.5" name="com.mysql">
      <resources>
        <resource-root path="mysql-connector-java-5.1.38.jar"/>
      </resources>
      <dependencies>
        <module name="javax.api"/>
        <module name="javax.transaction.api"/>
      </dependencies>
    </module>
 
   **注意的问题**：需要确认jar包版本（8.0的尝试失败）,以及`urn:jboss:module:1.5`中1.5与其他module的一致

  

  2.更改WildFly的Standalone的配置文件

  **如图**  
  ![module](/image/standalone.png)

  在datasouces标签下添加两项  
  **datasource** 

    <datasource jndi-name="java:jboss/datasources/mysqlDS" pool-name="mysqlDSPool" enabled="true" use-java-context="true">
        <connection-url>jdbc:mysql://***.***.***.***:3306/study?characterEncoding=utf-8</connection-url>
        <driver>mysql</driver>
        <security>
          <user-name>root</user-name>
          <password>123456</password>
        </security>
    </datasource>

  **driver** 
   
    <driver name="mysql" module="com.mysql">
       <xa-datasource-class>com.mysql.jdbc.jdbc2.optional.MysqlXADataSource</xa-datasource-class>
    </driver>

   **注意的问题**：需要 `enabled="true" use-java-context="true"`才能动态加载，`module="com.mysql"`需要和刚才添加的module对应

  

  3.启动bin中的standalone.bat，数据源已经成功使用

  **如图**  
  ![module](/image/datasource.png)

</br>
</br>
  

- **部署项目的Server**

  待解决的问题：在idea使用wildfly:run命令不能加载刚才添加的mysql数据源到context中  
  这里选择使用wildfly:deploy将项目部署到wildfly容器

  **如图配置**  
  ![module](/image/deploy.png)

  先启动bin中的standalone.bat，后点击idea的start，命令行出现下图证明已经部署
  
  **如图配置**  
  ![module](/image/deployToWildfly.png)


####3.&emsp;具体实现####

- **定义录入员的相关接口**

  **RemoteEntryClerk.java**

      public interface RemoteEntryClerk {  
         String login(Admin admin);  
         Integer deleteAlumni(Long id);  
         Integer insertAlumni(Alumni record);  
         Integer updateAlumni(Alumni record);  
         List<Alumni> listAlumnu();  
         String loginInfo();  
         String  logout(Admin admin);  
         List<Alumni> generatorAlumni(Integer num);  
     }
 
- **使用Stateful的Bean实现接口**
  
  篇幅问题展示该Stateful的Bean的属性  
  **EntryClerkBean.java**

    @Stateful
    @Remote(RemoteEntryClerk.class)
    public class EntryClerkBean implements RemoteEntryClerk{
    
       private int loginCount;              //登录次数
       private LocalDateTime thisTime;      //本次登录时间
       private LocalDateTime lastTime;      //上次登录时间
       private LocalDateTime operateTime;   //最后一次操作的时间，用来判断超时
       private Connection conn;             //数据库连接
       private Admin admin;                 //登录的录入员
    }

- **在Client调用Server**
    
   关键是 `context.lookup()`方法

   **lookupRemoteEntryClerk()方法**

    private static RemoteEntryClerk lookupRemoteEntryClerk() throws NamingException {
        final Hashtable<String, String> jndiProperties = new Hashtable<>();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        final Context context = new InitialContext(jndiProperties);
        return (RemoteEntryClerk) context.lookup("ejb:/wildfly-ejb-remote-server-side/EntryClerkBean!"
        + RemoteEntryClerk.class.getName() + "?stateful");
    }
   

   其中Server加载数据库Connection也用到类似的动态加载  
   **getConnection()方法**

    private Boolean getConnection() {
        try {
            InitialContext context = new InitialContext();
            DataSource dataSource = (DataSource) context.lookup("java:jboss/datasources/mysqlDS");
            System.out.println("success mysql");
            conn = dataSource.getConnection();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("error mysql");
        return false;
    }


- **完成一个校友生成器**

 随机选取已在内存中的属性
 ![module](/image/generator.png)
 
  **使用单例模式生成**

    public static InfoGenerator getInstance() {
        if (infoGenerator == null) {
            synchronized (InfoGenerator.class) {
                if (infoGenerator == null) {
                    infoGenerator = new InfoGenerator();
                }
            }
        }
        return infoGenerator;
    }

  **指定数量进行生成**

    public List<Alumni> generatorAlumni(int num){

        if(num<= 0){
            return null;
        }

        List<Alumni> alumnis = new ArrayList<>();
        for(int i=0;i<num;i++){
            Alumni temp = new Alumni();
            temp.setName(getName());
            temp.setPhone(getTelephone());
            temp.setJob(getDuties());
            temp.setCompany(getWorkUnit());
            temp.setGender(getSex());
            temp.setWeChat(getWechat());
            temp.setWorkCity(getWorkingLocation());
            temp.setMail(getEmail());

            int year = getYear();
            temp.setEnrollYear(year);
            temp.setGraduateYear(year+4);

            alumnis.add(temp);
        }
        return  alumnis;
    }


###四、实验结果###

**在Client检验**

    public static void main(String[] args) throws Exception {
        final RemoteEntryClerk f = lookupRemoteEntryClerk();
        Admin admin = new Admin("root","123456");
        //登录
        System.out.println(f.login(admin));

        //删除已经超时
        Thread.sleep(5000);
        System.out.println("Delete: "+f.deleteAlumni(7L));

        //重新登录
        System.out.println(f.login(admin));

        //5秒后退出
        Thread.sleep(5000);
        System.out.println(f.logout(admin));

        //重新登录
        System.out.println(f.login(admin));
        System.out.println("Delete: "+f.deleteAlumni(7L));

        //插入100个校友
        for(Alumni alumni :f.generatorAlumni(100)){
            f.insertAlumni(alumni);
        };
        
        //更新校友名字
        Alumni testUpdateAlumni = new Alumni();
        testUpdateAlumni.setId(8L);
        testUpdateAlumni.setName("testUpdate");
        System.out.println("Updata: " +f.updateAlumni(testUpdateAlumni));

        //获得所有
        for(Alumni alumni:f.listAlumnu()){
            System.out.println(alumni);
        }

    }

**运行结果**
 ![module](/image/result.png)

**验证功能**  
**1**.&emsp;操作用户（录入人员）登陆后，显示本次登陆的次数和上一次登陆的时间  
   即第一行登录后显示 
   `登录次数:8 上次登录:2019-04-06T15:54:49当前时间:2019-04-06T15:56:40.639`  

**2**.&emsp;5分钟如果没有操作，则自动登出系统  
   5分钟太长，验证使用5s代替，即通过

        Thread.sleep(5000);
        System.out.println("Delete: "+f.deleteAlumni(7L));

   实现后，返回`Delete:2`，即为超时，代码如下
        
        public Integer deleteAlumni(Long id){
        if(isTimeOutAndUpdate()){
            return 2;
        }
        ...
        }
   重新登陆后立即删除，返回`Delete:1`为成功
   
        System.out.println(f.login(admin));
        System.out.println("Delete: "+f.deleteAlumni(7L));
   
       
**3**.&emsp;操作用户退出时，显示用户连接的时间长度，并把此次登陆记录到数据库

   即通过

           Thread.sleep(5000);
           System.out.println(f.logout(admin));  

   5秒后退出，返回 `本次连接5秒` ,数据库已经记录这次的登录时间`2019-04-06 15:56:46`和连接的时间 `5052毫秒`

   **结果如图**
   ![size](/image/logout.png)
   
**4**.进行各种增删改的操作
   
   过程：插入100个校友，并删除id为7的校友，更新id为8的校友名字，最后列出所有的校友 

   **增删改结果如图**
   ![size](/image/CRUD.png)

   **列出所有的校友**
   ![size](/image/CRUD2.png)
    
###五、实验总结###

对于EJB的使用，两种Bean有了深入理解，熟悉了WildFly的使用和相关的配置

**遇到的问题**  

1.文中提到的使用Maven wildfly:run 不能加载mysql的数据源到上下文，可能是版本的问题


- **工程代码**  
[EJB](https://github.com/Man1usaka/EjbWithWildFly)  


- **参考**   
[LocalDateTime优雅的处理日期](https://blog.csdn.net/yzb_20115952/article/details/79735877)   
[JBoss 7/WildFly中配置使用Mysql数据库](https://blog.csdn.net/kylinsoong/article/details/17042245)


