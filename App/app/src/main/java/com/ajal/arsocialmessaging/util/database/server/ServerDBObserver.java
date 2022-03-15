package com.ajal.arsocialmessaging.util.database.server;

import com.ajal.arsocialmessaging.util.database.Banner;
import com.ajal.arsocialmessaging.util.database.Message;

import java.util.List;

public interface ServerDBObserver {
    public void onMessageSuccess(List<Message> result);
    public void onMessageFailure();
    public void onBannerSuccess(List<Banner> result);
    public void onBannerFailure();
}
