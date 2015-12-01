package com.sourcey.materiallogindemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by mmiguel12345 on 12/1/15.
 */
public class VerifyIdentity extends AppCompatActivity implements View.OnClickListener {

    Integer code;
    EditText verifyCodeTextAreaPlease;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_identity);

        verifyCodeTextAreaPlease = (EditText) findViewById(R.id.text_verification_code);
        Button verifyButton = (Button) findViewById(R.id.button_verify);
        verifyButton.setOnClickListener(this);

        Bundle b = this.getIntent().getExtras();
        code = b.getInt("verifyCode");



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_verify:

                Integer firstCode = Integer.parseInt(verifyCodeTextAreaPlease.getText().toString());
                if(code.equals(firstCode)){
                    Intent i = new Intent();
                    Bundle b = new Bundle();
                    b.putBoolean("passfail", true);
                    i.putExtras(b);
                    //finish the activity and return the edit password screen
                    setResult(RESULT_OK, i);
                    finish();
                }else {
                    verifyCodeTextAreaPlease.setError("Invalid Verification Code");
                }
                break;
        }
    }
}
