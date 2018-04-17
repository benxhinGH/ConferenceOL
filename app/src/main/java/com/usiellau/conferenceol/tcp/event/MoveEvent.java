package com.usiellau.conferenceol.tcp.event;

/**
 * Created by UsielLau on 2018/4/17 0017 11:13.
 */
public class MoveEvent {
    public static final int EVENTTYPE=4;
    private float offsetX;
    private float offsetY;

    public MoveEvent(float offsetX, float offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    @Override
    public String toString() {
        return "MoveEvent{" +
                "offsetX=" + offsetX +
                ", offsetY=" + offsetY +
                '}';
    }
}
