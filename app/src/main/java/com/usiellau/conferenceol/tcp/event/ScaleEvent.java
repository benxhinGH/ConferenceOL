package com.usiellau.conferenceol.tcp.event;

import android.graphics.PointF;

/**
 * Created by UsielLau on 2018/4/17 0017 10:43.
 */
public class ScaleEvent {

    public static final int EVENTTYPE=3;

    private float dzoom;
    private PointF pivot;

    public ScaleEvent(float dzoom, PointF pivot) {
        this.dzoom = dzoom;
        this.pivot = pivot;
    }

    public float getDzoom() {
        return dzoom;
    }

    public void setDzoom(float dzoom) {
        this.dzoom = dzoom;
    }

    public PointF getPivot() {
        return pivot;
    }

    public void setPivot(PointF pivot) {
        this.pivot = pivot;
    }

    @Override
    public String toString() {
        return "ScaleEvent{" +
                "dzoom=" + dzoom +
                ", pivot=" + pivot +
                '}';
    }
}
