package com.usiellau.conferenceol.activity;

import android.app.Activity;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.gildaswise.horizontalcounter.HorizontalCounter;
import com.google.gson.Gson;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.network.ConfSvMethods;
import com.usiellau.conferenceol.network.HttpResult;
import com.usiellau.conferenceol.network.entity.ConfIng;
import com.usiellau.conferenceol.network.entity.FileDescription;
import com.usiellau.conferenceol.util.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.Orientation;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by UsielLau on 2018/3/19 0019 11:09.
 */

public class CreateSpeechActivity extends AppCompatActivity implements OnDateSetListener{

    String TAG=CreateSpeechActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_conf_title)
    EditText etConfTitle;
    @BindView(R.id.et_conf_password)
    EditText etConfPassword;
    @BindView(R.id.capacity_counter)
    HorizontalCounter capacityCounter;
    @BindView(R.id.tv_choose_file)
    TextView tvChooseFile;
    @BindView(R.id.create_forecast)
    CheckBox cbCreateForecast;
    @BindView(R.id.tv_forecast_time)
    TextView tvForecastTime;

    TimePickerDialog timePickerDialog;
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    ProgressDialog progressDialog;
    File fileSelected;
    long startTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_speech);
        ButterKnife.bind(this);
        initViews();

    }
    private void initViews(){
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("创建演示会议");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @OnClick(R.id.tv_choose_file)
    public void onClickTvChooseFile(){
        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setActivityTheme(R.style.AppTheme)
                .addFileSupport("PDF",new String[]{".pdf"})
                .withOrientation(Orientation.UNSPECIFIED)
                .pickFile(this);

    }

    @OnCheckedChanged(R.id.create_forecast)
    public void onCheckCreateForecast(boolean checked){
        if(checked){
            showTimePickerDialog();
        }else{
            tvForecastTime.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<String> docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                    addDocToView(docPaths);
                }
                break;
        }
    }

    private void addDocToView(ArrayList<String> list){
        String path=list.get(0);
        fileSelected=new File(path);
        tvChooseFile.setText(fileSelected.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_speech,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.action_submit:
                onSubmit();
                break;
            default:
                break;
        }
        return true;
    }

    private void onSubmit(){
        final String channelId= Utils.getUUID();
        final boolean hasFile=fileSelected!=null;
        Runnable afterWork=new Runnable() {
            @Override
            public void run() {
                if(cbCreateForecast.isChecked()){
                    createForecast(channelId,hasFile);
                }else{
                    createSpeech(channelId,hasFile);
                }

            }
        };

        if(hasFile){
            uploadFile(channelId,afterWork);
        }else{
            afterWork.run();
        }


    }

    private void createSpeech(final String channelId, final boolean hasFile){
        String title=etConfTitle.getText().toString();
        String password=etConfPassword.getText().toString();
        int type=1;
        int capacity=(int)(capacityCounter.getCurrentValue()/1);
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
                    Toast.makeText(CreateSpeechActivity.this, "创建speech成功", Toast.LENGTH_SHORT).show();
                    if(hasFile&&!Utils.fileExistExternalDir(CreateSpeechActivity.this,fileSelected.getName())){
                        String sp=fileSelected.getAbsolutePath();
                        String dp=Utils.getDefaultFileSavePath(CreateSpeechActivity.this)+File.separator+fileSelected.getName();
                        Utils.copySdcardFile(sp,dp);
                        Log.d(TAG,"复制文件，原路径："+sp+"目的路径："+dp);
                    }
                    Intent intent=new Intent(CreateSpeechActivity.this,SpeechActivity.class);
                    intent.putExtra("channelId",channelId);
                    intent.putExtra("identity",0);
                    intent.putExtra("roomId",confIngHttpResult.getResult().getId());
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(CreateSpeechActivity.this, "创建speech失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                closeProgressDialog();
                Toast.makeText(CreateSpeechActivity.this, "error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                closeProgressDialog();
            }
        },title,type,password,channelId,capacity,creator,hasFile);
    }

    private void uploadFile(String channelId, final Runnable afterWork){
        FileDescription fileDescription=new FileDescription(FileDescription.TYPE_CONF_FILE,channelId);
        Gson gson=new Gson();
        ConfSvMethods.getInstance().uploadFile(new Observer<HttpResult<String>>() {
            @Override
            public void onSubscribe(Disposable d) {
                showProgressDialog();
            }

            @Override
            public void onNext(HttpResult httpResult) {
                int code=httpResult.getCode();
                String msg=httpResult.getMsg();
                if(code==0){
                    Toast.makeText(CreateSpeechActivity.this, "上传文件成功", Toast.LENGTH_SHORT).show();
                    afterWork.run();
                }else{
                    Toast.makeText(CreateSpeechActivity.this, "上传文件失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                closeProgressDialog();
                Toast.makeText(CreateSpeechActivity.this, "error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                closeProgressDialog();
            }
        },gson.toJson(fileDescription),fileSelected);
    }

    public void createForecast(String channelId,boolean hasFile){
        String title=etConfTitle.getText().toString();
        String password=etConfPassword.getText().toString();
        int capacity=(int)(capacityCounter.getCurrentValue()/1);
        String creator= PreferenceManager.getDefaultSharedPreferences(this).getString("username","");
        ConfSvMethods.getInstance().createForecast(new Observer<HttpResult>() {
            @Override
            public void onSubscribe(Disposable d) {
                showProgressDialog();
            }

            @Override
            public void onNext(HttpResult httpResult) {
                int code=httpResult.getCode();
                String msg=httpResult.getMsg();
                if(code==0){
                    Toast.makeText(CreateSpeechActivity.this, "创建预告成功", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(CreateSpeechActivity.this, "创建预告失败", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(Throwable e) {
                closeProgressDialog();
                Toast.makeText(CreateSpeechActivity.this, "error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                closeProgressDialog();
            }
        },title,password,channelId,capacity,creator,hasFile,startTime);
    }


    private void showTimePickerDialog(){
        if(timePickerDialog==null){
            long oneYear = 365 * 1000 * 60 * 60 * 24L;
            timePickerDialog=new TimePickerDialog.Builder()
                    .setCallBack(this)
                    .setCancelStringId("取消")
                    .setSureStringId("确定")
                    .setTitleStringId("请选择会议开始时间")
                    .setYearText("年")
                    .setMonthText("月")
                    .setDayText("日")
                    .setHourText("时")
                    .setMinuteText("分")
                    .setCyclic(false)
                    .setThemeColor(getResources().getColor(R.color.colorPrimary))
                    .setMinMillseconds(System.currentTimeMillis())
                    .setMaxMillseconds(System.currentTimeMillis() + oneYear)
                    .setCurrentMillseconds(System.currentTimeMillis())
                    .setType(Type.ALL)
                    .build();
        }
        timePickerDialog.show(getSupportFragmentManager(),"all");
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        String time=getDateToString(millseconds);
        tvForecastTime.setText(time);
        tvForecastTime.setVisibility(View.VISIBLE);
    }
    public String getDateToString(long time) {
        startTime=time;
        Date d = new Date(time);
        return sf.format(d);
    }

    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("上传文件中...");
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        progressDialog.cancel();
    }
}
