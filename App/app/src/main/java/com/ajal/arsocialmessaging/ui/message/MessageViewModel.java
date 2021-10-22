package com.ajal.arsocialmessaging.ui.message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MessageViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MessageViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is message fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}