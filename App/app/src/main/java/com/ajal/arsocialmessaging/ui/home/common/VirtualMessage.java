package com.ajal.arsocialmessaging.ui.home.common;

// Note: this is only temporary and will be removed when the database is fully implemented

import com.ajal.arsocialmessaging.util.database.Message;

public class VirtualMessage {
    private String albedoTexture;
    private String pbrTexture;
    private String objfilename;

    public VirtualMessage(Message message) {
        setFiles(message.getId());
    }

    public void setFiles(int messageId) {
        switch (messageId) {
            case 1:
                this.albedoTexture = "models/happy-birthday.png";
                this.pbrTexture = "models/white-texture.png";
                this.objfilename = "models/happy-birthday.obj";
                break;
            case 2:
                this.albedoTexture = "models/merry-christmas.png";
                this.pbrTexture = "models/grey-texture.png";
                this.objfilename = "models/merry-christmas.obj";
                break;
            case 3:
                this.albedoTexture = "models/congratulations.png";
                this.pbrTexture = "models/white-texture.png";
                this.objfilename = "models/congratulations.obj";
                break;
            case 4: // temporary files
                this.albedoTexture = "models/congratulations.png";
                this.pbrTexture = "models/grey-texture.png";
                this.objfilename = "models/congratulations.obj";
                break;
            case 5: // temporary files
                this.albedoTexture = "models/congratulations.png";
                this.pbrTexture = "models/grey-texture.png";
                this.objfilename = "models/congratulations.obj";
                break;
            case 6:
                this.albedoTexture = "models/thank-you.png";
                this.pbrTexture = "models/grey-texture.png";
                this.objfilename = "models/thank-you.obj";
                break;
        }
    }

    public String getAlbedoTexture() {
        return this.albedoTexture;
    }

    public String getPbrTexture() {
        return this.pbrTexture;
    }

    public String getObjfilename() {
        return this.objfilename;
    }
}
