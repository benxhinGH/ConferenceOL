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

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import com.foxit.sdk.PDFViewCtrl;
import com.foxit.uiextensions.UIExtensionsManager;
import com.foxit.uiextensions.pdfreader.impl.PDFReader;
import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.foxitpdf.App;

import java.io.InputStream;

public class PDFReaderFragment extends BaseFragment {

    private final static String TAG = PDFReaderFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        InputStream stream = getActivity().getApplicationContext().getResources().openRawResource(R.raw.uiextensions_config);
        UIExtensionsManager.Config config = new UIExtensionsManager.Config(stream);
        if (!config.isLoadDefaultReader()) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity()).
                    setMessage("Default reader could not be loaded.").
                    setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }
                    }).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            return null;
        }
        PDFViewCtrl pdfViewerCtrl = new PDFViewCtrl(getActivity().getApplicationContext());
        UIExtensionsManager uiExtensionsManager = new UIExtensionsManager(getActivity().getApplicationContext(), null, pdfViewerCtrl, config);

        pdfViewerCtrl.setUIExtensionsManager(uiExtensionsManager);
        uiExtensionsManager.setAttachedActivity(getActivity());
        uiExtensionsManager.registerModule(App.instance().getLocalModule()); // use to refresh file list
        mPDFReader = (PDFReader) uiExtensionsManager.getPDFReader();
        mPDFReader.onCreate(getActivity(), pdfViewerCtrl, savedInstanceState);
        mPDFReader.openDocument(getPath(), null);
        mPDFReader.setOnFinishListener(getOnFinishListener());
        setName(mPDFReader.getName());
        return mPDFReader.getContentView();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mPDFReader != null) {
            mPDFReader.onConfigurationChanged(getActivity(), newConfig);
        }
    }



}
