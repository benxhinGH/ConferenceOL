package com.usiellau.conferenceol.network.entity;

import java.sql.Timestamp;

/**
 * Created by UsielLau on 2018/1/22 0022 21:27.
 */

public class ConfIng {
    private int id;
    private String title;
    /**
     * 0为视频会议，1为演示会议
     */
    private int type;
    private String password;
    private String channelId;
    private int capacity;
    private int online;
    private String member;
    private String creator;
    private long createTime;
    private String participator;

    public ConfIng(){

    }

    public ConfIng(int id, String title, int type, String password, String channelId, int capacity,
                   int online, String member, String creator, long createTime, String participator) {
        this.id = id;
        this.title = title;
        this.type=type;
        this.password = password;
        this.channelId = channelId;
        this.capacity = capacity;
        this.online = online;
        this.member = member;
        this.creator = creator;
        this.createTime = createTime;
        this.participator = participator;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getParticipator() {
        return participator;
    }

    public void setParticipator(String participator) {
        this.participator = participator;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPassword() {
        return password;
    }


    public int getCapacity() {
        return capacity;
    }

    public String getCreator() {
        return creator;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    @Override
    public String toString() {
        return "ConfIng{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", password='" + password + '\'' +
                ", capacity=" + capacity +
                ", creator='" + creator + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
