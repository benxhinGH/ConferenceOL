package com.usiellau.conferenceol.activity;

import android.app.ProgressDialog;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnMoveListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.listener.OnScaleListener;
import com.github.barteksc.pdfviewer.listener.OnZoomListener;
import com.google.gson.Gson;
import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.network.ConfSvMethods;
import com.usiellau.conferenceol.network.HttpResult;
import com.usiellau.conferenceol.network.entity.ConfFile;
import com.usiellau.conferenceol.tcp.ConnectionClient;
import com.usiellau.conferenceol.tcp.callback.RequestCallBack;
import com.usiellau.conferenceol.tcp.event.AuthEvent;
import com.usiellau.conferenceol.tcp.event.MoveEvent;
import com.usiellau.conferenceol.tcp.event.ScaleEvent;
import com.usiellau.conferenceol.tcp.event.ScrollEvent;
import com.usiellau.conferenceol.tcp.event.ZoomEvent;
import com.usiellau.conferenceol.tcp.protocol.BasicProtocol;
import com.usiellau.conferenceol.tcp.protocol.DataAckProtocol;
import com.usiellau.conferenceol.tcp.protocol.DataProtocol;
import com.usiellau.conferenceol.util.Utils;

import java.io.File;
import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by UsielLau on 2018/3/20 0020 12:01.
 */

public class SpeechActivity extends AppCompatActivity {

    String TAG=SpeechActivity.class.getSimpleName();

    @BindView(R.id.pdfview)
    PDFView pdfView;
    @BindView(R.id.btnLeave)
    Button btnLeave;
    @BindView(R.id.btnSendAudio)
    Button btnSendAudio;
    @BindView(R.id.btnSpeaker)
    Button btnSpeaker;

    boolean isSpeaker=true;
    boolean isAudioSend=true;

    Gson gson=new Gson();

    ConnectionClient client;

    RequestCallBack clientCallback=new RequestCallBack() {
        @Override
        public void onSuccess(BasicProtocol msg) {
            if(msg.getProtocolType()==1){
                DataAckProtocol dataAck=(DataAckProtocol)msg;

                Log.d(TAG,"收到dataAck："+dataAck.toString()+"getUnused:"+dataAck.getAckMsgId());
                if(dataAck.getAckMsgId()==999){
                    prepareConfFile(channelId);
                }
            }else if(msg.getProtocolType()==0){
                DataProtocol data=(DataProtocol)msg;
                switch (data.getPattion()){
                    case ZoomEvent.EVENTTYPE:
                        ZoomEvent zoomEvent=gson.fromJson(data.getData(),ZoomEvent.class);
                        pdfView.zoomTo(zoomEvent.getZoom());
                        Log.d(TAG,"收到zoom事件:"+zoomEvent.toString());
                        break;
                    case MoveEvent.EVENTTYPE:
                        MoveEvent moveEvent=gson.fromJson(data.getData(),MoveEvent.class);
                        pdfView.moveTo(moveEvent.getOffsetX(),moveEvent.getOffsetY());
                        pdfView.loadPages();
                        Log.d(TAG,"收到move事件:"+moveEvent.toString());
                        break;
                        default:
                            break;
                }
            }
            Log.d(TAG,"onSuccess,"+msg);
        }

        @Override
        public void onFailed(int errorCode, String msg) {
            Log.d(TAG,"onFailed,errorCode:"+errorCode+",msg:"+msg);
        }
    };

    String fileName;

    String channelId;
    int identity;
    int roomId;

    ProgressDialog progressDialog;

    RtcEngine mRtcEngine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        ButterKnife.bind(this);
        channelId=getIntent().getStringExtra("channelId");
        identity=getIntent().getIntExtra("identity",-1);
        roomId=getIntent().getIntExtra("roomId",-1);

        requestTcpLongCon();

