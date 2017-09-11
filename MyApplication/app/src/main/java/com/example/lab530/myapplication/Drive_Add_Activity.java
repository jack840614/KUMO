package com.example.lab530.myapplication;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.About;

import java.util.Arrays;

/**
 * Created by user on 2016/3/19.
 */
public class Drive_Add_Activity extends ActionBarActivity {

    ImageButton drop,google,own;
    TextView drboo,goboo,owboo;
    private static GoogleAccountCredential mCredential;
    static final int 	REQUEST_ACCOUNT_PICKER = 1;
    public static final String PREF_ACCOUNT_NAME = "accountName";

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.drive_add_activity);
        setTitle("選擇雲端");

        //介面物件
        processViews();
        //物件操作
        processControllers();

        if(Main_Activity.goboo==true){
            goboo.setText("已設定");
        }
        else{
            goboo.setText("無");
        }
        if(Main_Activity.drboo==true){
            drboo.setText("已設定");
        }
        else{
            drboo.setText("無");
        }
        if(Main_Activity.owboo==true){
            owboo.setText("已設定");
        }
        else{
            owboo.setText("無");
        }

        // Enabling Up navigation 這段很重要~
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void processViews() {

        drop = (ImageButton) findViewById(R.id.dropbox);
        google = (ImageButton) findViewById(R.id.googledrive);
        own = (ImageButton) findViewById(R.id.owncloud);
        drboo = (TextView) findViewById(R.id.drboo);
        goboo = (TextView) findViewById(R.id.goboo);
        owboo = (TextView) findViewById(R.id.owboo);

    }

    private void processControllers() {

        //dropbox 圖片觸碰事件
        drop.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(Main_Activity.drboo==false){

                    Log.e("DDDDDDDDDDDDDDD1",""+Dropbox.DBApi.getSession().isLinked());
                    Dropbox.DBApi.getSession().startOAuth2Authentication(getApplicationContext());

                    Main_Activity.drboo=true;
                    Intent intent = getIntent();
                    intent.putExtra("Grallery", "" + R.drawable.dropboxdrive);
                    intent.putExtra("Content", "Dropbox");
                    setResult(2, intent);

                    Log.e("DDDDDDDDDDDDDDD2",""+Dropbox.DBApi.getSession().isLinked());
                }
                finish();
            }
        });

        //googledrive 圖片觸碰事件
        google.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(Main_Activity.goboo==false){

                    // Connect to Google Drive
                    Main_Activity.goboo=true;
                    Intent intent = getIntent();
                    intent.putExtra("Grallery", "" + R.drawable.googledrive);
                    intent.putExtra("Content", "GoogleDrive");
                    //intent.putExtra("Account",Main_Activity.accountName);
                    setResult(2, intent);

                    mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(DriveScopes.DRIVE));

                    startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);


                    Log.e("!!!!","000");



                }
            }
        });

        //owncloud 圖片觸碰事件
        own.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(Main_Activity.owboo==false){
                    Main_Activity.owboo=true;
                    Intent intent = getIntent();
                    intent.putExtra("Grallery", "" + R.drawable.owncloud);
                    intent.putExtra("Content", "OwnCloud");
                    setResult(2, intent);
                }
                finish();
            }
        });

    }

    public  Drive getDriveService(GoogleAccountCredential credential) {
        return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
                .build();
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
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    Main_Activity.accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

                    mCredential.setSelectedAccountName( Main_Activity.accountName);
                    Main_Activity.mService = getDriveService(mCredential);

                }
                finish();
                break;
        }
    }
    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
