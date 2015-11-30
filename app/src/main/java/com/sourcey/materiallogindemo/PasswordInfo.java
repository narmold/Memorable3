package com.sourcey.materiallogindemo;

import android.text.method.DateTimeKeyListener;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mmiguel12345 on 11/29/15.
 */
public class PasswordInfo {
    private String website;
    private String username;
    private String password;
    private Date lastUpdate;
    Calendar CurrentDateTime = Calendar.getInstance();

    public PasswordInfo(String website, String username, String password){
        this.website =website;
        this.username = username;
        this.password = password;
        this.lastUpdate = CurrentDateTime.getTime();
    }

    public String getUsername(){
        return this.username;
    }

    public String getWebsite(){
        return this.website;
    }

    public String getPassword(){
        return this.password;
    }

    public String getLastUpdate(){
        return ((Integer)this.lastUpdate.getMonth()).toString() + "/" +((Integer)this.lastUpdate.getDate()).toString();
    }


}
