package com.usiellau.conferenceol.tcp.callback;

import com.usiellau.conferenceol.tcp.protocol.DataProtocol;

/**
 * Created by UsielLau on 2018/4/8 0008 15:02.
 */

public interface ResponseCallback {

    void targetIsOffline(DataProtocol reciveMsg);

    void targetIsOnline(String clientIp);
}
