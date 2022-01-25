package com.ajal.arsocialmessaging;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MessageService {
    @GET("/getAllMessages")
    Call<List<Message>> getAllMessages();

    @GET("/users/{username}")
    public Call<User> getUser(@Path("username") String username);
}
