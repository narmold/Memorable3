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
    private DateHelper expirationDate;
    Calendar CurrentDateTime = Calendar.getInstance();

    public PasswordInfo(String website, String username, String password, DateHelper date){
        this.website =website;
        this.username = username;
        this.password = password;
        this.expirationDate = date;
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

    public DateHelper getExpiration(){return this.expirationDate;}


}
