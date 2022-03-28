package com.ajal.arsocialmessaging.ui.home.common;

// Note: this is only temporary and will be removed when the database is fully implemented

import com.ajal.arsocialmessaging.R;
import com.ajal.arsocialmessaging.util.database.Message;

public class VirtualMessage {
    private String albedoTexture;
    private String pbrTexture;
    private String objfilename;
    private String audioFile;

    public VirtualMessage(Message message) {
        setFiles(message);
    }

    public void setFiles(Message message) {
        this.albedoTexture = "models/"+message.getObjfilename()+".png";
        this.objfilename = "models/"+message.getObjfilename()+".obj";
        this.audioFile = "audio/"+message.getObjfilename()+".mp3";

        // TODO: remove rest of the switch statement
        switch (message.getId()) {
            case 1:
                this.pbrTexture = "models/white-texture.png";
                break;
            case 2:
                this.pbrTexture = "models/grey-texture.png";
                break;
            case 3:
                this.pbrTexture = "models/white-texture.png";
                break;
            case 4:
                this.pbrTexture = "models/grey-texture.png";
                break;
            case 5:
                this.pbrTexture = "models/grey-texture.png";
                break;
            case 6:
                this.pbrTexture = "models/white-texture.png";
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

    public String getAudioFile() {
        return this.audioFile;
    }
}
