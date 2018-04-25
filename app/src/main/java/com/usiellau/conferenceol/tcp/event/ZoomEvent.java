package com.usiellau.conferenceol.tcp.event;

/**
 * Created by UsielLau on 2018/4/11 0011 15:38.
 */
public class ZoomEvent {

    public static final int EVENTTYPE=2;

    private float zoom;

    public ZoomEvent(float zoom){
        this.zoom=zoom;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    @Override
    public String toString() {
        return "ZoomEvent{" +
                "zoom=" + zoom +
                '}';
    }
}
