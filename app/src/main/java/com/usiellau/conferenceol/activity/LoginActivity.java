package com.usiellau.conferenceol.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.network.ConfSvMethods;
import com.usiellau.conferenceol.network.HttpResult;
import com.usiellau.conferenceol.network.entity.User;
import com.usiellau.conferenceol.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by UsielLau on 2018/1/19 0019 4:03.
 */

public class LoginActivity extends AppCompatActivity{

    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.btn_login)
    Button btnLogin;

    AlertDialog progressDialog;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }


    @OnClick(R.id.btn_login)
    public void clickBtnLogin(){
        ConfSvMethods confSvMethods=ConfSvMethods.getInstance();
        String username=etUsername.getText().toString();
        String password=etPassword.getText().toString();
        if(!Utils.isMobiPhoneNum(username)){
            Toast.makeText(this, "非法用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        confSvMethods.login(new Observer<HttpResult<User>>() {
            @Override
            public void onSubscribe(Disposable d) {
                showProgressDialog();
            }

            @Override
            public void onNext(HttpResult<User> userHttpResult) {
                int code=userHttpResult.getCode();
                String msg=userHttpResult.getMsg();
                if(code==0){
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(LoginActivity.this,ConfManageActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                }
                Log.d("LoginActivity",userHttpResult.toString());
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
                closeProgressDialog();
            }

            @Override
            public void onComplete() {
                closeProgressDialog();
            }
        },username,password);
    }

    @OnClick(R.id.btn_register)
    public void clickBtnRegister(){
        Intent intent=new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new SpotsDialog(this,R.style.login_progress_dialog);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        progressDialog.cancel();
    }


}
