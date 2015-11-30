package com.sourcey.materiallogindemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mmiguel12345 on 11/29/15.
 */
public class ListAdapter extends ArrayAdapter {
    //contains the context being used and a list of row items
    private final Context context;
    private final ArrayList<PasswordInfo> PasswordInfoArrayList;

    //list adapter constructor
    public ListAdapter(Context context, ArrayList<PasswordInfo> passwordInfoArrayList){
        //initialize variables
        super(context, R.layout.password_bank_item, passwordInfoArrayList);
        this.context = context;
        this.PasswordInfoArrayList = passwordInfoArrayList;
    }

    @Override
    public View getView(int position, View bankView, ViewGroup parent){
        //set the rowview
        View rowView = bankView;
        // reuse views
        if (rowView == null) {
            //if the row view is not null then inflate for display
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.password_bank_item, null);
        }

        //get the text views and set the values to the values in the list
        TextView username = (TextView) rowView.findViewById(R.id.value_username);
        TextView website = (TextView) rowView.findViewById(R.id.value_website);
        TextView password = (TextView) rowView.findViewById(R.id.value_password);
        TextView expiration = (TextView) rowView.findViewById(R.id.value_expires);

        username.setText(PasswordInfoArrayList.get(position).getUsername());
        website.setText(PasswordInfoArrayList.get(position).getWebsite());
        password.setText(PasswordInfoArrayList.get(position).getPassword());
        expiration.setText(PasswordInfoArrayList.get(position).getLastUpdate());

        return rowView;
    }
}