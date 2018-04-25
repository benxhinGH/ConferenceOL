package com.usiellau.conferenceol.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.adapter.ConfRecordRvAdapter;
import com.usiellau.conferenceol.adapter.OnItemClickListener;
import com.usiellau.conferenceol.network.ConfSvMethods;
import com.usiellau.conferenceol.network.HttpResult;
import com.usiellau.conferenceol.network.entity.ConfOver;
import com.usiellau.conferenceol.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by UsielLau on 2018/4/20 0020 16:27.
 */
public class ConfRecordFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    ConfRecordRvAdapter adapter;

    List<ConfOver> adapterDatas;

    private int confType;

    Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_conf_record,container,false);
        unbinder= ButterKnife.bind(this,rootView);
        Bundle args=getArguments();
        confType=args.getInt("confType");
        Log.d("ConfRecordFragment","confType是："+confType);
        adapter=new ConfRecordRvAdapter(getActivity());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showConfRecordDetailsDialog(position);
            }

            @Override
            public void onItemLongClick(int position) {

            }
        });
        refreshList();
        return rootView;
    }

    private void showConfRecordDetailsDialog(int position){
        View rootView=LayoutInflater.from(getActivity()).inflate(R.layout.dialog_conf_record_details,null);
        TextView tvConfTitle=rootView.findViewById(R.id.tv_conf_title);
        TextView tvCreator=rootView.findViewById(R.id.tv_creator);
        TextView tvCreateTime=rootView.findViewById(R.id.tv_create_time);
        TextView tvDuration=rootView.findViewById(R.id.tv_duration);
        TextView tvParticipator=rootView.findViewById(R.id.tv_conf_participator);
        ConfOver data=adapterDatas.get(position);
        tvConfTitle.setText(data.getTitle());
        tvCreator.setText(data.getCreator());
        tvCreateTime.setText(Utils.longTime2String(data.getCreateTime()));
        tvDuration.setText(Utils.longPeriod2String(data.getDuration()));
        tvParticipator.setText(data.getParticipator());

        String title="";
        int iconId=0;
        if(data.getType()==0){
            title="视频会议";
            iconId=R.drawable.ic_group_black_24dp;
        } else if(data.getType()==1){
            title="演示会议";
            iconId=R.drawable.ic_ondemand_video_black_24dp;
        }

        MaterialDialog dialog=new MaterialDialog.Builder(getActivity())
                .title(title)
                .iconRes(iconId)
                .customView(rootView,true)
                .positiveText("OK")
                .build();
        dialog.show();

    }


    private void refreshList(){
        ConfSvMethods.getInstance().queryAllConfOver(new Observer<HttpResult<List<ConfOver>>>() {
            @Override
            public void onSubscribe(Disposable d) {
                refreshLayout.setRefreshing(true);
            }

            @Override
            public void onNext(HttpResult<List<ConfOver>> listHttpResult) {
                if(listHttpResult.getCode()==0){
                    List<ConfOver> datas=listHttpResult.getResult();
                    adapterDatas=filterDatas(datas);
                    adapter.setDatas(adapterDatas);
                }

            }

            @Override
            public void onError(Throwable e) {
                refreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private List<ConfOver> filterDatas(List<ConfOver> datas){
        List<ConfOver> valid=new ArrayList<>();
        for(ConfOver confOver:datas){
            if(confOver.getType()==confType){
                valid.add(confOver);
            }
        }
        return valid;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
