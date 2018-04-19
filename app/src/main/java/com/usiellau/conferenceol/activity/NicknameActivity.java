package com.usiellau.conferenceol.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.network.ConfSvMethods;
import com.usiellau.conferenceol.network.HttpResult;
import com.usiellau.conferenceol.network.entity.UserUpdateInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by UsielLau on 2018/4/19 0019 10:20.
 */
public class NicknameActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_nickname)
    EditText etNickname;

    ProgressDialog progressDialog;

    String oldNickname;
    String newNickname;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        oldNickname=PreferenceManager.getDefaultSharedPreferences(this).getString("nickname","null");
        etNickname.setText(oldNickname);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nickname,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_confirm:
                if(checkIfNew()){
                    updateUserNicknameAndFinish();
                }else{
                    finish();
                }
                break;
                default:
                    break;
        }
        return true;
    }

    private void updateUserNicknameAndFinish(){
        UserUpdateInfo info=new UserUpdateInfo(UserUpdateInfo.TYPE_NICKNAME,newNickname,
                PreferenceManager.getDefaultSharedPreferences(this).getString("username",""));
        ConfSvMethods.getInstance().updateUserInfo(new Observer<HttpResult>() {
            @Override
            public void onSubscribe(Disposable d) {
                showProgressDialog();
            }

            @Override
            public void onNext(HttpResult httpResult) {
                if(httpResult.getCode()==0){
                    Toast.makeText(NicknameActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                    PreferenceManager.getDefaultSharedPreferences(NicknameActivity.this).edit()
                            .putString("nickname",newNickname)
                            .apply();
                }else{
                    Toast.makeText(NicknameActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                }
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(NicknameActivity.this, "error", Toast.LENGTH_SHORT).show();
                closeProgressDialog();
            }

            @Override
            public void onComplete() {
                closeProgressDialog();
            }
        },info);
    }

    private boolean checkIfNew(){
        newNickname=etNickname.getText().toString().trim();
        if(newNickname.equals(oldNickname))return false;
        else return true;
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
