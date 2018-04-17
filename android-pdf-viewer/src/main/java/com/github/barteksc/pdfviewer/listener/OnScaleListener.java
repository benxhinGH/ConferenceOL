package com.github.barteksc.pdfviewer.listener;

import android.graphics.PointF;

/**
 * Created by UsielLau on 2018/4/17 0017 10:35.
 */
public interface OnScaleListener {
    void onScale(float dzoom, PointF pivot);
}
