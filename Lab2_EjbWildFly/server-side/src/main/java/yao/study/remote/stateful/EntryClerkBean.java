package yao.study.remote.stateful;


import yao.study.remote.model.Admin;
import yao.study.remote.model.Alumni;
import yao.study.remote.tool.InfoGenerator;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Stateful
@Remote(RemoteEntryClerk.class)
public class EntryClerkBean implements RemoteEntryClerk{

    private int loginCount;
    private LocalDateTime thisTime;
    private LocalDateTime lastTime;
    private LocalDateTime operateTime;
    private Connection conn;
    private Admin admin;

    public EntryClerkBean(){
        getConnection();
        thisTime = LocalDateTime.now();
        loginCount=0;
        operateTime = LocalDateTime.now();
    }

    /**
     * 连接数据库
     * @return Boolean
     */
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

    /**
     * 5分钟如果没有操作，则自动登出系统；
     */
    private Boolean isTimeOutAndUpdate(){
        if(LocalDateTime.now().minusSeconds(5L).compareTo(operateTime)>0){
            logout(admin);
            return true;
        }
        operateTime = LocalDateTime.now();
        return  false;
    }

    @Override
    /**
     *  操作用户（录入人员）登陆后，显示本次登陆的次数和上一次登陆的时间；
     */
    public String login(Admin admin){
        thisTime = LocalDateTime.now();
        operateTime =thisTime;
        this.admin = admin;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("select * from admin where username=?");
            preparedStatement.setString(1, admin.getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if(admin.getPassword().equals(resultSet.getString("password"))){
                    lastTime = LocalDateTime.parse(resultSet.getString("last_time").replace(" ","T"));
                    loginCount = resultSet.getInt("login_count")+1;
                    return loginInfo();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String loginInfo(){
        return "登录次数:"+this.loginCount+" 上次登录:"+this.lastTime+"当前时间:"+LocalDateTime.now().toString();
    }

    @Override
    /**
     *操作用户退出时，显示用户连接的时间长度，并把此次登陆记录到数据库。
     */
    public String logout(Admin admin){
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE admin SET connection_time=?,login_count=?,last_time=? WHERE username = ?");
            preparedStatement.setLong(1, Duration.between(thisTime,LocalDateTime.now()).toMillis());
            preparedStatement.setString(3, thisTime.toString());
            preparedStatement.setInt(2,loginCount);
            preparedStatement.setString(4,admin.getUsername());
            preparedStatement.execute();
            return "本次连接" + Duration.between(thisTime,LocalDateTime.now()).toMillis()/1000+"秒";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Integer deleteAlumni(Long id){
        if(isTimeOutAndUpdate()){
            return 2;
        }
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("delete from alumni where id=?");
            preparedStatement.setLong(1,id);
            preparedStatement.execute();
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    };

    @Override
    public Integer insertAlumni(Alumni record){
        if(isTimeOutAndUpdate()){
            return 2;
        }
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("" +
                    "insert into alumni (" +
                    "name, gender, enroll_year, graduate_year, work_city, company, job, phone, mail, wechat) " +
                    " values (?,?,?,?,?,?,?,?,?,?)");
            preparedStatement.setString(1, record.getName());
            preparedStatement.setString(2, record.getGender());
            preparedStatement.setInt(3, record.getEnrollYear());
            preparedStatement.setInt(4, record.getGraduateYear());
            preparedStatement.setString(5, record.getWorkCity());
            preparedStatement.setString(6, record.getCompany());
            preparedStatement.setString(7, record.getJob());
            preparedStatement.setString(8, record.getPhone());
            preparedStatement.setString(9, record.getMail());
            preparedStatement.setString(10, record.getWeChat());
            preparedStatement.executeUpdate();
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    };

    @Override
    public Integer updateAlumni(Alumni record){
        if(isTimeOutAndUpdate()){
            return 2;
        }
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE alumni SET name = ? WHERE id = ?");
            preparedStatement.setString(1,record.getName());
            preparedStatement.setLong(2,record.getId());
            preparedStatement.execute();
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    };

    @Override
    public List<Alumni> listAlumnu(){
        if(isTimeOutAndUpdate()){
            return null;
        }
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("select * from alumni");
            List<Alumni> alumniList = new ArrayList<>();
            ResultSet resultSet =  preparedStatement.executeQuery();
            while (resultSet.next()){
                alumniList.add(assembleInfo(resultSet));
            }
            return  alumniList;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  null;
    };

    @Override
    public List<Alumni> generatorAlumni(Integer num){
        InfoGenerator infoGenerator = InfoGenerator.getInstance();
        return  infoGenerator.generatorAlumni(num);
    }

    private Alumni assembleInfo(ResultSet resultSet) throws SQLException {
        Alumni info = new Alumni();
        info.setId(resultSet.getLong("id"));
        info.setName(resultSet.getString("name"));
        info.setGender(resultSet.getString("gender"));
        info.setEnrollYear(resultSet.getInt("enroll_year"));
        info.setGraduateYear(resultSet.getInt("graduate_year"));
        info.setWorkCity(resultSet.getString("work_city"));
        info.setCompany(resultSet.getString("company"));
        info.setJob(resultSet.getString("job"));
        info.setPhone(resultSet.getString("phone"));
        info.setMail(resultSet.getString("mail"));
        info.setWeChat(resultSet.getString("wechat"));
        return info;
    }

}
