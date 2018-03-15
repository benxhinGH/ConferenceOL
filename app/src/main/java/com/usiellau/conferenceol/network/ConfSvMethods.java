package com.usiellau.conferenceol.network;

import android.util.Log;

import com.usiellau.conferenceol.network.entity.ConfIng;
import com.usiellau.conferenceol.network.entity.ConfOver;
import com.usiellau.conferenceol.network.entity.User;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;




/**
 * Created by UsielLau on 2018/1/20 0020 23:42.
 */

public class ConfSvMethods {

    public static final String BASE_URL="http://192.168.1.101:8080/ConfOL/";

    private static final int DEFAULT_TIMEOUT=5;

    private Retrofit retrofit;
    private ConfSvApi confSvApi;


    private ConfSvMethods(){
        //手动创建一个okhttpclient并设置超时时间
        OkHttpClient.Builder builder=new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        retrofit=new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        confSvApi=retrofit.create(ConfSvApi.class);
    }

    //在访问ConfSvMethods时创建单例
    private static class SingletonHolder{
        private static final ConfSvMethods INSTANCE = new ConfSvMethods();
    }

    //获取单例
    public static ConfSvMethods getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public void login(Observer<HttpResult<User>> observer, String phonenumber, String password){
        confSvApi.login(phonenumber, password)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
        Log.d("ConfSvMethods","phonenumber"+phonenumber+"password"+password);
    }

    public void register(Observer<HttpResult> observer,String phonenumber,String authcode,String password){
        confSvApi.register(phonenumber,authcode, password)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void sendAuthcode(Observer<HttpResult> observer,String phonenumber){
        confSvApi.sendAuthcode(phonenumber)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void queryConfIng(Observer<HttpResult<List<ConfIng>>> observer,String selectType,String channelId){
        confSvApi.queryConfIng(selectType,channelId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void queryAllConfOver(Observer<HttpResult<List<ConfOver>>> observer){
        confSvApi.queryAllConfOver("all")
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void createConference(Observer<HttpResult<ConfIng>> observer,
                                 String title,String password,
                                 String channelId,int capacity,String creator){
        confSvApi.createConference(title, password,channelId, capacity, creator)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void enterRoom(Observer<HttpResult> observer,String roomId,String phonenumber){
        confSvApi.enterRoom(roomId,phonenumber)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void leaveRoom(Observer<HttpResult> observer,String roomId,String phonenumber){
        confSvApi.leaveRoom(roomId, phonenumber)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }



}
