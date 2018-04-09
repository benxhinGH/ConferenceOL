package com.usiellau.conferenceol.tcp;

import com.usiellau.conferenceol.tcp.callback.RequestCallBack;
import com.usiellau.conferenceol.tcp.protocol.DataProtocol;

/**
 * Created by UsielLau on 2018/4/8 0008 15:04.
 */

public class ConnectionClient {

    private boolean isClosed;

    private ClientRequestTask mClientRequestTask;

    public ConnectionClient(RequestCallBack requestCallBack) {
        mClientRequestTask = new ClientRequestTask(requestCallBack);
        new Thread(mClientRequestTask).start();
    }

    public void addNewRequest(DataProtocol data) {
        if (mClientRequestTask != null && !isClosed)
            mClientRequestTask.addRequest(data);
    }

    public void closeConnect() {
        isClosed = true;
        mClientRequestTask.stop();
    }
}