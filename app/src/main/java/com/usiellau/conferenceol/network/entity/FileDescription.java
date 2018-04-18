package com.usiellau.conferenceol.network.entity;

/**
 * Created by UsielLau on 2018/4/18 0018 15:42.
 */
public class FileDescription {

    public static final int TYPE_CONF_FILE=0;
    public static final int TYPE_USER_HEAD_IMAGE=1;

    /**
     * 文件类型，0为会议文件，1为用户头像图片
     */
    private int type;

    /**
     * 附加信息
     */
    private String additionInfo;

    public FileDescription(int type, String additionInfo) {
        this.type = type;
        this.additionInfo = additionInfo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAdditionInfo() {
        return additionInfo;
    }

    public void setAdditionInfo(String additionInfo) {
        this.additionInfo = additionInfo;
    }

    @Override
    public String toString() {
        return "FileDescription{" +
                "type=" + type +
                ", additionInfo='" + additionInfo + '\'' +
                '}';
    }
}
