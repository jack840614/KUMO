package com.example.lab530.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Main_Activity extends ActionBarActivity {

    FragmentManager fm;
    static PtrrvBaseAdapter adapter;
    FragmentTransaction transaction;
    LinearLayout add_drive;
    RelativeLayout drive;
    ListView mDrawerList;
    ListView mDriveList;
    LinearLayout mDrawer;
    FloatingActionButton fab;
    List<HashMap<String,String>> mList;
    public static List<HashMap<String,String>> mList2;
    SimpleAdapter mAdapter;
    SimpleAdapter mAdapter2;
    DrawerLayout mDrawerLayout;
    RelativeLayout add;
    ActionBarDrawerToggle mDrawerToggle;
    public static Drive mService;
    private static GoogleAccountCredential mCredential;
    int mPosition = -1;
    public static final String PREF_ACCOUNT_NAME = "accountName";
    public static String accountName;
    //static Dropbox_Fragment dropboxFragment ;
    static Mix_Dropbox_Fragment dropboxFragment =null;
    Mix_Google_Fragment gdFragment = null;
    //側選項目圖片
    int[] drawergallery = new int[]{
            R.drawable.ic_menu_home2,
            R.drawable.type,
            android.R.drawable.ic_menu_manage,

    };

    static ArrayList<Item> datalist = new ArrayList<Item>();
    String[] drawercontent;

    int[] drivegallery = new int[]{
            android.R.drawable.ic_input_add
    };

    String[] drivecontent=new String[]{
            "新增雲端"
    };

    final private String Grallery = "Grallery";
    final private String Content = "Content";
    final private String Check ="Check";
    final private String Account ="Account";

    public Mix_Fragment dFragment;
    public FileType_Fragment fFragment;
    public Set_Fragment sFragment;
    private boolean driveboo=true;

    private MenuItem funtion;

    static boolean title=true;
    public static boolean GoolgleIsConnect=false;
    private static int SPLASH_TIME_OUT = 3000; //開啟畫面時間(3秒)

    public static boolean arrangestatus = true;
    public static boolean drboo=false;
    public static boolean goboo=false;
    public static boolean owboo=false;

    static String driveCategory="全部檔案";
    public static ListView item_list;

    static ConnectivityManager connManager;
    static NetworkInfo info;

    static String status = "目錄";

    static Context context;
    ImageView buttonicon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setTitle("全部檔案");
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        info = connManager.getActiveNetworkInfo();

        if(context==null)
            context = getApplicationContext();

        mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(DriveScopes.DRIVE));

        if (accountName != null){
            SharedPreferences settings =
                    getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(PREF_ACCOUNT_NAME, accountName);
            editor.apply();
        }

        showDriveFragment();//全部檔案

        //Set_Upload_Activity.settings = getSharedPreferences(Set_Upload_Activity.data,0);
        //Set_Upload_Activity.switchboo = Set_Upload_Activity.settings.getBoolean("Switch",false);


        //側選單項目文字
        drawercontent = getResources().getStringArray(R.array.content);

        //介面物件
        processViews();

        // 建立側選list選單
        mList = new ArrayList<HashMap<String, String>>();
        for(int i=0;i<3;i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put(Grallery,Integer.toString(drawergallery[i]));
            hm.put(Content, drawercontent[i]);
            mList.add(hm);
        }


        //Drive隱藏欄位的選單
        mList2=new ArrayList<HashMap<String, String>>();
        /*
        HashMap<String, String> hm = new HashMap<String,String>();
        hm.put(Grallery, Integer.toString(drivegallery[0]));
        hm.put(Content, drivecontent[0]);
        hm.put(Check,null);
        //hm.put("flag","true");
        mList2.add(hm);
        */

        //  HashMap的 key

        String[] from = {Grallery,Content};
        String[] from2 = {Grallery,Content,Account,Check};

        // Ids of views in listview_layout
        int[] to = {R.id.grallery , R.id.content };
        int[] to2 = {R.id.grallery , R.id.content,R.id.content2,R.id.check };

        // Instantiating an adapter to store each items
        mAdapter = new SimpleAdapter(this, mList, R.layout.drawer_simpleadapter, from, to);
        mAdapter2 = new SimpleAdapter(this, mList2,R.layout.drive_simpleadapter, from2, to2);

        // Setting the adapter to the listView
        mDrawerList.setAdapter(mAdapter);
        mDriveList.setAdapter(mAdapter2);

        // Getting reference to DrawerLayout
        // Creating a ToggleButton for NavigationDrawer with drawer event listener
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.drawable.ic_menu_home, R.string.drawer_open,R.string.drawer_close){

            //Called when drawer is closed
            public void onDrawerClosed(View view) {
                //所選內容的Title 設定
                highlightSelectedContent();
                //動態刷新
                supportInvalidateOptionsMenu();
                //側選返回鍵 icon是否顯示
                getSupportActionBar().setDisplayUseLogoEnabled(true);
            }

            // Called when a drawer is opened
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("功能選擇");
                supportInvalidateOptionsMenu();
                getSupportActionBar().setDisplayUseLogoEnabled(false);
            }
        };

        // 側選監聽器
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Enabling Up navigation 這段很重要~
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //給予側選返回鍵 icon 圖片像素大約35 35
        getSupportActionBar().setLogo(R.drawable.ic_menu_home);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //側選按鈕圖示
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);

        //物件操作
        processControllers();

        // Dropbox init account
        AndroidAuthSession session = buildSession();
        Dropbox.DBApi = new DropboxAPI<AndroidAuthSession>(session);





        if(Dropbox.DBApi.getSession().isLinked()){
            drboo=true;

            final HashMap<String, String> dbhm = new HashMap<String,String>();
            dbhm.put(Grallery, "" + R.drawable.dropbox);
            dbhm.put(Content, "Dropbox");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Dropbox.AccountName=Dropbox.DBApi.accountInfo().email;
                        dbhm.put(Account, Dropbox.AccountName);
                        mHandler.sendEmptyMessage(2);
                    } catch (DropboxException e) {
                    }
                }
            }).start();
            dbhm.put(Check, null);

            mList2.add(0, dbhm);

            // 刷新嚕
            mDriveList.setAdapter(mAdapter2);

        }




        if(GDisLinked()){
            Log.e("!!!!:","22");
            goboo=true;
            mCredential.setSelectedAccountName(accountName);
            Main_Activity.mService = getDriveService(mCredential);
            GoolgleIsConnect=true;
            HashMap<String, String> GDhm = new HashMap<String,String>();
            GDhm.put(Grallery, "" + R.drawable.googledrive);
            GDhm.put(Content, "GoogleDrive");
            GDhm.put(Account, accountName);
            GDhm.put(Check, null);


            if(drboo)
                mList2.add(1, GDhm);
            else
                mList2.add(0, GDhm);

            // 刷新嚕
            mDriveList.setAdapter(mAdapter2);


        }

    }

    public void getAdapter(){
        adapter = new PtrrvBaseAdapter(Main_Activity.this, Mix_Fragment.al);
        adapter.changeArrayList(Mix_Fragment.al);
    }

    //禁止螢幕轉動
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if(newConfig.orientation ==Configuration.ORIENTATION_LANDSCAPE){
            Toast.makeText(this,"landscape",Toast.LENGTH_SHORT).show();
        }else if(newConfig.orientation ==Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this,"portrait",Toast.LENGTH_SHORT).show();
        }
    }

    public void onResume()
    {
        super.onResume();
        if(item_list!=null&&goboo!=true&&drboo!=true){
            //顯示新增後狀態

        }
        AndroidAuthSession session = Dropbox.DBApi.getSession();

        if (session.authenticationSuccessful()) {
            try {
                session.finishAuthentication();
                storeAuth(session);
                Dropbox.setLoggedIn(true);
            } catch (IllegalStateException e) {
                showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
               // Log.i(TAG, "Error authenticating", e);
            }
        }
        if (accountName != null){
            SharedPreferences settings =
                    getSharedPreferences(PREF_ACCOUNT_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(PREF_ACCOUNT_NAME, accountName);
            editor.apply();

        }

/*
        if(false){
            drboo=true;

            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put(Grallery, "" + R.drawable.dropbox);
            hm.put(Content, "Dropbox");
            hm.put(Check, null);
            // 雲端排序

            mList2.add(0, hm);

            // 刷新嚕
            mDriveList.setAdapter(mAdapter2);

        }
        */
        /*String accountName = getPreferences(Context.MODE_PRIVATE)
                .getString(Drive_Add_Activity.PREF_ACCOUNT_NAME, null);

        if(accountName != null){
            goboo=true;
            Drive_Add_Activity.mCredential.setSelectedAccountName(accountName);
            Main_Activity.mService = getDriveService(Drive_Add_Activity.mCredential);

            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put(Grallery, "" + R.drawable.googledrive);
            hm.put(Content, "GoogleDrive");
            hm.put(Check, null);

            if(drboo)
                mList2.add(1, hm);
            else
                mList2.add(0, hm);

            // 刷新嚕
            mDriveList.setAdapter(mAdapter2);

        }*/

        if(false){
            owboo=true;

            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put(Grallery, "" + R.drawable.owncloud);
            hm.put(Content, "OwnCloud");
            hm.put(Check, null);

            mList2.add(mList2.size(),hm);

            // 刷新嚕
            mDriveList.setAdapter(mAdapter2);

        }
        mDriveList.setAdapter(mAdapter2);
        buttonicon.setBackgroundResource(R.drawable.t);
    }

    private void processViews(){
        buttonicon = (ImageView) findViewById(R.id.imageButton);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        mDriveList = (ListView) findViewById(R.id.drive_list);
        mDrawer = (LinearLayout) findViewById(R.id.drawer);
        drive = (RelativeLayout) findViewById(R.id.drive);
        add_drive = (LinearLayout) findViewById(R.id.add_drive);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        add = (RelativeLayout)findViewById(R.id.add);
    }
    public  Drive getDriveService(GoogleAccountCredential credential) {
        return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
                .build();
    }


    private void processControllers() {

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Main_Activity.this,Drive_Add_Activity.class);
                startActivityForResult(intent,2);
            }
        });

        // 側選list 觸碰事件
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {


                //雲端勾勾取消
                for(int i=0;i<mList2.size();i++){
                    mList2.get(i).put(Check,null);
                }
                mDriveList.setAdapter(mAdapter2);


                //設定
                if(position == 2){
                    showSetFragment();
                    getSupportActionBar().setLogo(R.drawable.ic_menu_manage);
                }
                //全部檔案
                else if(position==0){

                    driveCategory="全部檔案";
                    title = true ;
                    //Intent intent = new Intent();
                    //intent.setClass(Main_Activity.this,PtrrvListViewMode.class);
                    //startActivity(intent);
                    showDriveFragment();
                    getSupportActionBar().setLogo(R.drawable.ic_menu_home);
                }
                // 類型篩選
                else if(position==1){
                    showFileTypeFragment();
                    getSupportActionBar().setLogo(R.drawable.type);
                }


                // Closing the drawer
                mDrawerLayout.closeDrawer(mDrawer);
            }
        });

        //我的雲端 觸碰事件
        drive.setOnClickListener(new RelativeLayout.OnClickListener() {

            @Override
            public void onClick(View v) {
                //顯示或關閉隱藏選單
                if (driveboo) {
                    add_drive.setVisibility(View.VISIBLE);
                    buttonicon.setBackgroundResource(R.drawable.t);
                    driveboo = false;
                } else {
                    add_drive.setVisibility(View.GONE);
                    buttonicon.setBackgroundResource(R.drawable.d);
                    driveboo = true;
                }
            }

        });


        //我的雲端 隱藏選單 觸碰事件
        mDriveList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {

                //各家雲端

                //調整title
                title = true;
                for(int i=0;i<mList2.size();i++){
                    mList2.get(i).put(Check,null);
                }
                driveCategory=mList2.get(position).get(Content);
                mList2.get(position).put(Check,Integer.toString(R.drawable.ic_checkmark_holo_light));
                mDriveList.setAdapter(mAdapter2);
                mDrawerLayout.closeDrawer(mDrawer);
                if(driveCategory.equals("Dropbox")) {
                    getSupportActionBar().setLogo(R.drawable.dropbox);
                    showDropboxFragment();
                }
                else if(driveCategory.equals("GoogleDrive")) {
                    getSupportActionBar().setLogo(R.drawable.google);
                    showGDFragment();
                }
                else if(driveCategory.equals("OwnCloud")) {
                    getSupportActionBar().setLogo(R.drawable.owncloud2);
                    showDriveFragment();
                }

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mList2.size()==0){
                    showToast("目前沒有雲端能放資料");
                }
                else {
                    Intent intent = new Intent();
                    intent.setClass(Main_Activity.this, FAB_Activity.class);
                    startActivity(intent);
                }
            }
        });

    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }

    //onCreate –> onContentChanged –> onStart –> onPostCreate –> onResume –> onPostResume –> onPause –> onStop –> onDestroy
    //onPostCreate方法是指onCreate方法彻底执行完毕的回调
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    public void onPause(){
        super.onPause();
        /*Set_Upload_Activity.settings = getSharedPreferences(Set_Upload_Activity.data,0);
        Set_Upload_Activity.settings.edit()
                .putBoolean("Switch",Set_Upload_Activity.switchboo)
                .commit();*/
    }

    //側按鈕
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(!Dropbox_Fragment.mPath.equals("/")){
            Dropbox_Fragment.mPath=Dropbox_Fragment.mPath.substring(0,Dropbox_Fragment.mPath.length()-1);
            while(Dropbox_Fragment.mPath.charAt(Dropbox_Fragment.mPath.length()-1)!='/'){
                Dropbox_Fragment.mPath=Dropbox_Fragment.mPath.substring(0,Dropbox_Fragment.mPath.length()-1);
            }
            dropboxFragment.runSetListThread();
        }

        else if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void processMenu(){
        if(title) {
            fab.setVisibility(View.VISIBLE);
           // funtion.setVisible(true);
        }
        else {
            fab.setVisibility(View.GONE);
           // funtion.setVisible(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //funtion = menu.findItem(R.id.function);
        processMenu();
        return true;
    }
/*
    public void clickMenuItem(MenuItem item) {
        // 使用參數取得使用者選擇的選單項目元件編號
        int itemId = item.getItemId();
        mDrawerLayout.closeDrawer(mDrawer);
        // 判斷該執行什麼工作，目前還沒有加入需要執行的工作
        switch (itemId) {

            case R.id.function:
                // 如果沒有雲端空間
                if(mList2.size()==1){
                    showToast("目前沒有雲端能放資料");
                }
                else {
                    // 使用Action名稱建立啟動另一個Activity元件需要的Intent物件
                    Intent intent = new Intent();
                    intent.setClass(Main_Activity.this, Menu_Activity.class);
                    startActivity(intent);
                }
                break;

        }

    }*/

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //新增雲端
        if(requestCode==2 && resultCode == 2){

            final HashMap<String, String> hm = new HashMap<String,String>();
            hm.put(Grallery, data.getStringExtra("Grallery"));
            hm.put(Content, data.getStringExtra("Content"));

            hm.put(Check, null);
            // 雲端排序
            if(data.getStringExtra("Content").equals("Dropbox")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Dropbox.AccountName=Dropbox.DBApi.accountInfo().email;
                            hm.put(Account, Dropbox.AccountName);
                            mHandler.sendEmptyMessage(2);
                        } catch (DropboxException e) {
                        }
                    }
                }).start();
                hm.put(Account, Dropbox.AccountName);
                mList2.add(0, hm);
            }
            else if(data.getStringExtra("Content").equals("OwnCloud")){
                hm.put(Account,"OwnCloud");
                mList2.add(mList2.size(),hm);
            }
            else{
                hm.put(Account,accountName);
                if(drboo)
                    mList2.add(1, hm);
                else
                    mList2.add(0, hm);

                GoolgleIsConnect=true;
            }
            // 刷新嚕
            mDriveList.setAdapter(mAdapter2);

        }

    }

    public void showDriveFragment(){

        if(dFragment==null) {
            dFragment = new Mix_Fragment();

            // Creating a Bundle object
            Bundle data = new Bundle();
            // 給陣列
            data.putBoolean("status", arrangestatus);
            dFragment.setArguments(data);
        }
        // Getting reference to the FragmentManager
        fm = getSupportFragmentManager();
        // Creating a fragment transaction
        transaction = fm.beginTransaction();
        // Adding a fragment to the fragment transaction
        transaction.replace(R.id.content_frame, dFragment);
        // Committing the transaction
        transaction.commit();

    }


    public void showGDFragment(){
        if(gdFragment==null) {
            gdFragment = new Mix_Google_Fragment();

            // Creating a Bundle object
            Bundle data = new Bundle();
            // 給陣列
            //data.putSerializable("data", datalist);
            //data.putBoolean("status",arrangestatus);
            gdFragment.setArguments(data);
        }
        // Getting reference to the FragmentManager
        fm = getSupportFragmentManager();
        // Creating a fragment transaction
        transaction = fm.beginTransaction();
        // Adding a fragment to the fragment transaction
        transaction.replace(R.id.content_frame, gdFragment);
        // Committing the transaction
        transaction.commit();

    }

    public void showDropboxFragment(){
        //dropboxFragment = new Dropbox_Fragment();
        if(dropboxFragment==null) {
            dropboxFragment = new Mix_Dropbox_Fragment();
            Log.e("Mix", "" + Dropbox.DBApi.getSession().isLinked());
            // Creating a Bundle object
            Bundle data = new Bundle();
            // 給陣列
            //data.putSerializable("data", datalist);
            //data.putBoolean("status",arrangestatus);
            dropboxFragment.setArguments(data);
            // Getting reference to the FragmentManager
        }
        fm = getSupportFragmentManager();
        // Creating a fragment transaction
        transaction = fm.beginTransaction();
        // Adding a fragment to the fragment transaction
        transaction.replace(R.id.content_frame, dropboxFragment);
        // Committing the transaction
        transaction.commit();


    }

    public void showSetFragment(){



        title=false;

        if(sFragment == null)
            sFragment = new Set_Fragment();
        // Getting reference to the FragmentManager
        fm = getSupportFragmentManager();
        // Creating a fragment transaction
        transaction = fm.beginTransaction();
        // Adding a fragment to the fragment transaction
        transaction.replace(R.id.content_frame, sFragment);
        // Committing the transaction
        transaction.commit();

    }

    public void showFileTypeFragment() {


        title = false;

        if (fFragment == null)
            fFragment = new FileType_Fragment();
        // Getting reference to the FragmentManager
        fm = getSupportFragmentManager();
        // Creating a fragment transaction
        transaction = fm.beginTransaction();
        // Adding a fragment to the fragment transaction
        transaction.replace(R.id.content_frame, fFragment);

        // Committing the transaction
        transaction.commit();

    }


    public void highlightSelectedContent(){
        int selectedItem = mDrawerList.getCheckedItemPosition();

        if(selectedItem > 2)
            mDrawerList.setItemChecked(mPosition, true);
        else
            mPosition = selectedItem;
        if(title)
            getSupportActionBar().setTitle(driveCategory);
        else if(mPosition!=-1)
            getSupportActionBar().setTitle(drawercontent[mPosition]);
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(Dropbox.APP_KEY, Dropbox.APP_SECRET);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }
    private void loadAuth(AndroidAuthSession session) {
        SharedPreferences prefs = getSharedPreferences(Dropbox.ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(Dropbox.ACCESS_KEY_NAME, null);
        String secret = prefs.getString(Dropbox.ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) return;

        if (key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
            session.setOAuth2AccessToken(secret);
        } else {
            // Still support using old OAuth 1 tokens.
            session.setAccessTokenPair(new AccessTokenPair(key, secret));
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeAuth(AndroidAuthSession session) {
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();

        Log.e("oauth2AccessToken",oauth2AccessToken);
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(Dropbox.ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(Dropbox.ACCESS_KEY_NAME, "oauth2:");
            edit.putString(Dropbox.ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
            return;
        }
        // Store the OAuth 1 access token, if there is one.  This is only necessary if
        // you're still using OAuth 1.
        AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
        if (oauth1AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(Dropbox.ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(Dropbox.ACCESS_KEY_NAME, oauth1AccessToken.key);
            edit.putString(Dropbox.ACCESS_SECRET_NAME, oauth1AccessToken.secret);
            edit.commit();
            return;
        }



    }
    public boolean GDisLinked() {

        accountName =  getSharedPreferences(PREF_ACCOUNT_NAME, 0)
                .getString(PREF_ACCOUNT_NAME, null);

        if (accountName != null)
            return true;
        else
            return false;

    }


    public  void getDriveContents(final String str)
    {


        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Mix_Google_Fragment. mResultList = new ArrayList<File>();
                com.google.api.services.drive.Drive.Files f1 = Main_Activity.mService.files();
                com.google.api.services.drive.Drive.Files.List request = null;

                //Log.e("!!",);
                do
                {
                    try
                    {
                        request = f1.list();
                        //request.setQ("trashed=false");
                        request.setQ("'"+str+"' in parents and trashed=false");

                        com.google.api.services.drive.model.FileList fileList = request.execute();
                        Mix_Google_Fragment.mResultList.addAll(fileList.getItems()); // !!

                        for(int i=0;i<Mix_Google_Fragment.mResultList.size();i++){
                            if(Mix_Google_Fragment.mResultList.get(i).getMimeType().equals("application/vnd.google-apps.folder")){
                                Mix_Google_Fragment.mResultList.add(0,Mix_Google_Fragment.mResultList.get(i));
                                Mix_Google_Fragment.mResultList.remove(i+1);
                            }
                        }
                        request.setPageToken(fileList.getNextPageToken());
                    } catch (UserRecoverableAuthIOException e) {
                        //startActivityForResult(e.getIntent(), Mix_Google_Fragment.REQUEST_AUTHORIZATION);
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (request != null)
                        {
                            request.setPageToken(null);
                        }
                    }
                } while (request.getPageToken() !=null && request.getPageToken().length() > 0);
                populateListView();

            }
        });
        t.start();
    }
    public void populateListView()
    {



        Mix_Google_Fragment.al = new ArrayList<Mix_Item>();
        for(File tmp : Mix_Google_Fragment.mResultList)
        {
            if(tmp.getMimeType().equals("application/vnd.google-apps.folder")){
                String iP=tmp.getThumbnailLink();
                String iN=tmp.getTitle();
                String ID=tmp.getId();
                String iT=tmp.getCreatedDate().toString();
                boolean iF=true;
                Mix_Google_Fragment.al.add(new Mix_Item(2,iN,iP,ID,iT,iF,0,0,""));
            }else{
                String iP=tmp.getThumbnailLink();
                String iN=tmp.getTitle();
                String ID=tmp.getId();
                String iT=tmp.getCreatedDate().toString();
                int type=11;
                boolean iF=false;
                long iS=0;
                String downloadURL=null;
                Log.e("MIME : ",tmp.getTitle()+"  "+tmp.getMimeType());
                if(tmp.getMimeType().equals("image/jpeg")||tmp.getMimeType().equals("image/png")||tmp.getMimeType().equals("image/gif")||tmp.getMimeType().equals("image/bmp"))
                    type=1;
                else if(tmp.getMimeType().equals("audio/mpeg"))
                    type=2;
                else if(tmp.getMimeType().equals("video/mp4"))
                    type=3;
                else if(tmp.getMimeType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                    type=4;
                else if(tmp.getMimeType().equals("application/pdf"))
                    type=5;
                else if(tmp.getMimeType().equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")||tmp.getMimeType().equals("application/vnd.ms-powerpoint"))
                    type=6;
                else if(tmp.getMimeType().equals("text/plain"))
                    type=7;
                else if(tmp.getMimeType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    type=8;
                else if(tmp.getMimeType().equals("text/html"))
                    type=9;
                else if(tmp.getMimeType().equals("application/zip")||tmp.getMimeType().equals("application/rar"))
                    type=10;
                else if(tmp.getMimeType().equals("application/vnd.google-apps.document")||tmp.getMimeType().equals("application/vnd.google-apps.spreadsheet")||tmp.getMimeType().equals("application/vnd.google-apps.form"))
                    type=12;
                else
                    type=11;
                if(type!=12) {
                    iS = tmp.getFileSize();
                    downloadURL=tmp.getDownloadUrl();
                }
                Mix_Google_Fragment.al.add(new Mix_Item(2,iN,iP,ID,iT,iF,type,iS,downloadURL));
            }

        }
        mHandler.sendEmptyMessage(1);

    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Mix_Google_Fragment.adapter.setCount(Mix_Google_Fragment.al.size() >= 10 ? Mix_Google_Fragment.DEFAULT_ITEM_SIZE : Mix_Google_Fragment.al.size());
                Mix_Google_Fragment.adapter.changeArrayList(Mix_Google_Fragment.al);
                Mix_Google_Fragment.adapter.notifyDataSetChanged();
                Mix_Google_Fragment.list.onFinishLoading(true, false);
                Mix_Google_Fragment.ITEM_SIZE_TEMP = 0;
                Log.e("List", "!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            else if(msg.what==2){
                mDriveList.setAdapter(mAdapter2);
            }
        }
    };
}