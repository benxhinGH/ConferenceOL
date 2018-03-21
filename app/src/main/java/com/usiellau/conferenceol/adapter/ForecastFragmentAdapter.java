package com.usiellau.conferenceol.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.usiellau.conferenceol.fragment.ForecastFragment;

/**
 * Created by UsielLau on 2018/3/21 0021 14:06.
 */

public class ForecastFragmentAdapter extends FragmentPagerAdapter {


    public ForecastFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new ForecastFragment();
        Bundle args = new Bundle();
        if(position==0){
            args.putBoolean("isMyForecast",false);
        }else if(position==1){
            args.putBoolean("isMyForecast",true);
        }
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
        String title=null;
        switch (position){
            case 0:
                title="所有预告";
                break;
            case 1:
                title="我的预告";
                break;
            default:
                break;
        }
        return title;
    }
}
