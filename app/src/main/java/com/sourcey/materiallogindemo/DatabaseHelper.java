package com.sourcey.materiallogindemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A class which assists all activities in retrieving and submitting SQL queries to the database
 */
public class DatabaseHelper {
    //All variables used throughout the helper
    private static final String DATABASE_NAME = "Memorable.db";
    private static final int DATABASE_VERSION = 1;
    private static final String ACCOUNT_TABLE = "Accounts";
    private static final String PASSWORD_TABLE = "Passwords";
    private Context context;
    private SQLiteDatabase db;
    private SQLiteStatement currentStmt;
    private static final String NEW_USER_QUERY = "insert into " + ACCOUNT_TABLE + "(account_name, password) values (?, ?)";
    private static final String NEW_PASSWORD_QUERY = "insert into " + PASSWORD_TABLE + "(account_name, website, username, password, date) " +
            "values (?, ?, ?, ?, ?)";

    //Creates a new database helper
    public DatabaseHelper(Context context) {
        this.context = context;
        PasswordOpenHelper openHelper = new PasswordOpenHelper(this.context);
        this.db = openHelper.getWritableDatabase();
    }


    //inserts a new account into the database
    public long insertAccount(String account_name, String password) {
        this.currentStmt = this.db.compileStatement(NEW_USER_QUERY);
        this.currentStmt.bindString(1, account_name);
        this.currentStmt.bindString(2, password);
        return this.currentStmt.executeInsert();
    }

    //inserts a new geocache into the database
    public long insertSitePassword(String account_name, String website, String username, String password, String date) {
        this.currentStmt = this.db.compileStatement(NEW_PASSWORD_QUERY);
        this.currentStmt.bindString(1, account_name);
        this.currentStmt.bindString(2, website);
        this.currentStmt.bindString(3, username);
        this.currentStmt.bindString(4, password);
        this.currentStmt.bindString(5, date);
        return this.currentStmt.executeInsert();
    }

    //runs an update on the user's account password
    public int modifyAccountPassword(String account_name, String newPassword){
        ContentValues cv = new ContentValues();
        String where = "account_name=?";
        cv.put("password", newPassword);
        return db.update(ACCOUNT_TABLE, cv, where, new String[]{account_name});
    }

    //runs an update on specific password
    public int modifySitePassword(String account_name, String website, String username, String newPassword){
        ContentValues cv = new ContentValues();
        String where = "account_name=? AND website=? AND username=?";
        cv.put("password", newPassword);
        return db.update(PASSWORD_TABLE, cv, where, new String[]{account_name, website, username});
    }

    //runs an update on specific update date
    public int modifyPasswordExpiration(String account_name, String website, String username, String date){
        ContentValues cv = new ContentValues();
        String where = "account_name=? AND website=? AND username=?";
        cv.put("date", date);
        return db.update(PASSWORD_TABLE, cv, where, new String[]{account_name, website, username});
    }

    //runs an update on specific update date
    public int removePassword(String account_name, String website, String username){
        String where = "account_name=? AND website=? AND username=?";
        return db.delete(PASSWORD_TABLE, where, new String[]{account_name, website, username});
    }



    //returns a list of users matching the input username and password
    public List<String> selectAccounts(String account_name, String password) {
        //create a list of strings and run the query
        List<String> list = new ArrayList<>();
        Cursor cursor = this.db.query(ACCOUNT_TABLE, new String[] { "account_name", "password" }, "account_name = '"+ account_name +"' " +
                "AND password= '"+ password+"'", null, null, null, "account_name desc");
        if (cursor.moveToFirst()) {
            do {
                //add the results to the list
                list.add(cursor.getString(0));
                list.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }

    //Returns a boolean whether or not a username has already been taken
    public boolean usernameTaken(String account_name) {
        //create a list of strings and run the query
        List<String> list = new ArrayList<>();
        Cursor cursor = this.db.query(ACCOUNT_TABLE, new String[] {"account_name"}, "account_name = '"+ account_name +"'", null, null, null, "account_name desc");
        if (cursor.moveToFirst()) {
            do {
                //add the results to the list
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        //return true if the list is not empty
        return list.size() != 0;
    }

    //Returns a boolean whether or not any accounts have been created
    public boolean firstTime() {
        //create a list of strings and run the query
        List<String> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Accounts", null);
        if (cursor.moveToFirst()) {
            do {
                //add the results to the list
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        //return true if the list is empty
        return list.size() == 0;
    }

    public List<PasswordInfo> selectPasswords(String account_name) {
        //create a list of geocaches
        List<PasswordInfo> list = new ArrayList<>();
        //build the query
        String query = "SELECT * FROM " + PASSWORD_TABLE;
        //do not include a search on the cacheNum if it is 0
        query += " WHERE account_name='" + account_name;
        query+="' ORDER BY account_name DESC";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                //add a new geocache to the list for each item returned
                String dateString = cursor.getString(cursor.getColumnIndex("date"));
                DateHelper date = DateHelper.stringToDateHelper(dateString);
                PasswordInfo p = new PasswordInfo(cursor.getString(cursor.getColumnIndex("website")), cursor.getString(cursor.getColumnIndex("username")), cursor.getString(cursor.getColumnIndex("password")), date);
                list.add(p);
            } while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }



    //Returns all accounts in the database
//    public List<Account> getAllAccounts() {
//        //create an account list and run the query
//        List<Account> list = new ArrayList<>();
//        String query = "SELECT * FROM Accounts";
//        Cursor cursor = db.rawQuery(query, null);
//        if (cursor.moveToFirst()) {
//            do {
//                //add a new account to the list for each item returned
//                Account a = new Account();
//                a.setUsername(cursor.getString(0));
//                a.setPoints(0);
//                a.setImage(cursor.getBlob(2));
//                list.add(a);
//            } while (cursor.moveToNext());
//        }
//        if (!cursor.isClosed()) {
//            cursor.close();
//        }
//        return list;
//    }


    private static class PasswordOpenHelper extends SQLiteOpenHelper {
        PasswordOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //creates all the tables needed for app functionality
            db.execSQL("CREATE TABLE " + ACCOUNT_TABLE + "(account_name TEXT PRIMARY KEY, password TEXT)");
            db.execSQL("CREATE TABLE " + PASSWORD_TABLE + "(account_name TEXT, website TEXT, username TEXT, password TEXT, " +
                    "date TEXT, PRIMARY KEY(account_name, website, username))");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //drop and recreate tables on upgrade
            Log.w("Example", "Upgrading database; this will drop and recreate the tables.");
            db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + PASSWORD_TABLE);
            onCreate(db);
        }
    }
}

