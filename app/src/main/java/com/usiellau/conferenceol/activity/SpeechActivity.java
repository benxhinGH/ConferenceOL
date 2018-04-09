package com.usiellau.conferenceol.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.listener.OnZoomListener;
import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.network.ConfSvMethods;
import com.usiellau.conferenceol.network.HttpResult;
import com.usiellau.conferenceol.network.entity.ConfFile;
import com.usiellau.conferenceol.tcp.ConnectionClient;
import com.usiellau.conferenceol.tcp.callback.RequestCallBack;
import com.usiellau.conferenceol.tcp.protocol.BasicProtocol;
import com.usiellau.conferenceol.tcp.protocol.DataProtocol;
import com.usiellau.conferenceol.util.Utils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by UsielLau on 2018/3/20 0020 12:01.
 */

public class SpeechActivity extends AppCompatActivity {

    String TAG=SpeechActivity.class.getSimpleName();

    @BindView(R.id.pdfview)
    PDFView pdfView;
    @BindView(R.id.btn_test)
    Button testBtn;

    ConnectionClient client;

    RequestCallBack clientCallback=new RequestCallBack() {
        @Override
        public void onSuccess(BasicProtocol msg) {
            Log.d(TAG,"onSuccess,"+msg);
        }

        @Override
        public void onFailed(int errorCode, String msg) {
            Log.d(TAG,"onFailed,errorCode:"+errorCode+",msg:"+msg);
        }
    };

    String fileName;


    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        ButterKnife.bind(this);

        String channelId=getIntent().getStringExtra("channelId");

        ConfSvMethods.getInstance().queryConfFile(new Observer<HttpResult<ConfFile>>() {
            @Override
            public void onSubscribe(Disposable d) {
                showProgressDialog();
            }

            @Override
            public void onNext(HttpResult<ConfFile> confFileHttpResult) {
                ConfFile confFile=confFileHttpResult.getResult();
                fileName=confFile.getName();
                boolean fileExist=Utils.fileExistExternalDir(SpeechActivity.this,confFile.getName());
                if(!fileExist){
                    Log.d("SpeechActivity","文件不存在，向服务器下载");
                    ConfSvMethods.getInstance().downloadConfFile(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            showProgressDialog();
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if(aBoolean){
                                startSpeech();
                            }else{
                                Toast.makeText(SpeechActivity.this, "出错", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            closeProgressDialog();
                            Toast.makeText(SpeechActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {
                            Toast.makeText(SpeechActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    },confFile.getPath(),Utils.getDefaultFileSavePath(SpeechActivity.this)+confFile.getName());
                }else{
                    Log.d("SpeechActivity","文件已存在");
                    startSpeech();
                }
            }

            @Override
            public void onError(Throwable e) {
                closeProgressDialog();
                Toast.makeText(SpeechActivity.this, "error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                closeProgressDialog();
            }
        },channelId);

        requestTcpLongCon();
    }

    private void requestTcpLongCon(){
        Log.d(TAG,"请求建立tcp长连接");
        ConfSvMethods.getInstance().requestTcpLongConnection(new Observer<HttpResult>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(HttpResult httpResult) {
                int code=httpResult.getCode();
                if(code==0){
                    buildTcpLongConnection();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(SpeechActivity.this, "error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void buildTcpLongConnection(){
        Log.d(TAG,"建立tcp长连接");
        client=new ConnectionClient(clientCallback);
    }

    private void startSpeech(){
        Log.d("SpeechActivity","开始会议");
        File[] files=getExternalFilesDir(null).listFiles();
        File theOne=null;
        for(File file:files){
            if(file.getName().equals(fileName)){
                theOne=file;
                break;
            }
        }

        pdfView.fromFile(theOne)
                .defaultPage(0)
                .onPageScroll(new OnPageScrollListener() {
                    @Override
                    public void onPageScrolled(int page, float positionOffset) {
                        Log.d("SpeechActivity","OnScroll事件：page:"+page+",positionOffset:"+positionOffset);
                    }
                })
                .onZoom(new OnZoomListener() {
                    @Override
                    public void onZoom(float centerX, float centerY, float scale) {
                        Log.d("SpeechActivity","OnZoom事件：centerX:"+centerX+",centerY:"+centerY+",scale:"+scale);
                    }
                })
                .spacing(10) // in dp
                .load();
    }

    @OnClick(R.id.btn_test)
    public void onClickTest(){
        DataProtocol data=new DataProtocol();
        data.setData("hi,imclient");
        client.addNewRequest(data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.closeConnect();
    }

    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在准备会议...");
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        progressDialog.cancel();
    }
}
