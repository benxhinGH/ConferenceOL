/**
 * Copyright (C) 2003-2018, Foxit Software Inc..
 * All Rights Reserved.
 * <p>
 * http://www.foxitsoftware.com
 * <p>
 * The following code is copyrighted and is the proprietary of Foxit Software Inc.. It is not allowed to
 * distribute any parts of Foxit Mobile PDF SDK to third party or public without permission unless an agreement
 * is signed between Foxit Software Inc. and customers to explicitly grant customers permissions.
 * Review legal.txt for additional license and legal information.
 */
package com.usiellau.conferenceol.foxitpdf.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.foxit.uiextensions.pdfreader.impl.PDFReader;

public class BaseFragment extends Fragment {

    public PDFReader mPDFReader;

    public String name;

    private String path;

    private long fId;

    private PDFReader.OnFinishListener onFinishListener;

    @Override
    public void onStart() {
        super.onStart();
        if (mPDFReader != null)
            mPDFReader.onStart(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mPDFReader != null) {
            mPDFReader.onStop(getActivity());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPDFReader != null)
            mPDFReader.onPause(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPDFReader != null)
            mPDFReader.onResume(getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mPDFReader!=null)
            mPDFReader.onDestroy(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setFId(long tag) {
        this.fId = tag;
    }

    public long getFId() {
        return fId;
    }

    public void setOnFinishListener(PDFReader.OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }

    public PDFReader.OnFinishListener getOnFinishListener() {
        return onFinishListener;
    }
}
