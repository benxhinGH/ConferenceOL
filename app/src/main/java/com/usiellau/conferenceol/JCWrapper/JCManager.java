package com.usiellau.conferenceol.JCWrapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.juphoon.cloud.JCCall;
import com.juphoon.cloud.JCCallCallback;
import com.juphoon.cloud.JCCallItem;
import com.juphoon.cloud.JCClient;
import com.juphoon.cloud.JCClientCallback;
import com.juphoon.cloud.JCGroup;
import com.juphoon.cloud.JCGroupCallback;
import com.juphoon.cloud.JCGroupItem;
import com.juphoon.cloud.JCGroupMember;
import com.juphoon.cloud.JCMediaChannel;
import com.juphoon.cloud.JCMediaChannelCallback;
import com.juphoon.cloud.JCMediaChannelParticipant;
import com.juphoon.cloud.JCMediaChannelQueryInfo;
import com.juphoon.cloud.JCMediaDevice;
import com.juphoon.cloud.JCMediaDeviceCallback;
import com.juphoon.cloud.JCMessageChannel;
import com.juphoon.cloud.JCMessageChannelCallback;
import com.juphoon.cloud.JCMessageChannelItem;
import com.juphoon.cloud.JCStorage;
import com.juphoon.cloud.JCStorageCallback;
import com.juphoon.cloud.JCStorageItem;
import com.juphoon.cloud.JCPush;

import com.usiellau.conferenceol.JCWrapper.JCData.JCGroupData;
import com.usiellau.conferenceol.JCWrapper.JCData.JCMessageData;
import com.usiellau.conferenceol.JCWrapper.JCEvent.JCConfMessageEvent;
import com.usiellau.conferenceol.JCWrapper.JCEvent.JCConfQueryEvent;
import com.usiellau.conferenceol.JCWrapper.JCEvent.JCEvent;
import com.usiellau.conferenceol.JCWrapper.JCEvent.JCJoinEvent;
import com.usiellau.conferenceol.JCWrapper.JCEvent.JCLoginEvent;
import com.usiellau.conferenceol.JCWrapper.JCEvent.JCMessageEvent;
import com.usiellau.conferenceol.JCWrapper.JCEvent.JCStorageEvent;
import com.usiellau.conferenceol.R;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 本类主要是对Juphoon Cloud SDK 的简单封装
 */
public class JCManager implements JCClientCallback, JCCallCallback, JCMediaChannelCallback, JCMediaDeviceCallback,
        JCStorageCallback,  JCMessageChannelCallback {

    private static String AppKey="0dc657108b5a6e5eefe44097";


    public static JCManager getInstance() {
        return JCManagerHolder.INSTANCE;
    }

    public Boolean pstnMode = false; // 会议的Pstn落地模式

    private Context mContext;
    public JCClient client;
    public JCCall call;
    public JCMediaDevice mediaDevice;
    public JCMediaChannel mediaChannel;
  //  public JCMessageChannel messageChannel;
   // public JCStorage storage;
 //   public JCGroup group;


    public boolean initialize(Context context) {
        mContext = context;
        client = JCClient.create(context, AppKey, this, null);
        mediaDevice = JCMediaDevice.create(client, this);
        mediaChannel = JCMediaChannel.create(client, mediaDevice, this);
        Log.d("11111111111","222222222222222222222"+",媒体通道标识："+mediaChannel.getChannelId());
//        call = JCCall.create(client, mediaDevice, this);
//        messageChannel = JCMessageChannel.create(client, this);
//        storage = JCStorage.create(client, this);
//        group = JCGroup.create(client, this);


        generateDefaultConfig(context);

        client.displayName = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.cloud_setting_key_display_name), "");
        client.setConfig(JCClient.CONFIG_KEY_SERVER_ADDRESS,
                PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.cloud_setting_key_server), ""));
