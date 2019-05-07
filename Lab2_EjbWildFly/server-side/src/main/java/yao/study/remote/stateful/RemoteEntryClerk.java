package yao.study.remote.stateful;
import yao.study.remote.model.Admin;
import yao.study.remote.model.Alumni;

import java.util.List;


/**
 * 建立有状态的Java Bean，实现以下功能：
 * 操作用户（录入人员）登陆后，显示本次登陆的次数和上一次登陆的时间；
 * 操作用户登录后，可进行校友的检索、修改、删除、统计等功能；
 * 5分钟如果没有操作，则自动登出系统；
 * 操作用户退出时，显示用户连接的时间长度，并把此次登陆记录到数据库。
 * 在2台机器上模拟2个录入员，生成1000个校友用户，并进行各种增删改的操作。
 */
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
