package com.fishpound.accountservice.service;

public interface AsyncService {
    void createNoticeToDeptLead(String uid, String oid, String title, String content);
    void createNoticeByUid(String uid, String oid, String title, String content);
}
