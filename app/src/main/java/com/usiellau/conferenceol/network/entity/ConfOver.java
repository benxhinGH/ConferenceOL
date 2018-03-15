package com.usiellau.conferenceol.network.entity;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by UsielLau on 2018/1/23 0023 21:20.
 */

public class ConfOver {
    private int id;
    private String title;
    private String creator;
    private Timestamp createTime;
    private int duration;
    private String participator;
    public ConfOver(int id, String title, String creator, Timestamp createTime,
                    int duration, String participator) {
        this.id = id;
        this.title = title;
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

    public Timestamp getCreateTime() {
        return createTime;
    }

    public int getDuration() {
        return duration;
    }

    public String getParticipator() {
        return participator;
    }
}
