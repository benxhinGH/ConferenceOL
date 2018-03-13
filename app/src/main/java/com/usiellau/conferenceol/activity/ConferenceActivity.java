package com.usiellau.conferenceol.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.adapter.PartpListAdapter;
import com.usiellau.conferenceol.network.ConfSvMethods;
import com.usiellau.conferenceol.network.HttpResult;
import com.usiellau.conferenceol.network.entity.ConfIng;
import com.usiellau.conferenceol.tools.IdConverter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by UsielLau on 2018/2/21 0021 23:01.
 */

public class ConferenceActivity extends AppCompatActivity {

    private static final String LOG_TAG = ConferenceActivity.class.getSimpleName();

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private static final int PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1;

    private boolean isVideoSend=true;
    private boolean isAudioSend=true;
    private boolean isSpeaker=false;
    private boolean isRemoteAudio=true;



    @BindView(R.id.video_main)
    FrameLayout videoMain;
    @BindView(R.id.controlLayout)
    View mControlLayout;
    @BindView(R.id.btnSpeaker)
    Button mBtnSpeaker;
    @BindView(R.id.btnSendAudio)
    Button mBtnSendAudio;
    @BindView(R.id.btnSendVideo)
    Button mBtnSendVideo;
    @BindView(R.id.partp_list)
    RecyclerView partpList;
    private PartpListAdapter adapter;

    private RtcEngine mRtcEngine;

    private String channelId;

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1


        @Override
        public void onUserJoined(final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.onRemoteUserJoined(uid);
                }
            });
        }

        @Override
        public void onUserOffline(final int uid, int reason) { // Tutorial Step 7
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.onRemoteUserLeft(uid);
                }
            });
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) { // Tutorial Step 10
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.onRemoteUserVideoMuted(uid,muted);
                }
            });
        }

        @Override
        public void onUserMuteAudio(final int uid, final boolean muted) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.onRemoteUserAudioMuted(uid, muted);
                }
            });
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conference);
        ButterKnife.bind(this);
        channelId=getIntent().getStringExtra("channelId");
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)) {
            initAgoraEngineAndJoinChannel();
        }
        partpList.setLayoutManager(new LinearLayoutManager(this));
        adapter=new PartpListAdapter(this,mRtcEngine);
        partpList.setAdapter(adapter);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initRemoteVideo();
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA);
                } else {
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
                    finish();
                }
                break;
            }
            case PERMISSION_REQ_ID_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                } else {
                    showLongToast("No permission for " + Manifest.permission.CAMERA);
                    finish();
                }
                break;
            }
        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    public void onSwitchCamera(View view) {
        mRtcEngine.switchCamera();
    }

    public void onSendAudio(View view) {
        mRtcEngine.muteLocalAudioStream(isAudioSend);
        isAudioSend=!isAudioSend;
    }

    public void onSendVideo(View view) {
        mRtcEngine.muteLocalVideoStream(isVideoSend);
        isVideoSend=!isVideoSend;
        SurfaceView surfaceView=(SurfaceView)videoMain.getChildAt(0);
        surfaceView.setVisibility(isVideoSend?View.VISIBLE:View.GONE);
    }

    public void onAudioOut(View view) {
        mRtcEngine.muteAllRemoteAudioStreams(isRemoteAudio);
        isRemoteAudio=!isRemoteAudio;
    }

    public void onSpeaker(View view) {
        mRtcEngine.setEnableSpeakerphone(!isSpeaker);
        isSpeaker=!isSpeaker;
    }

    public void onLeave(View view) {
        mRtcEngine.leaveChannel();
        ConfSvMethods.getInstance().leaveRoom(new Observer<HttpResult>() {
            @Override
            public void onSubscribe(Disposable d) {

            }
            @Override
            public void onNext(HttpResult httpResult) {
                int code=httpResult.getCode();
                if(code==0||code==1){
                    Toast.makeText(ConferenceActivity.this, "离开房间成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ConferenceActivity.this, "离开房间时出错,code"+httpResult.getCode()+"msg:"+httpResult.getMsg(), Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onError(Throwable e) {

            }
            @Override
            public void onComplete() {

            }
        },channelId, PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("username",""));
        finish();
    }




    private void initAgoraEngineAndJoinChannel(){
        initializeAgoraEngine();     // Tutorial Step 1
        setupVideoProfile();         // Tutorial Step 2
        setupLocalVideo();           // Tutorial Step 3
        joinChannel();
    }
    // Tutorial Step 1
    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_sdk_id), mRtcEventHandler);
            mRtcEngine.setDefaultAudioRoutetoSpeakerphone(false);
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    // Tutorial Step 2
    private void setupVideoProfile() {
        mRtcEngine.enableVideo();
        mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P, false);
    }

    // Tutorial Step 3
    private void setupLocalVideo() {
        FrameLayout container = findViewById(R.id.video_main);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE,
                PreferenceManager.getDefaultSharedPreferences(this).getInt("uid",0)));
    }

    // Tutorial Step 4
    private void joinChannel() {
        mRtcEngine.joinChannel(null, channelId, "Extra Optional Data",
                PreferenceManager.getDefaultSharedPreferences(this).getInt("uid",0)); // if you do not specify the uid, we will generate the uid for you
    }

    private void initRemoteVideo(){
        ConfSvMethods.getInstance().queryConfIng(new Observer<HttpResult<List<ConfIng>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(HttpResult<List<ConfIng>> listHttpResult) {
                if(listHttpResult.getResult().size()==0){
                    Toast.makeText(ConferenceActivity.this, "error", Toast.LENGTH_SHORT).show();
                    return;
                }
                ConfIng confIng=listHttpResult.getResult().get(0);
                IdConverter idConverter=new IdConverter(confIng.getMember());
                adapter.setUidList(idConverter.getList());

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        },"one",channelId);
    }








}
