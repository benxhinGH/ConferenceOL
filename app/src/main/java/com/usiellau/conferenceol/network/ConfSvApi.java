package com.usiellau.conferenceol.network;

import com.usiellau.conferenceol.network.entity.ConfIng;
import com.usiellau.conferenceol.network.entity.ConfOver;
import com.usiellau.conferenceol.network.entity.User;


import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
                                                     @Field("password")String password,
                                                     @Field("channel_id")String channelId,
                                                     @Field("capacity")int capacity,
                                                     @Field("creator")String creator);
    @FormUrlEncoded
    @POST("room")
    Observable<HttpResult> enterRoom(@Field("channel_id")String channelId,
                                     @Field("phonenumber")String phonenumber);
    @GET("room")
    Observable<HttpResult> leaveRoom(@Query("channel_id") String channelId,
                                     @Query("phonenumber") String phonenumber);


}
