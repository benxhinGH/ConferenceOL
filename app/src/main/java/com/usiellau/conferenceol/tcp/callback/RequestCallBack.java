package com.usiellau.conferenceol.tcp.callback;

import com.usiellau.conferenceol.tcp.protocol.BasicProtocol;

/**
 * Created by UsielLau on 2018/4/8 0008 15:05.
 */

public interface RequestCallBack {

    void onSuccess(BasicProtocol msg);

    void onFailed(int errorCode, String msg);
}