package com.usiellau.conferenceol.network;

import com.usiellau.conferenceol.network.entity.ConfFile;
import com.usiellau.conferenceol.network.entity.ConfForecast;
import com.usiellau.conferenceol.network.entity.ConfIng;
import com.usiellau.conferenceol.network.entity.ConfOver;
import com.usiellau.conferenceol.network.entity.User;


import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


/**
 * Created by UsielLau on 2018/1/20 0020 23:57.
 */

public interface ConfSvApi {
    @GET("login")
    Observable<HttpResult<User>> login(@Query("phonenumber") String phonenumber,
                                       @Query("password") String password);
    @FormUrlEncoded
    @POST("register")
    Observable<HttpResult> register(@Field("phonenumber") String phonenumber,
                                    @Field("authcode") String authcode,
                                    @Field("password") String password);

    @FormUrlEncoded
    @POST("authcode")
    Observable<HttpResult> sendAuthcode(@Field("phonenumber") String phonenumber);

    @GET("conf_ing")
    Observable<HttpResult<List<ConfIng>>> queryConfIng(@Query("selectType")String selectType,
                                                          @Query("channelId")String channelId);

    @GET("conf_over")
    Observable<HttpResult<List<ConfOver>>> queryAllConfOver(@Query("selectType")String selectType);

    @FormUrlEncoded
    @POST("conf_ing")
    Observable<HttpResult<ConfIng>> createConference(@Field("title")String title,
                                                     @Field("type")int type,
                                                     @Field("password")String password,
                                                     @Field("channel_id")String channelId,
                                                     @Field("capacity")int capacity,
                                                     @Field("creator")String creator,
                                                     @Field("hasfile")boolean hasfile);
    @FormUrlEncoded
    @POST("conf_forecast")
    Observable<HttpResult> createForecast(@Field("title")String title,
                                          @Field("password")String password,
                                          @Field("channel_id")String channelId,
                                          @Field("capacity")int capacity,
                                          @Field("creator")String creator,
                                          @Field("hasfile")boolean hasfile,
                                          @Field("start_time")long startTime);
    @Multipart
    @POST("upload")
    Observable<HttpResult<String>> uploadFile(@Part("description") RequestBody description,
                                                @Part MultipartBody.Part file);
    @FormUrlEncoded
    @POST("room")
    Observable<HttpResult> enterRoom(@Field("channel_id")String channelId,
                                     @Field("phonenumber")String phonenumber);
    @GET("room")
    Observable<HttpResult> leaveRoom(@Query("channel_id") String channelId,
                                     @Query("phonenumber") String phonenumber);

    @GET("conf_forecast")
    Observable<HttpResult<List<ConfForecast>>> queryForecast();

    @GET("conf_file")
    Observable<HttpResult<ConfFile>> queryConfFile(@Query("channel_id")String channelId);

    @FormUrlEncoded
    @POST("download")
    Observable<ResponseBody> downloadFile(@Field("path") String path);

    @POST("tcp")
    Observable<HttpResult> requestTcpLongCon();

    @FormUrlEncoded
    @POST("user_update")
    Observable<HttpResult> updateUserInfo(@Field("update_info")String updateInfoJson);


    @GET("user")
    Observable<HttpResult<User>> queryUserInfo(@Query("username")String username);


}
