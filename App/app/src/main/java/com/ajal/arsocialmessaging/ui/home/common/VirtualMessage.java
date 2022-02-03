package com.ajal.arsocialmessaging.ui.home.common;

// Note: this is only temporary and will be removed when the database is fully implemented

public class VirtualMessage {
    private int messageId;
    private String albedoTexture;
    private String pbrTexture;
    private String filename;
    private String postcode;

    public VirtualMessage(int messageId, String postcode) {
        this.postcode = postcode;
        switch (messageId) {
            case 0:
                this.messageId = messageId;
                this.albedoTexture = "models/happy-birthday.png";
                this.pbrTexture = "models/grey-texture.png";
                this.filename = "models/happy-birthday.obj";
                break;
            case 1:
                this.messageId = messageId;
                this.albedoTexture = "models/merry-christmas.png";
                this.pbrTexture = "models/grey-texture.png";
                this.filename = "models/merry-christmas.obj";
                break;
            case 2:
                this.messageId = messageId;
                this.albedoTexture = "models/congratulations.png";
                this.pbrTexture = "models/grey-texture.png";
                this.filename = "models/congratulations.obj";
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
