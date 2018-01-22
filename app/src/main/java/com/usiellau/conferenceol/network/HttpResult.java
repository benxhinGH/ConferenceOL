package com.usiellau.conferenceol.network;

/**
 * Created by UsielLau on 2018/1/21 0021 5:43.
 */

public class HttpResult<T> {

    private int code;
    private String msg;
    private T result;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


    public T getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", result=" + result +
                '}';
    }
}
