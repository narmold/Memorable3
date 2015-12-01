package com.sourcey.materiallogindemo;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final int REQUEST_VERIFY = 1;
    DatabaseHelper dh;
    GPSTracker gps;
    LoginActivity reference;
    private static Random random = new Random(System.currentTimeMillis());
    Integer verifyCode;
    Location currentLoc = new Location("current");

    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        reference = this;
        gps = new GPSTracker(reference);

        dh = new DatabaseHelper(getApplicationContext());

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        ParseUser.logInInBackground(email, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {

                                    if (gps.canGetLocation()) {
                                        currentLoc.setLatitude(gps.getLatitude());
                                        currentLoc.setLongitude(gps.getLongitude());

                                        Map<String, Double> lastLocation = dh.selectLocation(_emailText.getText().toString());

                                        if (lastLocation.size() == 0) {
                                            //Send email for first time
                                            dh.insertLocation(_emailText.getText().toString(), currentLoc.getLatitude(), currentLoc.getLongitude());
                                            onLoginSuccess();

                                        } else {
                                            Location lastLoc = new Location("current");
                                            lastLoc.setLatitude(lastLocation.get("latitude"));
                                            lastLoc.setLongitude(lastLocation.get("longitude"));
                                            double distanceFromMe = (currentLoc.distanceTo(lastLoc)) / 0.000621371;
                                            if (distanceFromMe < .5) {
                                                dh.modifyLocation(_emailText.getText().toString(), currentLoc.getLatitude(), currentLoc.getLongitude());
                                                onLoginSuccess();
                                            } else {
                                                dh.modifyLocation(_emailText.getText().toString(), currentLoc.getLatitude(), currentLoc.getLongitude());
                                                Toast.makeText(LoginActivity.this, "You're too far from your last log in", Toast.LENGTH_SHORT).show();

                                                verifyCode = random.nextInt();
                                                MailTask mailTask = new MailTask();
                                                mailTask.execute(currentLoc, _emailText.getText().toString(), verifyCode);

                                                onLoginFailed();
                                                Intent intent = new Intent(reference, VerifyIdentity.class);
                                                Bundle b = new Bundle();
                                                b.putInt("verifyCode", verifyCode);
                                                intent.putExtras(b);
                                                startActivityForResult(intent, 1);

                                                //send email and divert to different screen to input code
                                            }


                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Can't function without GPS allowed", Toast.LENGTH_SHORT).show();
                                    }

                                    // On complete call either onLoginSuccess or onLoginFailed
//                                    onLoginSuccess();
                                    // onLoginFailed();
                                    progressDialog.dismiss();
                                }
                            }, 3000);
                } else {
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    // On complete call either onLoginSuccess or onLoginFailed
                                    onLoginFailed();
                                    // onLoginFailed();
                                    progressDialog.dismiss();
                                }
                            }, 3000);
                }
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }else if(requestCode == REQUEST_VERIFY){
            if(resultCode == RESULT_OK) {
                Boolean passfail = data.getExtras().getBoolean("passfail");
                if(passfail!=null){
                    if(passfail) {
                        dh.modifyLocation(_emailText.getText().toString(), currentLoc.getLatitude(), currentLoc.getLongitude());
                        onLoginSuccess();
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
//        Intent intent = new Intent(this, Home.class);
//        startActivity(intent);
        if(!dh.usernameTaken(_emailText.getText().toString())){
            dh.insertAccount(_emailText.getText().toString(), _passwordText.getText().toString());
        }



        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences",0);
        pref.edit().putString("account_name", _emailText.getText().toString()).apply();
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}


