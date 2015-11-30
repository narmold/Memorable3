package com.sourcey.materiallogindemo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class PasswordBankActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<PasswordInfo> passwordInfoList;
    ListView infoListView;
    DatabaseHelper dbHelp;
    String account_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_bank);
        dbHelp = new DatabaseHelper(getApplicationContext());
        account_name = getApplicationContext().getSharedPreferences("Preferences", 0).getString("account_name", "Broken");
        passwordInfoList = new ArrayList<PasswordInfo>();
        infoListView = (ListView) findViewById(R.id.passwordInfoList);


        passwordInfoList.addAll(dbHelp.selectPasswords(account_name));

        Button btnNewEntry = (Button) findViewById(R.id.button_new_entry);
        btnNewEntry.setOnClickListener(this);

        ListAdapter currentAdapter = new ListAdapter(this, passwordInfoList);
        infoListView.setAdapter(currentAdapter);

    }


    @Override
    protected void onResume() {
        super.onResume();
        passwordInfoList.clear();
        passwordInfoList.addAll(dbHelp.selectPasswords(account_name));
        ListAdapter currentAdapter = new ListAdapter(this, passwordInfoList);
        infoListView.setAdapter(currentAdapter);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_new_entry:
                Intent intent = new Intent(this, NewEntry.class);
                startActivity(intent);
                break;
        }
    }
}
