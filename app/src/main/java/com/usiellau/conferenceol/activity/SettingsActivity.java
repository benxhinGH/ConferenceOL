package com.usiellau.conferenceol.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Switch;

import com.usiellau.conferenceol.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class SettingsActivity extends AppCompatActivity {

    private final String title="设置";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.switch_server)
    Switch switchServer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }



    @OnCheckedChanged(R.id.switch_server)
    void onCheckSwitchServer(boolean isChecked){
        if(isChecked){

        }else{

        }
    }




}
