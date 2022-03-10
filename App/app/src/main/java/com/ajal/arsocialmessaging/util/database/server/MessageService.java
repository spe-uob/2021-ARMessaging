package com.ajal.arsocialmessaging.util.database.server;

import com.ajal.arsocialmessaging.util.database.Banner;
import com.ajal.arsocialmessaging.util.database.Message;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MessageService {
    @GET("/getAllMessages")
    Call<List<Message>> getAllMessages();

    @GET("/getAllBanners")
    Call<List<Banner>> getAllBanners();

    @FormUrlEncoded
    @POST("/addBanner")
    Call<String> addBanner(@Field("bannerData") String bannerData);

    @FormUrlEncoded
    @POST("/addToken")
    Call<String> addToken(@Field("tokenData") String tokenData);
}
