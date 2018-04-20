package com.usiellau.conferenceol.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.usiellau.conferenceol.fragment.ConfRecordFragment;

/**
 * Created by UsielLau on 2018/4/20 0020 16:25.
 */
public class RecordFragmentAdapter extends FragmentPagerAdapter {

    public RecordFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        ConfRecordFragment fragment=new ConfRecordFragment();
        Bundle args=new Bundle();
        args.putInt("confType",position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title="";
        switch (position){
            case 0:
                title="视频会议";
                break;
            case 1:
                title="演示会议";
                break;
                default:
                    break;
        }
        return title;
    }
}
