package com.ajal.arsocialmessaging.util.database;

public class Message {
    private Integer id;
    private String message;
    private String objfilename;

    public Message(Integer id, String message, String objfilename){
        this.id = id;
        this.message = message;
        this.objfilename = objfilename;
    }

    public Integer getId() {
        return this.id;
    }

    public String getMessage() {
        return this.message;
    }

    public String getObjfilename() {
        return this.objfilename;
    }
}
