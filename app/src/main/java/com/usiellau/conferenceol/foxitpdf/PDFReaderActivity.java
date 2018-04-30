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
package com.usiellau.conferenceol.foxitpdf;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.WindowManager;


import com.foxit.uiextensions.pdfreader.impl.PDFReader;
import com.foxit.uiextensions.utils.AppTheme;
import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.foxitpdf.fragment.BaseFragment;
import com.usiellau.conferenceol.foxitpdf.fragment.EmptyViewFragment;
import com.usiellau.conferenceol.foxitpdf.fragment.PDFReaderFragment;

public class PDFReaderActivity extends FragmentActivity implements PDFReader.OnFinishListener {
    private FragmentManager mFragmentManager;

    private BaseFragment currentFragment;

    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppTheme.setThemeFullScreen(this);
        AppTheme.setThemeNeedMenuKey(this);
        setContentView(R.layout.activity_reader);
//        filePath = AppFileUtil.getFilePath(this, getIntent(), IHomeModule.FILE_EXTRA);
        filePath=getIntent().getStringExtra("filePath");
        mFragmentManager = getSupportFragmentManager();
        if(!App.instance().checkLicense())
            openEmptyView();
        else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            openDocView();
        }
    }

    /**
     * open a Doc View use Fragment
     */
    private void openDocView(){
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        PDFReaderFragment fragment = new PDFReaderFragment();
        fragment.setPath(filePath);
        fragment.setOnFinishListener(this);
        fragmentTransaction.replace(R.id.reader_container, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * when App license is valid, it should open a empty view use Fragment also.
     */
    private void openEmptyView(){
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.reader_container, new EmptyViewFragment());
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        BaseFragment currentFrag = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.reader_container);
        setSelectedFragment(currentFrag);
        if (this.currentFragment.mPDFReader != null && this.currentFragment.mPDFReader.onKeyDown(this, keyCode, event))
            return true;
        return super.onKeyDown(keyCode, event);
    }

    private void setSelectedFragment(BaseFragment fragment){
        this.currentFragment = fragment;
    }

    private void finishActivity() {
        this.finish();
    }

    @Override
    public void onFinish() {
        finish();
    }
}
