package com.ajal.arsocialmessaging;

import java.util.List;

public interface ApiCallback{
    public void onMessageSuccess(List<Message> result);
    public void onBannerSuccess(List<Banner> result);
}
