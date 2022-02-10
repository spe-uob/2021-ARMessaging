package com.ajal.arsocialmessaging;

import java.sql.Timestamp;

public class Banner {
    String postcode;
    Integer message;
    String timestamp;

    public Banner(String postcode, Integer message, String timestamp){
        this.postcode = postcode;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Banner(String postcode, Integer message){
        this.postcode = postcode;
        this.message = message;
    }
}
