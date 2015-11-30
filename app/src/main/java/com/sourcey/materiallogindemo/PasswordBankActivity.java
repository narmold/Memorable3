package com.sourcey.materiallogindemo;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class PasswordBankActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<PasswordInfo> passwordInfoList;
    ListView infoListView;
    DatabaseHelper dbHelp;
    String account_name;
    PasswordBankActivity reference;
    Integer EDIT_RESULT = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_bank);
        dbHelp = new DatabaseHelper(getApplicationContext());
        account_name = getApplicationContext().getSharedPreferences("Preferences", 0).getString("account_name", "Broken");
        passwordInfoList = new ArrayList<PasswordInfo>();
        infoListView = (ListView) findViewById(R.id.passwordInfoList);

        reference = this;


        Button btnNewEntry = (Button) findViewById(R.id.button_new_entry);
        btnNewEntry.setOnClickListener(this);

        passwordInfoList.addAll(dbHelp.selectPasswords(account_name));

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
        infoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                final CharSequence[] options = {"Edit Password", "Check Password", "Delete Entry", "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(PasswordBankActivity.this);
                builder.setTitle("Password for " + passwordInfoList.get(position).getWebsite());
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Edit Password")) {
                            Bundle b = new Bundle();
                            b.putString("account_name", account_name);
                            b.putString("website", passwordInfoList.get(position).getWebsite());
                            b.putString("username", passwordInfoList.get(position).getUsername());
                            b.putString("password", passwordInfoList.get(position).getPassword());

                            Intent intent = new Intent(reference, EditEntry.class);
                            intent.putExtras(b);
                            startActivityForResult(intent, EDIT_RESULT);
                        } else if (options[item].equals("Check Password")) {

                        } else if (options[item].equals("Delete Entry")) {
                            dbHelp.removePassword(account_name, passwordInfoList.get(position).getWebsite(), passwordInfoList.get(position).getUsername());
                            Toast.makeText(getApplicationContext(), "Password Deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (options[item].equals("Cancel"))

                        {
                            //if cancel is pressed then close the dialog
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == EDIT_RESULT) {
                String newPassword = data.getStringExtra("password");
                Toast.makeText(PasswordBankActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
            } else if (requestCode == 2) {

            }
        }
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_new_entry:
                Intent intent = new Intent(this, NewEntry.class);
                startActivity(intent);
                break;
            case R.id.clickArea:
                break;
        }
    }
}
