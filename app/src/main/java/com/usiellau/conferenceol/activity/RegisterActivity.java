package com.usiellau.conferenceol.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.network.ConfSvMethods;
import com.usiellau.conferenceol.network.HttpResult;
import com.usiellau.conferenceol.util.Utils;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by UsielLau on 2018/1/21 0021 9:25.
 */

public class RegisterActivity extends AppCompatActivity {


    @BindView(R.id.btn_get_authcode)
    Button btnGetAuthcode;
    @BindView(R.id.et_phonenumber)
    MaterialEditText etPhonenumber;
    @BindView(R.id.et_authcode)
    MaterialEditText etAuthcode;
    @BindView(R.id.et_password)
    MaterialEditText etPassword;

    SpotsDialog progressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("注册");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    
    
    @OnClick(R.id.btn_get_authcode)
    public void onClickBtnGetAuthcode(){
        String number=etPhonenumber.getText().toString();
        if(Utils.isMobiPhoneNum(number)){
            ConfSvMethods.getInstance().sendAuthcode(new Observer<HttpResult>() {
                @Override
                public void onSubscribe(Disposable d) {
                    showProgressDialog();
                }

                @Override
                public void onNext(HttpResult httpResult) {
                    int code=httpResult.getCode();
                    String msg=httpResult.getMsg();
                    Log.d("RegisterActivity","code:"+code+"msg:"+msg);
                    if(code==0){
                        Toast.makeText(RegisterActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                        btnGetAuthcode.setEnabled(false);
                        new CountDownTimer(60000,1000){
                            @Override
                            public void onTick(long millisUntilFinished) {
                                btnGetAuthcode.setText(millisUntilFinished/1000+"秒");
                            }
                            @Override
                            public void onFinish() {
                                btnGetAuthcode.setEnabled(true);
                                btnGetAuthcode.setText("获取验证码");
                            }
                        }.start();
                    }else{
                        Toast.makeText(RegisterActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    closeProgressDialog();
                    Toast.makeText(RegisterActivity.this, "error", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete() {
                    closeProgressDialog();
                }
            },number);
        }else{
            Toast.makeText(this, "非法手机号", Toast.LENGTH_SHORT).show();
        }
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_submit:
                String phonenumber=etPhonenumber.getText().toString();
                String authcode=etAuthcode.getText().toString();
                String password=etPassword.getText().toString();
                ConfSvMethods.getInstance().register(new Observer<HttpResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showProgressDialog();
                    }

                    @Override
                    public void onNext(HttpResult httpResult) {
                        int code=httpResult.getCode();
                        String msg=httpResult.getMsg();
                        Log.d("RegisterActivity","code:"+code+"msg:"+msg);
                        if(code==0){
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        closeProgressDialog();
                        Toast.makeText(RegisterActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        closeProgressDialog();
                    }
                },phonenumber,authcode,password);
                break;
            default:
                break;
        }
        return true;
    }

}
