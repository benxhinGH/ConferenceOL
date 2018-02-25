package com.usiellau.conferenceol.activity;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.juphoon.cloud.JCMediaChannel;
import com.juphoon.cloud.JCMediaChannelParticipant;
import com.juphoon.cloud.JCMediaDevice;
import com.juphoon.cloud.JCMediaDeviceVideoCanvas;
import com.usiellau.conferenceol.JCWrapper.JCConfUtils;
import com.usiellau.conferenceol.JCWrapper.JCEvent.JCConfMessageEvent;
import com.usiellau.conferenceol.JCWrapper.JCEvent.JCEvent;
import com.usiellau.conferenceol.JCWrapper.JCEvent.JCJoinEvent;
import com.usiellau.conferenceol.JCWrapper.JCManager;
import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.network.ConfSvMethods;
import com.usiellau.conferenceol.network.HttpResult;
import com.usiellau.conferenceol.util.Utils;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by UsielLau on 2018/2/21 0021 23:01.
 */

public class ConferenceActivity extends AppCompatActivity {

    private FrameLayout mPartpLayout;
    private View mControlLayout;
    private boolean mFullScreen;
    private Button mBtnSpeaker;
    private Button mBtnSendAudio;
    private Button mBtnSendVideo;
    private Button mBtnAudioOut;
    private Button mBtnScreenShare;
    private Button mBtnCdn;
    private Button mBtnRecord;
    private TextView mTextStatistics;

    private ScheduledExecutorService mScheduledExecutor = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture mStatisticsScheduled;
    private GestureDetector mGestureDetector;

    private String roomId;

    class Item {
        JCMediaChannelParticipant partp;
        JCConfUtils.SubViewRect rect;
        JCMediaDeviceVideoCanvas canvas;

        ConstraintLayout constraintLayout;
        TextView txtInfo;

        Item() {
            constraintLayout = (ConstraintLayout) ConferenceActivity.this.getLayoutInflater().inflate(R.layout.view_partp, null);
            txtInfo = (TextView) constraintLayout.findViewById(R.id.txtInfo);
        }

        void reset() {
            if (canvas != null) {
                // 关闭视频请求
                if (!isSelf(this)) {
                    JCManager.getInstance().mediaChannel.requestVideo(partp, JCMediaChannel.PICTURESIZE_NONE);
                }
                JCManager.getInstance().mediaDevice.stopVideo(canvas);
                constraintLayout.removeView(canvas.getVideoView());
                canvas = null;
            }
        }

        void delete() {
            reset();
            mPartpLayout.removeView(constraintLayout);
        }
    }

