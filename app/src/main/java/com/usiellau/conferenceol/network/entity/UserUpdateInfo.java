package com.usiellau.conferenceol.network.entity;

/**
 * Created by UsielLau on 2018/4/19 0019 10:32.
 */
public class UserUpdateInfo {

    public static final int TYPE_NICKNAME=0;
    public static final int TYPE_PASSWORD=1;

    private int type;
    private String value;
    private String username;

    public UserUpdateInfo(int type, String value,String username) {
        this.type = type;
        this.value = value;
        this.username=username;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserUpdateInfo{" +
                "type=" + type +
                ", value='" + value + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
