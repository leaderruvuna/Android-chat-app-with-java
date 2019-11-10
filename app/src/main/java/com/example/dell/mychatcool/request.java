package com.example.dell.mychatcool;

/**
 * Created by DELL on 4/16/2018.
 */

public class request {

    private String username,userstatus,userthumbimage;
    public request(){

    }

    public request(String username, String userstatus, String userthumbimage) {
        this.username = username;
        this.userstatus = userstatus;
        this.userthumbimage = userthumbimage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserstatus() {
        return userstatus;
    }

    public void setUserstatus(String userstatus) {
        this.userstatus = userstatus;
    }

    public String getUserthumbimage() {
        return userthumbimage;
    }

    public void setUserthumbimage(String userthumbimage) {
        this.userthumbimage = userthumbimage;
    }
}
