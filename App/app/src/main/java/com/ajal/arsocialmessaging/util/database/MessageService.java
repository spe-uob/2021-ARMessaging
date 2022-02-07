package com.ajal.arsocialmessaging.util.database;

import com.ajal.arsocialmessaging.util.database.Banner;
import com.ajal.arsocialmessaging.util.database.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MessageService {
    @GET("/getAllMessages")
    Call<List<Message>> getAllMessages();

    @GET("/getAllBanners")
    Call<List<Banner>> getAllBanners();

    @POST("/addBanner")
    Call<Void> addBanner(@Body String postcode, Integer messageId);

}
