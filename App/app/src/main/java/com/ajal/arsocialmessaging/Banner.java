package com.ajal.arsocialmessaging;

public class Banner {
    private String postcode;
    private Integer message;
    private String timestamp;

    public Banner(String postcode, Integer message, String timestamp){
        this.postcode = postcode;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getPostcode() {
        return postcode;
    }

    public Integer getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
