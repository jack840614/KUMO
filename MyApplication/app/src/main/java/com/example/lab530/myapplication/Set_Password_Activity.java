package com.example.lab530.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lab530 on 2016/7/20.
 */
/*
public class Set_Password_Activity extends ActionBarActivity {

    Switch switchPassword;
    Button changePassword;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_password_activity);

        setTitle("密碼鎖");
        //介面物件
        processViews();
        //物件操作
        processControllers();
        // Enabling Up navigation 這段很重要~
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void processViews() {

        switchPassword = (Switch) findViewById(R.id.switchPassword);
        changePassword = (Button) findViewById(R.id.changePassword);

    }

    private void processControllers() {

        CompoundButton.OnCheckedChangeListener listener=new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {

                if(isChecked)
                {
                    showToast("Open");
                }
                else
                {
                    showToast("Close");
                }
            }

        };

        switchPassword.setOnCheckedChangeListener(listener);

        changePassword.setOnClickListener(new Button.OnClickListener(){

            @Override

            public void onClick(View v) {

                showToast("Open");

            }

        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_null, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }

}
*/

