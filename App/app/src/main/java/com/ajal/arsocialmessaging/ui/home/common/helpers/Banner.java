package com.ajal.arsocialmessaging.ui.home.common.helpers;

public class Banner {
    private int messageId;
    private String filename;


    public Banner(int messageId) {
        switch (messageId) {
            case 0: this.messageId = messageId; this.filename = "models/happy-birthday.obj";
        }
    }

    public int getMessageId() {
        return messageId;
    }

    public String getFilename() {
        return filename;
    }
}
