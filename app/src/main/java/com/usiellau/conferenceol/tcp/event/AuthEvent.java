package com.usiellau.conferenceol.tcp.event;

/**
 * Created by UsielLau on 2018/4/11 0011 14:49.
 */
public class AuthEvent {

    public static final int EVENTTYPE=0;
    public static final String AUTH_SUCCESS="identity verification";

    private int roomId;
    /**
     * 0为主讲人，1为参会者
     */
    private int identity;


    public AuthEvent(int roomId, int identity) {
        this.roomId = roomId;
        this.identity = identity;
    }
    public int getRoomId() {
        return roomId;
    }
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
    public int getIdentity() {
        return identity;
    }
    public void setIdentity(int identity) {
        this.identity = identity;
    }
}
