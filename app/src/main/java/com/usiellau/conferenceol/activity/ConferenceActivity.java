package com.usiellau.conferenceol.activity;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.juphoon.cloud.JCMediaDevice;
import com.juphoon.cloud.JCMediaDeviceVideoCanvas;
import com.usiellau.conferenceol.JCWrapper.JCEvent.JCConfMessageEvent;
import com.usiellau.conferenceol.JCWrapper.JCEvent.JCEvent;
import com.usiellau.conferenceol.JCWrapper.JCEvent.JCJoinEvent;
import com.usiellau.conferenceol.JCWrapper.JCManager;
import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.adapter.PartpListAdapter;
import com.usiellau.conferenceol.network.ConfSvMethods;
import com.usiellau.conferenceol.network.HttpResult;
import com.usiellau.conferenceol.util.Utils;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by UsielLau on 2018/2/21 0021 23:01.
 */

public class ConferenceActivity extends AppCompatActivity {

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

    private boolean mFullScreen;
    private String roomId;

    private JCMediaDeviceVideoCanvas videoCanvasMain;

    private Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_conference);
        ButterKnife.bind(this);
        mFullScreen = false;
        EventBus.getDefault().register(this);
        roomId=getIntent().getStringExtra("roomId");
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        switchFullScreen();
        partpList.setLayoutManager(new LinearLayoutManager(this));
        adapter=new PartpListAdapter(this);
        partpList.setAdapter(adapter);
        updateVideoMain();
        updateControlButtons();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.updatePartp();
            }
        },1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.destory();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        EventBus.getDefault().unregister(this);
        if (videoCanvasMain!=null)
        JCManager.getInstance().mediaDevice.stopVideo(videoCanvasMain);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            int direction = keyCode == KeyEvent.KEYCODE_VOLUME_UP ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER;
            int flags = AudioManager.FX_FOCUS_NAVIGATION_UP;
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            am.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, direction, flags);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onSwitchCamera(View view) {
        JCManager.getInstance().mediaDevice.switchCamera();
    }

    public void onSendAudio(View view) {
        JCManager.getInstance().mediaChannel.enableUploadAudioStream(!JCManager.getInstance().mediaChannel.getUploadLocalAudio());
    }

    public void onSendVideo(View view) {
        JCManager.getInstance().mediaChannel.enableUploadVideoStream(!JCManager.getInstance().mediaChannel.getUploadLocalVideo());
    }



    public void onSpeaker(View view) {
        JCManager.getInstance().mediaDevice.enableSpeaker(!JCManager.getInstance().mediaDevice.isSpeakerOn());
        updateControlButtons();
    }

    public void onLeave(View view) {
        JCManager.getInstance().mediaChannel.leave();
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
        },roomId, PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.cloud_setting_last_login_user_id),""));
        finish();
    }

    public void updateVideoMain(){
        videoCanvasMain=JCManager.getInstance().mediaDevice.startCameraVideo(JCMediaDevice.RENDER_FULL_CONTENT);
        videoMain.addView(videoCanvasMain.getVideoView(),0);
    }



    public void onFullScreen(View view) {
        mFullScreen = !mFullScreen;
        switchFullScreen();
    }

    @Subscribe
    public void onEvent(JCEvent event) {
        if (event.getEventType() == JCEvent.EventType.CONFERENCE_JOIN) {
            JCJoinEvent join = (JCJoinEvent) event;
            if (join.result) {
                updateVideoMain();
                Log.e("ConferenceActivity","收到CONFERENCE_JOIN消息，调用updatePartp");
                adapter.updatePartp();
            } else {
                finish();
            }
        } else if (event.getEventType() == JCEvent.EventType.CONFERENCE_LEAVE) {
            finish();
        } else if (event.getEventType() == JCEvent.EventType.CONFERENCE_PARTP_JOIN
                || event.getEventType() == JCEvent.EventType.CONFERENCE_PARTP_LEAVE) {
            Log.e("ConferenceActivity","收到CONFERENCE_PARTP_JOIN消息，调用updatePartp");
            adapter.updatePartp();
        } else if (event.getEventType() == JCEvent.EventType.CONFERENCE_PARTP_UPDATE) {
            Log.e("ConferenceActivity","收到CONFERENCE_PARTP_UPDATE消息，调用updatePartp");
            adapter.updatePartp();
        } else if (event.getEventType() == JCEvent.EventType.CONFERENCE_PROP_CHANGE) {
            updateControlButtons();
        } else if (event.getEventType() == JCEvent.EventType.CONFERENCE_MESSAGE_RECEIVED) {
            JCConfMessageEvent messageEvent = (JCConfMessageEvent)event;
            Toast.makeText(this, String.format("%s: type:%s content:%s",
                    messageEvent.fromUserId, messageEvent.type, messageEvent.content), Toast.LENGTH_SHORT).show();
        }
    }


    private void updateControlButtons() {
        mBtnSendAudio.setSelected(JCManager.getInstance().mediaChannel.getUploadLocalAudio());
        mBtnSendVideo.setSelected(JCManager.getInstance().mediaChannel.getUploadLocalVideo());
        mBtnSpeaker.setSelected(JCManager.getInstance().mediaDevice.isSpeakerOn());
        }


    private void switchFullScreen() {
        Utils.showSystemUI(this, !mFullScreen);
        Utils.setActivityFullScreen(this, mFullScreen);
        mControlLayout.setVisibility(mFullScreen ? View.INVISIBLE : View.VISIBLE);

    }


}