        if(identity==AuthEvent.TYPE_SPEAKER){
            joinAudioChannel(Constants.CLIENT_ROLE_BROADCASTER);
            btnSpeaker.setVisibility(View.GONE);
        }else if(identity==AuthEvent.TYPE_PARTICIPATOR){
            joinAudioChannel(Constants.CLIENT_ROLE_AUDIENCE);
            btnSendAudio.setVisibility(View.GONE);
        }
    }

    private void joinAudioChannel(int role){
        try {
            mRtcEngine=RtcEngine.create(this,getResources().getString(R.string.agora_sdk_id),null);
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.setClientRole(role);
            mRtcEngine.joinChannel(null,channelId,null, PreferenceManager.getDefaultSharedPreferences(this).getInt("uid",0));
            if(role==Constants.CLIENT_ROLE_AUDIENCE){
                mRtcEngine.setDefaultAudioRoutetoSpeakerphone(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareConfFile(String channelId){
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
                    String localPath=Utils.getDefaultFileSavePath(SpeechActivity.this)+File.separator+confFile.getName();
                    Log.d(TAG,"文件存储路径："+localPath);
                    ConfSvMethods.getInstance().downloadFile(new Observer<Boolean>() {
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
                    },confFile.getPath(),localPath);
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
                    identityAuth();
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

    private void identityAuth(){
        Log.d(TAG,"身份认证");
        AuthEvent authEvent=new AuthEvent(roomId,identity);
        DataProtocol dataProtocol=new DataProtocol();
        dataProtocol.setPattion(0);
        dataProtocol.setData(gson.toJson(authEvent,AuthEvent.class));
        client.addNewRequest(dataProtocol);
        Log.d(TAG,"进行身份认证，等待数据返回");
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

        if(identity==0){
            //身份为主讲人，为pdfView注册监听器，发送事件
            pdfView.fromFile(theOne)
                    .defaultPage(0)
                    .onZoom(new OnZoomListener() {
                        @Override
                        public void onZoom(float zoom) {
                            ZoomEvent event=new ZoomEvent(zoom);
                            DataProtocol dataProtocol=new DataProtocol();
                            dataProtocol.setPattion(ZoomEvent.EVENTTYPE);
                            dataProtocol.setData(gson.toJson(event));
                            client.addNewRequest(dataProtocol);
                            Log.d(TAG,"发送zoomEvent："+event.toString());
                        }
                    })
                    .onMove(new OnMoveListener() {
                        @Override
                        public void onMove(float offsetX, float offsetY) {
                            MoveEvent event=new MoveEvent(offsetX, offsetY);
                            DataProtocol dataProtocol=new DataProtocol();
                            dataProtocol.setPattion(MoveEvent.EVENTTYPE);
                            dataProtocol.setData(gson.toJson(event));
                            client.addNewRequest(dataProtocol);
                            Log.d(TAG,"发送moveEvent："+event.toString());
                        }
                    })
                    .spacing(10) // in dp
                    .load();
        }else if(identity==1){
            pdfView.fromFile(theOne)
                    .defaultPage(0)
                    .spacing(10) // in dp
                    .load();


        }


    }

    @OnClick(R.id.btnLeave)
    void onClickBtnLeave(){
        finish();
    }

    @OnClick(R.id.btnSpeaker)
    void onClickBtnSpeaker(){
        isSpeaker=!isSpeaker;
        mRtcEngine.setEnableSpeakerphone(isSpeaker);
        if(isSpeaker){
            btnSpeaker.setBackgroundResource(R.drawable.ic_speaker_phone_blue_24dp);
        }else{
            btnSpeaker.setBackgroundResource(R.drawable.ic_speaker_phone_white_24dp);
        }
    }

    @OnClick(R.id.btnSendAudio)
    void onClickBtnSendAudio(){
        mRtcEngine.muteLocalAudioStream(isAudioSend);
        isAudioSend=!isAudioSend;
        if(isAudioSend){
            btnSendAudio.setBackgroundResource(R.drawable.ic_mic_white_24dp);
        }else{
            btnSendAudio.setBackgroundResource(R.drawable.ic_mic_off_blue_24dp);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConfSvMethods.getInstance().leaveRoom(new Observer<HttpResult>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(HttpResult httpResult) {
                if(httpResult.getCode()==0||httpResult.getCode()==1){
                    Toast.makeText(SpeechActivity.this, "离开房间成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SpeechActivity.this, "离开房间出错", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(SpeechActivity.this, "error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        },channelId,PreferenceManager.getDefaultSharedPreferences(this).getString("username",""));
        client.closeConnect();
        mRtcEngine.leaveChannel();
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
