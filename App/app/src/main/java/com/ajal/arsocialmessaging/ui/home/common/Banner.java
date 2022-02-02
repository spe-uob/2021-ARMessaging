package com.ajal.arsocialmessaging.ui.home.common;

// Note: this is only temporary and will be removed when the database is fully implemented

public class Banner {
    private int messageId;
    private String albedoTexture;
    private String pbrTexture;
    private String filename;
    private String postcode;

    public Banner(int messageId, String postcode) {
        this.postcode = postcode;
        switch (messageId) {
            case 0:
                this.messageId = messageId;
                this.albedoTexture = "models/happy-birthday.png";
                this.pbrTexture = "models/white_texture.png";
                this.filename = "models/happy-birthday.obj";
                break;
        }
    }

    public int getMessageId() {
        return messageId;
    }

    public String getAlbedoTexture() {
        return this.albedoTexture;
    }

    public String getPbrTexture() {
        return this.pbrTexture;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getPostCode() {
        return this.postcode;
    }
}
