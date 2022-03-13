package com.ajal.arsocialmessaging.ui.home.common;

// Note: this is only temporary and will be removed when the database is fully implemented

import com.ajal.arsocialmessaging.R;
import com.ajal.arsocialmessaging.util.database.Message;

public class VirtualMessage {
    private String albedoTexture;
    private String pbrTexture;
    private String objfilename;
    private int audioFile;

    public VirtualMessage(Message message) {
        setFiles(message.getId());
    }

    public void setFiles(int messageId) {
        switch (messageId) {
            case 1:
                this.albedoTexture = "models/happy-birthday.png";
                this.pbrTexture = "models/white-texture.png";
                this.objfilename = "models/happy-birthday.obj";
                this.audioFile = R.raw.happy_birthday;
                break;
            case 2:
                this.albedoTexture = "models/merry-christmas.png";
                this.pbrTexture = "models/grey-texture.png";
                this.objfilename = "models/merry-christmas.obj";
                this.audioFile = R.raw.merry_christmas;
                break;
            case 3:
                this.albedoTexture = "models/congratulations.png";
                this.pbrTexture = "models/white-texture.png";
                this.objfilename = "models/congratulations.obj";
                this.audioFile = R.raw.congratulations;
                break;
            case 4: // temporary files
                this.albedoTexture = "models/congratulations.png";
                this.pbrTexture = "models/grey-texture.png";
                this.objfilename = "models/congratulations.obj";
                this.audioFile = R.raw.good_luck;
                break;
            case 5: // temporary files
                this.albedoTexture = "models/congratulations.png";
                this.pbrTexture = "models/grey-texture.png";
                this.objfilename = "models/congratulations.obj";
                this.audioFile = R.raw.feel_better;
                break;
            case 6:
                this.albedoTexture = "models/thank-you.png";
                this.pbrTexture = "models/grey-texture.png";
                this.objfilename = "models/thank-you.obj";
                this.audioFile = R.raw.thank_you;
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

    public int getAudioFile() {
        return this.audioFile;
    }
}
