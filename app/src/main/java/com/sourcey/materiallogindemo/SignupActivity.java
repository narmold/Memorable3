package com.sourcey.materiallogindemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private static final Pattern namePattern = Pattern.compile("([A-Z][a-zA-Z]{1,48}) ([A-Z][a-zA-Z]{1,48} )?([A-Z][a-z\\-A-Z]*[a-zA-Z]{2,48})");
    DatabaseHelper dh;

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        dh = new DatabaseHelper(getApplicationContext());

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
        }
        else
        {
            _signupButton.setEnabled(false);
            Intent intent = new Intent(this, CheckerActivity.class);
            Bundle b = new Bundle();
            b.putString("website", _emailText.getText().toString());
            b.putString("username", _nameText.getText().toString());
            b.putString("password", "");
            intent.putExtras(b);
            startActivityForResult(intent, 1);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                password = data.getExtras().getString("password");

                Toast.makeText(this, "Password Created.", Toast.LENGTH_SHORT).show();

                final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Creating Account...");
                progressDialog.show();

                String name = _nameText.getText().toString();
                String email = _emailText.getText().toString();

                ParseUser user = new ParseUser();
                user.setEmail(email);
                user.setUsername(email);
                user.setPassword(password);

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // Hooray! Let them use the app now.
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            // On complete call either onSignupSuccess or onSignupFailed
                                            // depending on success
                                            onSignupSuccess();
                                            // onSignupFailed();
                                            progressDialog.dismiss();
                                        }
                                    }, 3000);
                        } else {
                            // Sign up didn't succeed. Look at the ParseException
                            // to figure out what went wrong
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            // On complete call either onSignupSuccess or onSignupFailed
                                            // depending on success
                                            onSignupFailed();
                                            // onSignupFailed();
                                            progressDialog.dismiss();
                                        }
                                    }, 3000);
                        }
                    }
                });

                Toast.makeText(this, "Account Created.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        if(!dh.usernameTaken(_emailText.getText().toString())){
            dh.insertAccount(_emailText.getText().toString(), password);
        }
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences",0);
        pref.edit().putString("account_name", _emailText.getText().toString()).apply();
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Invalid Input Request.", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();

        Matcher nameMatcher = namePattern.matcher(name);
        if (name.isEmpty() || !nameMatcher.find() || !nameMatcher.group().equals(name)) {
            _nameText.setError("Must be a valid name containing a valid first and last name. Middle name can be included optionally.");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        return valid;
    }
}