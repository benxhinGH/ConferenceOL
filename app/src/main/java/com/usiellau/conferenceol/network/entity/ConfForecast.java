package com.usiellau.conferenceol.network.entity;

/**
 * Created by UsielLau on 2018/3/20 0020 21:10.
 */

public class ConfForecast {
    private int id;
    private String title;
    private String password;
    private String channelId;
    private int capacity;
    private String creator;
    private boolean hasfile;
    private long startTime;

    public ConfForecast(){}


    public ConfForecast(int id, String title, String password,
                        String channelId, int capacity, String creator, boolean hasfile,
                        long startTime) {
        this.id = id;
        this.title = title;
        this.password = password;
        this.channelId = channelId;
        this.capacity = capacity;
        this.creator = creator;
        this.hasfile = hasfile;
        this.startTime = startTime;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getChannelId() {
        return channelId;
    }


    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }


    public int getCapacity() {
        return capacity;
    }


    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }


    public String getCreator() {
        return creator;
    }


    public void setCreator(String creator) {
        this.creator = creator;
    }


    public boolean isHasfile() {
        return hasfile;
    }


    public void setHasfile(boolean hasfile) {
        this.hasfile = hasfile;
    }


    public long getStartTime() {
        return startTime;
    }


    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
