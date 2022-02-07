package com.ajal.arsocialmessaging.util.database;

import java.util.List;

public interface DBObserver {
    public void onMessageSuccess(List<Message> result);
    public void onMessageFailure();
    public void onBannerSuccess(List<Banner> result);
    public void onBannerFailure();
}
