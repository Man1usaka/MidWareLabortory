package com.study.yao.model;

import javax.persistence.*;
import java.io.Serializable;


/**
 * @author Y Jiang
 * 姓名、性别、生日、入学年份、毕业年份、工作城市/地区、工作单位、职务、手机、邮箱、微信
 */
@Entity
@Table(name = "alumni")
public class Alumni {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String gender;

    @Column(name = "enroll_year")
    private Integer enrollYear;

    @Column(name = "graduate_year")
    private Integer graduateYear;

    @Column(name = "work_city")
    private String workCity;

    private String company;
    private String job;
    private String phone;
    private String mail;

    @Column(name = "wechat")
    private String weChat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getEnrollYear() {
        return enrollYear;
    }

    public void setEnrollYear(Integer enrollYear) {
        this.enrollYear = enrollYear;
    }

    public Integer getGraduateYear() {
        return graduateYear;
    }

    public void setGraduateYear(Integer graduateYear) {
        this.graduateYear = graduateYear;
    }

    public String getWorkCity() {
        return workCity;
    }

    public void setWorkCity(String workCity) {
        this.workCity = workCity;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getWeChat() {
        return weChat;
    }

    public void setWeChat(String weChat) {
        this.weChat = weChat;
    }

    @Override
    public String toString() {
        return "[Alumni{" +
                "name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", enrollYear=" + enrollYear +
                ", graduateYear=" + graduateYear +
                ", workCity='" + workCity + '\'' +
                ", company='" + company + '\'' +
                ", job='" + job + '\'' +
                ", phone='" + phone + '\'' +
                ", mail='" + mail + '\'' +
                ", weChat='" + weChat + '\'' +
                "}]";
    }
}
