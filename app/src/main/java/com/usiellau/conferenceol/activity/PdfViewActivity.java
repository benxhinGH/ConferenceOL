package com.usiellau.conferenceol.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;


import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.usiellau.conferenceol.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by UsielLau on 2018/3/23 0023 11:35.
 */

public class PdfViewActivity extends AppCompatActivity {

    @BindView(R.id.pdfview)
    PDFView pdfView;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        ButterKnife.bind(this);
        String fileName=getIntent().getStringExtra("fileName");
        File[] files=getExternalFilesDir(null).listFiles();
        File theOne=null;
        for(File file:files){
            if(file.getName().equals(fileName)){
                theOne=file;
                break;
            }
        }

        pdfView.fromFile(theOne)
                .defaultPage(0)
                .spacing(10) // in dp
                .load();



    }
}
