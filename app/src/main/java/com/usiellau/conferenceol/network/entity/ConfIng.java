package com.usiellau.conferenceol.network.entity;

import java.sql.Timestamp;

/**
 * Created by UsielLau on 2018/1/22 0022 21:27.
 */

public class ConfIng {
    private int id;
    private String title;
    private String password;
    private String roomId;
    private int capacity;
    private String creator;
    private Timestamp createTime;

    public ConfIng(){

    }

    public ConfIng(int id, String title, String password, String roomId, int capacity, String creator, Timestamp createTime) {
        this.id = id;
        this.title = title;
        this.password = password;
        this.roomId = roomId;
        this.capacity = capacity;
        this.creator = creator;
        this.createTime = createTime;
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

    public String getRoomId() {
        return roomId;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getCreator() {
        return creator;
    }

    public Timestamp getCreateTime() {
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

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "ConfIng{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", password='" + password + '\'' +
                ", roomId='" + roomId + '\'' +
                ", capacity=" + capacity +
                ", creator='" + creator + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
