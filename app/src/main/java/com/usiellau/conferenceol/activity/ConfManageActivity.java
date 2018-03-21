package com.usiellau.conferenceol.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.adapter.ConfRvAdapter;
import com.usiellau.conferenceol.network.ConfSvMethods;
import com.usiellau.conferenceol.network.HttpResult;
import com.usiellau.conferenceol.network.entity.ConfIng;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by UsielLau on 2018/1/22 0022 2:49.
 */

public class ConfManageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    RecyclerView rvConfList;
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.create_conf_menu)
    FloatingActionsMenu createConfMenu;
    @BindView(R.id.video_conf)
    FloatingActionButton videoConfBtn;
    @BindView(R.id.speech_conf)
    FloatingActionButton speechConfBtn;

    ProgressDialog progressDialog;

    ConfRvAdapter confListAdapter;

    Runnable drawerClosedRunnable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf_manage);
        ButterKnife.bind(this);
        initViews();
        refreshConfList();
    }

    private void initViews(){
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("会议列表");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer=findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        rvConfList=findViewById(R.id.rv_conf_list);
        rvConfList.setLayoutManager(new LinearLayoutManager(this));
        rvConfList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        confListAdapter=new ConfRvAdapter(this,new ArrayList<ConfIng>());
        rvConfList.setAdapter(confListAdapter);
        confListAdapter.setOnItemClickListener(new ConfRvAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                showConfDetailsDialog(position);
            }
        });


        refreshLayout=findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("ConfManageActivity","onRefresh.......");
                refreshConfList();
            }
        });
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if(drawerClosedRunnable!=null){
                    drawerClosedRunnable.run();
                    drawerClosedRunnable=null;
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }

    private void showConfDetailsDialog(final int position){
        View dialogView= LayoutInflater.from(this).inflate(R.layout.dialog_conf_details,null);
        TextView tvConfTitle=dialogView.findViewById(R.id.tv_conf_title);
        TextView tvConfType=dialogView.findViewById(R.id.tv_conf_type);
        TextView tvCapacity=dialogView.findViewById(R.id.tv_capacity);
        TextView tvCreator=dialogView.findViewById(R.id.tv_creator);
        TextView tvStartTime=dialogView.findViewById(R.id.tv_start_time);
        final EditText etPassword=dialogView.findViewById(R.id.et_password);
        ConfIng confIng=confListAdapter.getData().get(position);
        tvConfTitle.setText(confIng.getTitle());
        if(confIng.getType()==0)tvConfType.setText("视频会议");
        else if(confIng.getType()==1)tvConfType.setText("演示会议");
        tvCapacity.setText(String.valueOf(confIng.getCapacity()));
        tvCreator.setText(confIng.getCreator());
        tvStartTime.setText(confIng.getCreateTime().toString());

        MaterialDialog dialog=new MaterialDialog.Builder(this)
                .title("会议信息")
                .customView(dialogView,true)
                .positiveText("进入会议")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String password=etPassword.getText().toString();
                        attemptEnterRoom(position,password);
                    }
                })
                .negativeText("取消")
                .build();
        dialog.show();

    }


    private void attemptEnterRoom(int position,String pwd){
        ConfIng conf=confListAdapter.getData().get(position);
        if(conf.getPassword().equals(pwd)){
            enterRoom(conf);
        }else{
            Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
        }
    }

    private void enterRoom(ConfIng confIng){
        ConfSvMethods.getInstance().enterRoom(new Observer<HttpResult>() {
            @Override
            public void onSubscribe(Disposable d) {
                showProgressDialog();
            }

            @Override
            public void onNext(HttpResult httpResult) {
                int code=httpResult.getCode();
                String msg=httpResult.getMsg();
                if(code!=0){
                    Log.d("ConfManageActivity","进入房间失败，code："+code+"msg："+msg);
                }

            }

            @Override
            public void onError(Throwable e) {
                closeProgressDialog();
                Toast.makeText(ConfManageActivity.this, "error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                closeProgressDialog();
            }
        },confIng.getChannelId(), PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("username",""));


        Intent intent=new Intent(this,ConferenceActivity.class);
        intent.putExtra("channelId",confIng.getChannelId());
        startActivity(intent);

        Toast.makeText(this, "进入房间"+confIng.getChannelId(), Toast.LENGTH_SHORT).show();
    }


    private void refreshConfList(){
        Log.d("ConfManageActivity","refreshConfList............");
        ConfSvMethods.getInstance().queryConfIng(new Observer<HttpResult<List<ConfIng>>>() {
            @Override
            public void onSubscribe(Disposable d) {
                refreshLayout.setRefreshing(true);
            }

            @Override
            public void onNext(HttpResult<List<ConfIng>> confIngHttpResult) {
                int code=confIngHttpResult.getCode();
                String msg=confIngHttpResult.getMsg();
                List<ConfIng> data=confIngHttpResult.getResult();
                Log.d("ConfManageActivity","会议查询记录数："+data.size());
                if(code==0){
                    confListAdapter.setData(data);
                    confListAdapter.notifyDataSetChanged();
                }else{

                }
            }

            @Override
            public void onError(Throwable e) {
                refreshLayout.setRefreshing(false);
                Toast.makeText(ConfManageActivity.this, "error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                refreshLayout.setRefreshing(false);
            }
        },"all",null);
    }

    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("请稍候...");
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        progressDialog.cancel();
    }

    /**
     * 点击手机back键时的侧滑菜单逻辑处理
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_conference_manage,menu);
        return true;
    }

    /**
     * toolbar点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search_conf:
                Toast.makeText(this, "searchconf", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }


    /**
     * 侧滑菜单点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){
            case R.id.nav_personal:
                Toast.makeText(this, "personal", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_conf_forecast:
                Toast.makeText(this, "forecast", Toast.LENGTH_SHORT).show();
                drawerClosedRunnable=new Runnable() {
                    @Override
                    public void run() {
                        Intent intent=new Intent(ConfManageActivity.this,ForecastActivity.class);
                        startActivity(intent);
                    }
                };
                break;
            case R.id.nav_conf_record:
                Toast.makeText(this, "record", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_setting:
                Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_about:
                Toast.makeText(this, "about", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @OnClick(R.id.video_conf)
    public void onClickVideoConf(){
        Intent intent=new Intent(this,CreateConfActivity.class);
        startActivity(intent);
        createConfMenu.toggle();
    }
    @OnClick(R.id.speech_conf)
    public void onClickSpeechConf(){
        Intent intent=new Intent(this,CreateSpeechActivity.class);
        startActivity(intent);
        createConfMenu.toggle();
    }

    class RunnableHolder{
        Runnable runnable;
    }
}
