package com.usiellau.conferenceol.network;

import android.util.Log;

import com.usiellau.conferenceol.network.entity.ConfFile;
import com.usiellau.conferenceol.network.entity.ConfForecast;
import com.usiellau.conferenceol.network.entity.ConfIng;
import com.usiellau.conferenceol.network.entity.ConfOver;
import com.usiellau.conferenceol.network.entity.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
                                 String title,int type,String password,
                                 String channelId,int capacity,String creator,boolean hasfile){
        confSvApi.createConference(title, type,password,channelId, capacity, creator,hasfile)
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

    public void uploadFile(Observer<HttpResult> observer,String description, File file){
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        RequestBody description1 =
                RequestBody.create(MediaType.parse("multipart/form-data"),description);
        confSvApi.uploadFile(description1,body)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void createForecast(Observer<HttpResult> observer,String title,String password,String channelId,
                               int capacity,String creator,boolean hasFile,long startTime){
        confSvApi.createForecast(title,password,channelId,capacity,creator,hasFile,startTime)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void queryForecast(Observer<HttpResult<List<ConfForecast>>> observer){
        confSvApi.queryForecast()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void downloadConfFile(Observer<Boolean> observer, String serverPath, final String localPath){
        confSvApi.downloadConfFile(serverPath)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ResponseBody, Boolean>() {
                    @Override
                    public Boolean apply(ResponseBody responseBody) throws Exception {
                        return writeResponseBodyToDisk(responseBody,localPath);
                    }
                })
                .subscribe(observer);
    }

    public void queryConfFile(Observer<HttpResult<ConfFile>> observer,String channelId){
        confSvApi.queryConfFile(channelId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    private boolean writeResponseBodyToDisk(ResponseBody body,String path) {
        try {
            // todo change the file location/name according to your needs
            File file = new File(path);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("ConfSvMethods", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }





}