//        call.maxCallNum = Integer.valueOf(
//                PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.cloud_setting_key_call_max_num), ""));
//        call.setConference(
//                PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.cloud_setting_key_call_audio_conference), false));
        mediaChannel.setConfig(JCMediaChannel.CONFIG_CAPACITY,
                PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.cloud_setting_key_conference_max_num), ""));

        // 本程序设置为固定方向
        mediaDevice.autoRotate = false;

        return true;
    }

    public void uninitialize() {
        JCPush.destroy();
        JCStorage.destroy();
        JCMessageChannel.destroy();
        JCCall.destroy();
        JCMediaChannel.destroy();
        JCMediaDevice.destroy();
        JCClient.destroy();
      //  storage = null;
      //  messageChannel = null;
       // call = null;
        mediaChannel = null;
        mediaDevice = null;
        client = null;
    }

    @Override
    public void onLogin(boolean result, @JCClient.ClientReason int reason) {
        EventBus.getDefault().post(new JCLoginEvent(result, reason));

    }

    @Override
    public void onLogout(@JCClient.ClientReason int reason) {
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.LOGOUT));
        saveLastLogined("", "");
        JCMessageData.clear();
        JCGroupData.clear();


    }

    @Override
    public void onClientStateChange(@JCClient.ClientState int state, @JCClient.ClientState int oldState) {
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CLIENT_STATE_CHANGE));
    }

    @Override
    public void onCallItemAdd(JCCallItem item) {
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CALL_ADD));
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CALL_UI));
    }

    @Override
    public void onCallItemRemove(JCCallItem item, @JCCall.CallReason int reason) {
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CALL_REMOVE));
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CALL_UI));
    }

    @Override
    public void onCallItemUpdate(JCCallItem item) {
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CALL_UPDATE));
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CALL_UI));
    }

    @Override
    public void onMessageReceive(String s, String s1, JCCallItem jcCallItem) {

    }

    @Override
    public void onMediaChannelStateChange(@JCMediaChannel.MediaChannelState int state, @JCMediaChannel.MediaChannelState int oldState) {

    }

    @Override
    public void onMediaChannelPropertyChange() {
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CONFERENCE_PROP_CHANGE));
    }

    @Override
    public void onJoin(boolean result, @JCMediaChannel.MediaChannelReason int reason, String channelId) {
        EventBus.getDefault().post(new JCJoinEvent(result, reason, channelId));
        if (result && pstnMode) {
            if (mediaChannel.inviteSipUser(channelId) == -1) {
                mediaChannel.leave();
            }
        }
    }

    @Override
    public void onLeave(@JCMediaChannel.MediaChannelReason int reason, String channelId) {
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CONFERENCE_LEAVE));
    }

    @Override
    public void onQuery(int operationId, boolean result, @JCMediaChannel.MediaChannelReason int reason, JCMediaChannelQueryInfo queryInfo) {
        EventBus.getDefault().post(new JCConfQueryEvent(operationId, result, reason, queryInfo));
    }

    @Override
    public void onParticipantJoin(JCMediaChannelParticipant participant) {
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CONFERENCE_PARTP_JOIN));
        if (pstnMode) {
            mediaChannel.enableAudioOutput(true);
        }
    }

    @Override
    public void onParticipantLeft(JCMediaChannelParticipant participant) {
        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CONFERENCE_PARTP_LEAVE));
        if (pstnMode) {
            mediaChannel.leave();
        }
    }

    @Override
    public void onParticipantUpdate(JCMediaChannelParticipant participant) {
        Log.e("JCManager","收到onParticipantUpdate回调,participant状态："+
        "\ngetDisplayName:"+participant.getDisplayName()+
        "\ngetPictureSize:"+participant.getPictureSize()+
        "\ngetRenderId:"+participant.getRenderId()+
        "\ngetType:"+participant.getType()+
        "\ngetUserId:"+participant.getUserId()+
        "\ngetVolumnStatus:"+participant.getVolumeStatus()+
        "\nisAudio:"+participant.isAudio()+
        "\nisTalking:"+participant.isTalking()+
        "\nisVideo:"+participant.isVideo());

        EventBus.getDefault().post(new JCEvent(JCEvent.EventType.CONFERENCE_PARTP_UPDATE));
    }

    @Override
    public void onMessageReceive(String type, String content, String fromUserId) {
        EventBus.getDefault().post(new JCConfMessageEvent(type, content, fromUserId));
    }

    @Override
    public void onInviteSipUserResult(int operationId, boolean result, int reason) {
        if (pstnMode && !result) {
            mediaChannel.leave();
        }
    }

    @Override
    public void onCameraUpdate() {

    }

    @Override
    public void onAudioOutputTypeChange(boolean speaker) {

    }

    // 用于自动登录上次登录着的账号
    public boolean loginIfLastLogined() {
        String userId = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(mContext.getString(R.string.cloud_setting_last_login_user_id), null);
        if (TextUtils.isEmpty(userId)) {
            return false;
        }
        String password = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(mContext.getString(R.string.cloud_setting_last_login_password), null);
        return client.login(userId, password);
    }

    // 保存最后一次登录账号信息
    public void saveLastLogined(String userId, String password) {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                .putString(mContext.getString(R.string.cloud_setting_last_login_user_id), userId)
                .putString(mContext.getString(R.string.cloud_setting_last_login_password), password)
                .apply();
    }

    // 生成默认配置
    private void generateDefaultConfig(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        String value = sp.getString(context.getString(R.string.cloud_setting_key_display_name), "");
        if (TextUtils.isEmpty(value)) {
            editor.putString(context.getString(R.string.cloud_setting_key_display_name), "");
        }
        value = sp.getString(context.getString(R.string.cloud_setting_key_server), "");
        if (TextUtils.isEmpty(value)) {
            editor.putString(context.getString(R.string.cloud_setting_key_server), client.getConfig(JCClient.CONFIG_KEY_SERVER_ADDRESS));
        }
        value = sp.getString(context.getString(R.string.cloud_setting_key_call_max_num), "");
        if (TextUtils.isEmpty(value)) {
            editor.putString(context.getString(R.string.cloud_setting_key_call_max_num), String.valueOf(2));
        }
        value = sp.getString(context.getString(R.string.cloud_setting_key_conference_max_num), "");
        if (TextUtils.isEmpty(value)) {
            editor.putString(context.getString(R.string.cloud_setting_key_conference_max_num), mediaChannel.getConfig(JCMediaChannel.CONFIG_CAPACITY));
        }
        editor.apply();
    }

    @Override
    public void onMessageSendUpdate(JCMessageChannelItem jcMessageChannelItem) {
        EventBus.getDefault().post(new JCMessageEvent(true, jcMessageChannelItem));
    }

    @Override
    public void onMessageRecv(JCMessageChannelItem jcMessageChannelItem) {
        EventBus.getDefault().post(new JCMessageEvent(false, jcMessageChannelItem));
    }

    @Override
    public void onFileUpdate(JCStorageItem jcStorageItem) {
        EventBus.getDefault().post(new JCStorageEvent(jcStorageItem));
    }



    private static final class JCManagerHolder {
        private static final JCManager INSTANCE = new JCManager();
    }
}
