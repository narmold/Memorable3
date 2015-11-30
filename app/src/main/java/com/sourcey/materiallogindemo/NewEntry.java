package com.sourcey.materiallogindemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class NewEntry extends AppCompatActivity implements View.OnClickListener{
    DatabaseHelper dbHelp;
    EditText website;
    EditText username;
    EditText password;
    String filledSite = "";
    String filledUser = "";
    String filledPass = "";
    String account_name = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        dbHelp = new DatabaseHelper(getApplicationContext());
        website = (EditText) findViewById(R.id.edit_website);
        username = (EditText) findViewById(R.id.edit_username);
        password = (EditText) findViewById(R.id.edit_password);

        account_name = getApplicationContext().getSharedPreferences("Preferences", 0).getString("account_name", "Broken");

        Button btnSave = (Button) findViewById(R.id.button_save_password);
        btnSave.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.button_save_password:
                filledSite = website.getText().toString();
                filledUser = username.getText().toString();
                filledPass = password.getText().toString();
                if(filledSite.length() != 0 && filledUser.length() != 0 && filledPass.length() != 0){
                    DateHelper expirationDate = DateHelper.getFutureDate(14);
                    String expirationString = (expirationDate.getMonth()).toString() + "/" +(expirationDate.getDay()).toString() +"/" +(expirationDate.getYear()).toString();
                    dbHelp.insertSitePassword(account_name, filledSite, filledUser, filledPass, expirationString);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Fill in all text fields!", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}

