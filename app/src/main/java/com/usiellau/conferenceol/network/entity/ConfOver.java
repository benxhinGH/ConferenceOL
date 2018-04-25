package com.usiellau.conferenceol.network.entity;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by UsielLau on 2018/1/23 0023 21:20.
 */

public class ConfOver {
    private int id;
    private String title;
    private int type;
    private String creator;
    private long createTime;
    private int duration;
    private String participator;
    public ConfOver(int id, String title, int type,String creator, long createTime,
                    int duration, String participator) {
        this.id = id;
        this.title = title;
        this.type=type;
        this.creator = creator;
        this.createTime = createTime;
        this.duration = duration;
        this.participator = participator;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCreator() {
        return creator;
    }

    public long getCreateTime() {
        return createTime;
    }

    public int getDuration() {
        return duration;
    }

    public String getParticipator() {
        return participator;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ConfOver{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", creator='" + creator + '\'' +
                ", createTime=" + createTime +
                ", duration=" + duration +
                ", participator='" + participator + '\'' +
                '}';
    }
}
