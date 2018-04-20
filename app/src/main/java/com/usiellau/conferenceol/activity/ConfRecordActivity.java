package com.usiellau.conferenceol.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.adapter.RecordFragmentAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by UsielLau on 2018/4/20 0020 16:21.
 */
public class ConfRecordActivity extends AppCompatActivity {

    private String title="会议记录";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_top)
    TabLayout tabTop;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    RecordFragmentAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf_record);
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        pagerAdapter=new RecordFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabTop.setTabMode(TabLayout.MODE_FIXED);
        tabTop.setupWithViewPager(viewPager);
    }

}
