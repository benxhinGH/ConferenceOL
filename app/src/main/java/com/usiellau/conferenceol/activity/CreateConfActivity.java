package com.usiellau.conferenceol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gildaswise.horizontalcounter.HorizontalCounter;
import com.usiellau.conferenceol.JCWrapper.JCEvent.JCConfQueryEvent;
import com.usiellau.conferenceol.JCWrapper.JCEvent.JCEvent;
import com.usiellau.conferenceol.JCWrapper.JCManager;
import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.network.ConfSvMethods;
import com.usiellau.conferenceol.network.HttpResult;
import com.usiellau.conferenceol.network.entity.ConfIng;
import com.usiellau.conferenceol.util.Utils;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by UsielLau on 2018/1/24 0024 1:36.
 */

public class CreateConfActivity extends AppCompatActivity {

    private String TAG="CreateConfActivity";

    Toolbar toolbar;

    @BindView(R.id.et_conf_title)
    EditText etConfTitle;
    @BindView(R.id.et_conf_password)
    EditText etConfPassword;
    @BindView(R.id.capacity_counter)
    HorizontalCounter counter;

    SpotsDialog progressDialog;

    Handler handler=new Handler();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_conf);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews(){
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("创建会议");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_conf,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_submit:
                createConference();
                break;
                default:
                    break;
        }
        return true;
    }

    private void createConference(){
        final String channelId= Utils.getUUID();

        if(!JCManager.getInstance().mediaChannel.join(channelId,null)){
            Toast.makeText(this, "创建失败", Toast.LENGTH_SHORT).show();
            return;
        }else{
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    JCManager.getInstance().mediaChannel.query(channelId);
                    Log.d(TAG,"创建频道成功，查询频道信息");
                }
            },1000);
        }

    }


    @Subscribe
    public void onEvent(JCEvent event) {
        if (event.getEventType() == JCEvent.EventType.CONFERENCE_QUERY) {
            JCConfQueryEvent queryEvent = (JCConfQueryEvent) event;
            if (queryEvent.result) {
                String roomId=String.valueOf(queryEvent.queryInfo.getNumber());
                String channelId=queryEvent.queryInfo.getChannelId();
                Log.d(TAG,"查询频道信息成功，roomId"+roomId+"channelId"+channelId);
                createRoom(roomId,channelId);
            } else {
                Toast.makeText(this, "查询失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createRoom(final String roomId,String channelId){
        String title=etConfTitle.getText().toString();
        String password=etConfPassword.getText().toString();
        int capacity=(int)(counter.getCurrentValue()/1);
        String creator= PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.cloud_setting_last_login_user_id),"");

        Log.d(TAG,"创建房间111111111111111111111");
        ConfSvMethods.getInstance().createConference(new Observer<HttpResult<ConfIng>>() {
            @Override
            public void onSubscribe(Disposable d) {
                showProgressDialog();
            }

            @Override
            public void onNext(HttpResult<ConfIng> confIngHttpResult) {
                int code=confIngHttpResult.getCode();
                String msg=confIngHttpResult.getMsg();
                if(code==0){
                    Toast.makeText(CreateConfActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                    enterRoom(roomId);
                }else{
                    Toast.makeText(CreateConfActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(Throwable e) {
                closeProgressDialog();
                Toast.makeText(CreateConfActivity.this, "error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                closeProgressDialog();
            }
        },title,password,roomId,channelId,capacity,creator);
    }



    private void enterRoom(final String roomId){
        Log.d("CreateConfActivity","进入房间："+roomId);
        ConfSvMethods.getInstance().enterRoom(new Observer<HttpResult>() {
            @Override
            public void onSubscribe(Disposable d) {
                showProgressDialog();
            }

            @Override
            public void onNext(HttpResult httpResult) {
                int code=httpResult.getCode();
                String msg=httpResult.getMsg();
                if(code==0){
                    Toast.makeText(CreateConfActivity.this, "进入房间成功", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(CreateConfActivity.this,ConferenceActivity.class);
                    intent.putExtra("roomId",roomId);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(CreateConfActivity.this, "进入房间失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                closeProgressDialog();
                Toast.makeText(CreateConfActivity.this, "error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                closeProgressDialog();
            }
        },roomId,PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.cloud_setting_last_login_user_id),""));
    }



    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new SpotsDialog(this,R.style.wait_progress_dialog);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        progressDialog.cancel();
    }
}
