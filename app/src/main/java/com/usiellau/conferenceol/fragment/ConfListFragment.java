package com.usiellau.conferenceol.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usiellau.conferenceol.R;

/**
 * Created by UsielLau on 2018/1/22 0022 7:57.
 */

public class ConfListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_conf_list,container,false);
        SwipeRefreshLayout refreshLayout=rootView.findViewById(R.id.swipe_refresh_layout);
        RecyclerView confListRv=rootView.findViewById(R.id.rv_conf_list);
        confListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        return rootView;
    }
}
