package com.usiellau.conferenceol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.usiellau.conferenceol.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by UsielLau on 2018/1/19 0019 3:55.
 */

public class GuideActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doSkip();
            }
        },2000);
    }



    private void doSkip(){
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