    private List<Item> mItems = new ArrayList<>();
    private JCMediaDeviceVideoCanvas mScreenShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_conference);

        mFullScreen = false;
        mControlLayout = findViewById(R.id.controlLayout);
        mPartpLayout = (FrameLayout) findViewById(R.id.partpLayout);

        mBtnSpeaker = (Button) findViewById(R.id.btnSpeaker);
        mBtnSendAudio = (Button) findViewById(R.id.btnSendAudio);
        mBtnSendVideo = (Button) findViewById(R.id.btnSendVideo);
        mBtnAudioOut = (Button) findViewById(R.id.btnAudioOut);
        mBtnScreenShare = (Button) findViewById(R.id.btnShowScreenShare);
        mBtnCdn = (Button) findViewById(R.id.btnCdn);
        mBtnRecord = (Button) findViewById(R.id.btnRecord);
        mTextStatistics = (TextView) findViewById(R.id.textStatistics);
        mTextStatistics.setMovementMethod(ScrollingMovementMethod.getInstance());

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                mStatisticsScheduled.cancel(true);
                mStatisticsScheduled = null;
                mTextStatistics.setVisibility(View.INVISIBLE);
                return super.onDoubleTap(e);
            }
        });
        mTextStatistics.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mGestureDetector.onTouchEvent(motionEvent);
                return false;
            }
        });
        EventBus.getDefault().register(this);
        roomId=getIntent().getStringExtra("roomId");
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        switchFullScreen();
        layoutPartp();
        updateControlButtons();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Item item : mItems) {
            item.delete();
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        EventBus.getDefault().unregister(this);
        if (mStatisticsScheduled != null) {
            mStatisticsScheduled.cancel(true);
            mStatisticsScheduled = null;
            mScheduledExecutor.shutdown();
        }
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

    public void onAudioOut(View view) {
        JCManager.getInstance().mediaChannel.enableAudioOutput(!JCManager.getInstance().mediaChannel.getAudioOutput());
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

    public void onScreenShare(View view) {
        if (!TextUtils.isEmpty(JCManager.getInstance().mediaChannel.getScreenUserId())) {
            startScreenShare(mScreenShare == null);
            updateControlButtons();
        }
    }

    public void onCdn(View view) {
        if (JCManager.getInstance().mediaChannel.getCdnState() != JCMediaChannel.CDN_STATE_NONE) {
            JCManager.getInstance().mediaChannel.enableCdn();
        }
    }

    public void onRecord(View view) {
        if (JCManager.getInstance().mediaChannel.getRecordState() != JCMediaChannel.RECORD_STATE_NONE) {
            JCManager.getInstance().mediaChannel.enableRecord();
        }
    }

    public void onStatistics(View view) {
        mTextStatistics.setVisibility(View.VISIBLE);
        updateStatistics();

        mStatisticsScheduled = mScheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                mTextStatistics.post(new Runnable() {
                    @Override
                    public void run() {
                        updateStatistics();
                    }
                });
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    public void onSendMessage(View view) {
        View sendView = getLayoutInflater().inflate(R.layout.view_conf_send, null);
        final Spinner spinnerMessageTo = (Spinner) sendView.findViewById(R.id.spinnerMessageTo);
        final EditText editContent = (EditText) sendView.findViewById(R.id.editContent);
        List<String> items = new ArrayList<>();
        items.add("All");
        for (Item item : mItems) {
            if (!isSelf(item)) {
                items.add(item.partp.getUserId());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ConferenceActivity.this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMessageTo.setAdapter(adapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(sendView);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String content = editContent.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    String sendTo = spinnerMessageTo.getSelectedItem().toString();
                    if (TextUtils.equals(sendTo, "All")) {
                        JCManager.getInstance().mediaChannel.sendMessage("Text", content, null);
                    } else {
                        JCManager.getInstance().mediaChannel.sendMessage("Text", content, sendTo);
                    }
                }
            }
        });
        builder.create().show();
    }

    public void onSendCommand(View view) {
        View sendView = getLayoutInflater().inflate(R.layout.view_conf_send, null);
        final Spinner spinnerCommands = (Spinner) sendView.findViewById(R.id.spinnerMessageTo);
        final EditText editContent = (EditText) sendView.findViewById(R.id.editContent);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                ConferenceActivity.this,
                R.array.commands,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCommands.setAdapter(adapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(sendView);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                JCManager.getInstance().mediaChannel.sendCommand(
                        spinnerCommands.getSelectedItem().toString(),
                        editContent.getText().toString().trim());
            }
        });
        builder.create().show();
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
                layoutPartp();
            } else {
                finish();
            }
        } else if (event.getEventType() == JCEvent.EventType.CONFERENCE_LEAVE) {
            finish();
        } else if (event.getEventType() == JCEvent.EventType.CONFERENCE_PARTP_JOIN
                || event.getEventType() == JCEvent.EventType.CONFERENCE_PARTP_LEAVE) {
            layoutPartp();
        } else if (event.getEventType() == JCEvent.EventType.CONFERENCE_PARTP_UPDATE) {
            updatePartp();
        } else if (event.getEventType() == JCEvent.EventType.CONFERENCE_PROP_CHANGE) {
            if (TextUtils.isEmpty(JCManager.getInstance().mediaChannel.getScreenUserId())) {
                if (mScreenShare != null) {
                    startScreenShare(false);
                }
            }
            updateControlButtons();
        } else if (event.getEventType() == JCEvent.EventType.CONFERENCE_MESSAGE_RECEIVED) {
            JCConfMessageEvent messageEvent = (JCConfMessageEvent)event;
            Toast.makeText(this, String.format("%s: type:%s content:%s",
                    messageEvent.fromUserId, messageEvent.type, messageEvent.content), Toast.LENGTH_SHORT).show();
        }
    }

    private void layoutPartp() {
        // 界面还未有长宽
        if (mPartpLayout.getWidth() == 0) {
            mPartpLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    layoutPartp();
                }
            }, 500);
            return;
        }
        // TODO 可优化，这里主要处理每个成员试图对象的建立，并给该对象成员赋值，显示
        List<JCMediaChannelParticipant> partps = JCManager.getInstance().mediaChannel.getParticipants();
        List<JCConfUtils.SubViewRect> subViewRects = JCConfUtils.caclSubViewRect(
                mPartpLayout.getWidth(), mPartpLayout.getHeight(), partps.size());
        for (int i = 0; ; i++) {
            if (i < partps.size()) {
                JCMediaChannelParticipant partp = partps.get(i);
                JCConfUtils.SubViewRect subViewRect = subViewRects.get(i);
                Item item;
                if (mItems.size() <= i) {
                    item = new Item();
                    mItems.add(item);
                    mPartpLayout.addView(item.constraintLayout);
                } else {
                    item = mItems.get(i);
                }
                if (item.partp != partp) {
                    item.reset();
                    item.partp = partp;
                }
                item.rect = subViewRect;

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(subViewRect.width, subViewRect.height);
                params.setMargins(subViewRect.x, subViewRect.y, 0, 0);
                item.constraintLayout.setLayoutParams(params);
                continue;
            } else if (i < mItems.size()) {
                for (int j = mItems.size() - 1; j >= i; j--) {
                    mItems.get(j).delete();
                    mItems.remove(j);
                }
            }
            break;
        }
        updatePartp();
    }

    private void updateControlButtons() {
        mBtnAudioOut.setSelected(JCManager.getInstance().mediaChannel.getAudioOutput());
        mBtnSendAudio.setSelected(JCManager.getInstance().mediaChannel.getUploadLocalAudio());
        mBtnSendVideo.setSelected(JCManager.getInstance().mediaChannel.getUploadLocalVideo());
        mBtnSpeaker.setSelected(JCManager.getInstance().mediaDevice.isSpeakerOn());
        mBtnScreenShare.setEnabled(!TextUtils.isEmpty(JCManager.getInstance().mediaChannel.getScreenRenderId()));
        mBtnScreenShare.setSelected(mScreenShare != null);
        mBtnCdn.setEnabled(JCManager.getInstance().mediaChannel.getCdnState() != JCMediaChannel.CDN_STATE_NONE);
        mBtnCdn.setSelected(JCManager.getInstance().mediaChannel.getCdnState() == JCMediaChannel.CDN_STATE_RUNNING);
        mBtnRecord.setEnabled(JCManager.getInstance().mediaChannel.getRecordState() != JCMediaChannel.RECORD_STATE_NONE);
        mBtnRecord.setSelected(JCManager.getInstance().mediaChannel.getRecordState() == JCMediaChannel.RECORD_STATE_RUNNING);
    }

    private void updatePartp() {
        // TODO 可优化，每个成员视图信息显示
        for (Item item : mItems) {
            item.txtInfo.setText(String.format(Locale.getDefault(),
                    "%s\naudio=%b\nvideo=%b\npictureSize=%d\nvolume=%d\ntype=%d\ntalking(sip)=%b",
                    isSelf(item) ? getString(R.string.me) : item.partp.getDisplayName(),
                    item.partp.isAudio(),
                    item.partp.isVideo(),
                    item.partp.getPictureSize(),
                    item.partp.getVolumeStatus(),
                    item.partp.getType(),
                    item.partp.isTalking()));
            if (isSelf(item)) {
                if (item.partp.isVideo()) {
                    if (item.canvas == null) {
                        item.canvas = JCManager.getInstance().mediaDevice.startCameraVideo(JCMediaDevice.RENDER_FULL_CONTENT);
                        item.constraintLayout.addView(item.canvas.getVideoView(), 0);
                    }
                }
            } else {
                if (item.partp.isVideo()) {
                    if (item.canvas == null) {
                        JCManager.getInstance().mediaChannel.requestVideo(item.partp, JCMediaChannel.PICTURESIZE_LARGE);
                        item.canvas = JCManager.getInstance().mediaDevice.startVideo(item.partp.getRenderId(), JCMediaDevice.RENDER_FULL_CONTENT);
                        item.constraintLayout.addView(item.canvas.getVideoView(), 0);
                    }
                }
            }
            if (!item.partp.isVideo()) {
                if (item.canvas != null) {
                    item.reset();
                }
            }
        }
    }

    private boolean isSelf(Item item) {
        return TextUtils.equals(item.partp.getUserId(), JCManager.getInstance().client.getUserId());
    }

    private void startScreenShare(boolean start) {
        if (start) {
            JCManager.getInstance().mediaChannel.requestScreenVideo(JCManager.getInstance().mediaChannel.getScreenRenderId(),
                    JCMediaChannel.PICTURESIZE_LARGE);
            mScreenShare = JCManager.getInstance().mediaDevice.startVideo(
                    JCManager.getInstance().mediaChannel.getScreenRenderId(), JCMediaDevice.RENDER_FULL_CONTENT);
            mScreenShare.getVideoView().setZOrderOnTop(true);
            mScreenShare.getVideoView().setZOrderMediaOverlay(true);
            mPartpLayout.addView(mScreenShare.getVideoView(), mPartpLayout.getWidth(), mPartpLayout.getHeight());
        } else {
            JCManager.getInstance().mediaChannel.requestScreenVideo(JCManager.getInstance().mediaChannel.getScreenRenderId(),
                    JCMediaChannel.PICTURESIZE_NONE);
            mPartpLayout.removeView(mScreenShare.getVideoView());
            JCManager.getInstance().mediaDevice.stopVideo(mScreenShare);
            mScreenShare = null;
        }
    }

    private void switchFullScreen() {
        Utils.showSystemUI(this, !mFullScreen);
        Utils.setActivityFullScreen(this, mFullScreen);
        mControlLayout.setVisibility(mFullScreen ? View.INVISIBLE : View.VISIBLE);
        for (Item item : mItems) {
            item.txtInfo.setVisibility(mFullScreen ? View.INVISIBLE : View.VISIBLE);
        }
    }

    private void updateStatistics() {
        try {
            JSONObject object = new JSONObject(JCManager.getInstance().mediaChannel.getStatistics());
            StringBuilder builder = new StringBuilder();
            builder.append("channelId:");
            builder.append(JCManager.getInstance().mediaChannel.getChannelId());
            builder.append("\n");
            builder.append("channelNumber:");
            builder.append(JCManager.getInstance().mediaChannel.getChannelNumber());
            builder.append("\n");
            builder.append("channelTitle:");
            builder.append(JCManager.getInstance().mediaChannel.getTitle());
            builder.append("\n");
            builder.append("*********Config*********\n")
                    .append(object.optString("Config"))
                    .append("\n")
                    .append("*********Network*********\n")
                    .append(object.optString("Network"))
                    .append("\n")
                    .append("*********Transport*********\n")
                    .append(object.optString("Transport"))
                    .append("\n")
                    .append("*********Participants*********\n");
            JSONArray array = object.optJSONArray("Participants");
            if (array != null) {
                for (int i=0; i<array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String key = obj.keys().next();
                    String value = obj.getString(key);
                    builder.append("UserId:")
                            .append(key)
                            .append("\n")
                            .append(value)
                            .append("\n");
                }
            }
            mTextStatistics.setText(builder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
