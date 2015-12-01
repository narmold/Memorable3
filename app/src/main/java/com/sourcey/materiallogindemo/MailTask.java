package com.sourcey.materiallogindemo;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by mmiguel12345 on 12/1/15.
 */
public class MailTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] params) {
        //currentLoc 0, _emailText1
        try {
            GMailSender sender = new GMailSender("m3morabl33@gmail.com", "P@$$W0RD");
            sender.sendMail("Memorable Location Notification",
                    "A log in was attempted at " + ((Location) params[0]).getLatitude() + " latitude and " + ((Location) params[0]).getLongitude() + " longitude"
                            + "\n This is far away from your last log in location. Use the following code to verify your identity:" + params[2],
                    "m3morabl33@gmail.com",
                    ((String) params[1]));
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);

        }

        Log.e("SendMail", "Sent Mail");
        return null;
    }
}
