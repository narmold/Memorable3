package com.sourcey.materiallogindemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


public class EditEntry extends AppCompatActivity implements View.OnClickListener{
    DatabaseHelper dbHelp;
    TextView website;
    TextView username;
    EditText password;
    String filledSite = "";
    String filledUser = "";
    String filledPass = "";
    String account_name = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        dbHelp = new DatabaseHelper(getApplicationContext());
        website = (TextView) findViewById(R.id.text_website);
        username = (TextView) findViewById(R.id.text_username);
        password = (EditText) findViewById(R.id.text_password);

        Bundle b = this.getIntent().getExtras();
        website.setText(b.get("website").toString());
        username.setText(b.get("username").toString());
        password.setText(b.get("password").toString());

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
                    String expirationString = (expirationDate.getMonth()).toString() + "/" +(expirationDate.getDay()).toString() + "/" + (expirationDate.getYear()).toString();
                    dbHelp.modifySitePassword(account_name, filledSite, filledUser, filledPass);
                    dbHelp.modifyPasswordExpiration(account_name, filledSite, filledUser, expirationString);

                    Intent i = new Intent();
                    i.putExtra("password", filledPass);
                    //finish the actiivty and return the chosen latitude and longitude
                    setResult(RESULT_OK, i);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Fill in all text fields!", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}

