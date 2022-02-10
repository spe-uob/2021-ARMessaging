package com.ajal.arsocialmessaging;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MessageService {
    @GET("/getAllMessages")
    Call<List<Message>> getAllMessages();

    @GET("/getAllBanners")
    Call<List<Banner>> getAllBanners();

    @POST("/addBanner")
    Call<String> addBanner(@Body JSONObject bannerData);

}
