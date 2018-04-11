package com.usiellau.conferenceol.tcp.event;

/**
 * Created by UsielLau on 2018/4/11 0011 15:37.
 */
public class ScrollEvent {

    public static final int EVENTTYPE=1;

    private int page;
    private float positionOffset;

    public ScrollEvent(int page, float positionOffset) {
        this.page = page;
        this.positionOffset = positionOffset;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public float getPositionOffset() {
        return positionOffset;
    }

    public void setPositionOffset(float positionOffset) {
        this.positionOffset = positionOffset;
    }
}
