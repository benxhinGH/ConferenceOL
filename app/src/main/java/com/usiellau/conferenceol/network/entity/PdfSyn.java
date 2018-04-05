package com.usiellau.conferenceol.network.entity;

/**
 * Created by UsielLau on 2018/4/5 0005 9:11.
 */

public class PdfSyn {
    private PdfAction action;
    private float eventX;
    private float eventY;
    private float scale;
    private float distanceX;
    private float distanceY;

    public PdfSyn(){

    }


    public PdfAction getAction() {
        return action;
    }

    public void setAction(PdfAction action) {
        this.action = action;
    }

    public float getEventX() {
        return eventX;
    }

    public void setEventX(float eventX) {
        this.eventX = eventX;
    }

    public float getEventY() {
        return eventY;
    }

    public void setEventY(float eventY) {
        this.eventY = eventY;
    }

    public float getDistanceX() {
        return distanceX;
    }

    public void setDistanceX(float distanceX) {
        this.distanceX = distanceX;
    }

    public float getDistanceY() {
        return distanceY;
    }

    public void setDistanceY(float distanceY) {
        this.distanceY = distanceY;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    enum PdfAction{
        scroll,zoom;
    }
}
