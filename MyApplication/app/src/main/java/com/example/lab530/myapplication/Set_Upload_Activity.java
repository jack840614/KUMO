package com.example.lab530.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by lab530 on 2016/7/20.
 *//*
public class Set_Upload_Activity extends ActionBarActivity {

    Switch switchPassword;
    
    static SharedPreferences settings;
    static final String data = "DATA";
    static boolean switchboo ;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_upload_activity);

        setTitle("雲端上傳");
        //介面物件
        processViews();
        //物件操作
        processControllers();
        // Enabling Up navigation 這段很重要~
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switchPassword.setChecked(switchboo);

    }

    public void onPause(){
        super.onPause();
        settings = getSharedPreferences(data,0);
        settings.edit()
                .putBoolean("Switch",switchboo)
                .commit();
    }

    private void processViews() {
        switchPassword = (Switch) findViewById(R.id.switchPassword);
    }

    private void processControllers() {

        CompoundButton.OnCheckedChangeListener listener=new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {

                if(isChecked)
                {
                    switchboo=true;
                    showToast("Open");
                }
                else
                {
                    switchboo=false;
                    showToast("Close");
                }
            }

        };

        switchPassword.setOnCheckedChangeListener(listener);


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