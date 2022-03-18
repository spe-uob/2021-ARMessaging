package com.ajal.arsocialmessaging.util.database;

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

    public String getMessageAsString() {
        String msg = "";
        switch (this.message) {
            case 1: msg = "Happy birthday"; break;
            case 2: msg = "Merry Christmas"; break;
            case 3: msg = "Congratulations"; break;
            case 4: msg = "Good luck"; break;
            case 5: msg = "Hope you feel better soon!"; break;
            case 6: msg = "Thank you"; break;
        }
        return msg;
    }
}
