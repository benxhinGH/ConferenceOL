package com.usiellau.conferenceol.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.network.ConfSvMethods;
import com.usiellau.conferenceol.network.HttpResult;
import com.usiellau.conferenceol.network.entity.ConfIng;
import com.usiellau.conferenceol.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    ProgressDialog progressDialog;


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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_conf,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_submit:
                createRoom();
                break;
                default:
                    break;
        }
        return true;
    }


    private void createRoom(){
        final String channelId= Utils.getUUID();
        String title=etConfTitle.getText().toString();
        String password=etConfPassword.getText().toString();
        int capacity=(int)(counter.getCurrentValue()/1);
        String creator= PreferenceManager.getDefaultSharedPreferences(this).getString("username","");

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
                    enterRoom(channelId);
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
        },title,0,password,channelId,capacity,creator,false);
    }



    private void enterRoom(final String channelId){
        Log.d("CreateConfActivity","进入房间："+channelId);
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
                    intent.putExtra("channelId",channelId);
                    startActivity(intent);
                    finish();
                }else{
                    Log.d(TAG,"进入房间失败,code："+code+"msg:"+msg);
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
        },channelId,PreferenceManager.getDefaultSharedPreferences(this).getString("username",""));
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
}
