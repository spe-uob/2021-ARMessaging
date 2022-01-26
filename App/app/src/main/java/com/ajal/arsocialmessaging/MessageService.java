package com.ajal.arsocialmessaging;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MessageService {
    @GET("/getAllMessages")
    Call<String> getAllMessages();

    @GET("/sayHello")
    Call<User> sayHello();

    @GET("/users/{username}")
    Call<User> getUser(@Path("username") String username);
}
