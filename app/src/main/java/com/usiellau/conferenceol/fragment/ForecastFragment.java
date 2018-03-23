package com.usiellau.conferenceol.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.adapter.ForecastListAdapter;
import com.usiellau.conferenceol.network.ConfSvMethods;
import com.usiellau.conferenceol.network.HttpResult;
import com.usiellau.conferenceol.network.entity.ConfFile;
import com.usiellau.conferenceol.network.entity.ConfForecast;
import com.usiellau.conferenceol.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by UsielLau on 2018/3/21 0021 14:08.
 */

public class ForecastFragment extends Fragment {
    @BindView(R.id.rv_forecast)
    RecyclerView recyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    ProgressDialog progressDialog;

    boolean isMyForecast;

    ForecastListAdapter adapter;

    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_forecast,container,false);
        unbinder= ButterKnife.bind(this,rootView);
        Bundle args=getArguments();
        isMyForecast=args.getBoolean("isMyForecast");
        adapter=new ForecastListAdapter(getActivity());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
        adapter.setOnItemClickListener(new ForecastListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showForecastDetailsDialog(adapter.getDatas().get(position));
            }
        });
        refreshList();
        return rootView;
    }

    private void showForecastDetailsDialog(final ConfForecast forecast){
        View dialogView=LayoutInflater.from(getActivity()).inflate(R.layout.dialog_forecast_details,null);
        TextView tvConfTitle=dialogView.findViewById(R.id.tv_conf_title);
        TextView tvCapacity=dialogView.findViewById(R.id.tv_capacity);
        TextView tvHasfile=dialogView.findViewById(R.id.tv_hasfile);
        TextView tvCreator=dialogView.findViewById(R.id.tv_creator);
        TextView tvStartTime=dialogView.findViewById(R.id.tv_start_time);
        tvConfTitle.setText(forecast.getTitle());
        tvCapacity.setText(String.valueOf(forecast.getCapacity()));
        if(forecast.isHasfile())tvHasfile.setText("是");
        else tvHasfile.setText("否");
        tvCreator.setText(forecast.getCreator());
        tvStartTime.setText(Utils.longTime2String(forecast.getStartTime()));

        MaterialDialog dialog=new MaterialDialog.Builder(getActivity())
                .title("详细信息")
                .customView(dialogView,true)
                .positiveText("下载文件")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        onClickDownload(forecast);
                    }
                })
                .negativeText("OK")
                .build();
        if(!forecast.isHasfile())dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        dialog.show();
    }

    private void onClickDownload(ConfForecast forecast){
        ConfSvMethods.getInstance().queryConfFile(new Observer<HttpResult<ConfFile>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(HttpResult<ConfFile> confFileHttpResult) {
                Log.d("ForecastFragment","文件查询成功，要下载的文件信息如下："+confFileHttpResult.getResult().toString());
                downloadConfFile(confFileHttpResult.getResult());
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        },forecast.getChannelId());
    }

    private void downloadConfFile(ConfFile confFile){
        ConfSvMethods.getInstance().downloadConfFile(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                showProgressDialog();
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if(aBoolean){
                    Toast.makeText(getActivity(), "文件下载成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "文件下载失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                closeProgressDialog();
            }

            @Override
            public void onComplete() {
                closeProgressDialog();
            }
        },confFile.getPath(),getFileSavePath(confFile.getName()));
    }

    private String getFileSavePath(String fileName){
        String res=getActivity().getExternalFilesDir(null)+ File.separator+fileName;
        Log.d("ForecastFragment","文件存储路径为："+res);
        return res;
    }

    private void refreshList(){
        ConfSvMethods.getInstance().queryForecast(new Observer<HttpResult<List<ConfForecast>>>() {
            @Override
            public void onSubscribe(Disposable d) {
                refreshLayout.setRefreshing(true);
            }

            @Override
            public void onNext(HttpResult<List<ConfForecast>> listHttpResult) {
                int code=listHttpResult.getCode();
                String msg=listHttpResult.getMsg();
                List<ConfForecast> list=listHttpResult.getResult();
                if(code==0){
                    Toast.makeText(getActivity(), "刷新成功", Toast.LENGTH_SHORT).show();
                    if(isMyForecast){
                        list=filterDatas(list);
                    }
                    adapter.setDatas(list);

                }else {
                    Toast.makeText(getActivity(), "刷新失败", Toast.LENGTH_SHORT).show();
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

    private List<ConfForecast> filterDatas(List<ConfForecast> datas){
        List<ConfForecast> res=new ArrayList<>();
        String creator= PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("username","");
        for(ConfForecast forecast:datas){
            if(forecast.getCreator().equals(creator)){
                res.add(forecast);
            }
        }
        return res;
    }

    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("下载文件中...");
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        progressDialog.cancel();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
