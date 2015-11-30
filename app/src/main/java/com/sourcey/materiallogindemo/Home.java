package com.sourcey.materiallogindemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class Home extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Button btnGenerator = (Button) findViewById(R.id.generator_button);
        btnGenerator.setOnClickListener(this);
        Button btnChecker = (Button) findViewById(R.id.checker_button);
        btnChecker.setOnClickListener(this);
        Button btnBank = (Button) findViewById(R.id.bank_button);
        btnBank.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.generator_button:
                Intent genIntent = new Intent(this, GeneratorActivity.class);
                startActivity(genIntent);
                break;
            case R.id.checker_button:
                Intent checkIntent = new Intent(this, CheckerActivity.class);
                startActivity(checkIntent);
                break;
            case R.id.bank_button:
                Intent bankIntent = new Intent(this, PasswordBankActivity.class);
                startActivity(bankIntent);
                break;
        }
    }
}

