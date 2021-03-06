package com.fishpound.accountservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "notice")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "state")
    private Boolean state = true;

    @Column(name = "time")
    private Date time = new Date();

    @Column(name = "oid")
    private String oid;

    @ManyToOne(targetEntity = UserInfo.class, cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "uid", referencedColumnName = "id")
    @JsonBackReference
    private UserInfo userInfo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Notice() {
    }

    public Notice(String title, String content, String oid) {
        this.title = title;
        this.content = content;
        this.oid = oid;
    }

    public Notice(String title, String content, String oid, UserInfo userInfo) {
        this.title = title;
        this.content = content;
        this.oid = oid;
        this.userInfo = userInfo;
    }
}
