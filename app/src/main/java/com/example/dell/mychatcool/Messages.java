package com.example.dell.mychatcool;

/**
 * Created by DELL on 4/10/2018.
 */

public class Messages  {

    private String message,type;
    private long time;
    private boolean seen;
    private String from;

    public Messages(){

    }

    public Messages(String message, String type, long time, boolean seen,String from) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
        this.from=from;
    }
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }


    public String getMessage() {
        return message;
    }

    public void setMessages(String messages) {
        this.message = messages;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
