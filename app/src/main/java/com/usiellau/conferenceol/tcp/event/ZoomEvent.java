package com.usiellau.conferenceol.tcp.event;

/**
 * Created by UsielLau on 2018/4/11 0011 15:38.
 */
public class ZoomEvent {

    public static final int EVENTTYPE=2;

    private float centerX;
    private float centerY;
    private float scale;

    public ZoomEvent(float centerX, float centerY, float scale) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.scale = scale;
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public String toString() {
        return "ZoomEvent{" +
                "centerX=" + centerX +
                ", centerY=" + centerY +
                ", scale=" + scale +
                '}';
    }
}
