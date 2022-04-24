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
            case 1 : msg = "Happy birthday"; break;
            case 2 : msg = "Merry Christmas"; break;
            case 3 : msg = "Congratulations"; break;
            case 4 : msg = "Good luck"; break;
            case 5 : msg = "Get well soon"; break;
            case 6 : msg = "Thank you"; break;
            case 7 : msg = "Happy New Year"; break;
            case 8 : msg = "Happy Mother's Day"; break;
            case 9 : msg = "Ramadan Kareem"; break;
            case 10: msg = "Happy Diwali"; break;
        }
        return msg;
    }
}
