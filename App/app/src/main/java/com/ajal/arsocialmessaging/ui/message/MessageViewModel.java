package com.ajal.arsocialmessaging.ui.message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MessageViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MessageViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Select a message and enter a postcode to send the message to:");
    }

    public LiveData<String> getText() {
        return mText;
    }
}