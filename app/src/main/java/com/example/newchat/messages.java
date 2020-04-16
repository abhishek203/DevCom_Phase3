package com.example.newchat;

public class messages {
    private String message, seeen, type, time;
    public messages(String message, String seeen, String type, String time) {
        this.message = message;
        this.seeen = seeen;
        this.type = type;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeeen() {
        return seeen;
    }

    public void setSeeen(String seeen) {
        this.seeen = seeen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }



}
