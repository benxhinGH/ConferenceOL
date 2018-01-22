package com.usiellau.conferenceol.network;

import com.usiellau.conferenceol.network.entity.User;


import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * Created by UsielLau on 2018/1/20 0020 23:57.
 */

public interface ConfSvApi {
    @GET("login")
    Observable<HttpResult<User>> login(@Query("phonenumber") String phonenumber, @Query("password") String password);

    @POST("register")
    Observable<HttpResult> register(@Query("phonenumber") String phonenumber, @Query("authcode") String authcode,@Query("password") String password);

    @POST("authcode")
    Observable<HttpResult> sendAuthcode(@Query("phonenumber") String phonenumber);
}
