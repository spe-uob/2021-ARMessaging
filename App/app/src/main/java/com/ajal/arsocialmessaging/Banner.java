package com.ajal.arsocialmessaging;

import java.sql.Timestamp;

public class Banner {
    Integer id;
    String postcode;
    Integer message;
    String timestamp;

    public Banner(Integer id, String postcode, Integer message, String timestamp){
        this.id = id;
        this.postcode = postcode;
        this.message = message;
        this.timestamp = timestamp;
    }
}
