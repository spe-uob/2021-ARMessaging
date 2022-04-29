package com.ajal.arsocialmessaging.ui.home;

// Note: this is only temporary and will be removed when the database is fully implemented

import com.ajal.arsocialmessaging.R;
import com.ajal.arsocialmessaging.util.database.Message;

public class VirtualMessage {
    private String albedoTexture;
    private String pbrTexture;
    private String objfilename;
    private String audioFile;

    public VirtualMessage(Message message) {
        this.objfilename = "models/"+message.getObjfilename()+"/model.obj";
        this.albedoTexture = "models/"+message.getObjfilename()+"/albedo-texture.png";
        this.pbrTexture = "models/"+message.getObjfilename()+"/pbr-texture.png";
        this.audioFile = "audio/"+message.getObjfilename()+".mp3";
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
