package com.example.lab530.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lab530 on 2016/8/22.
 */
public class Upload_Activity extends Activity {

    ListView list;
    TextView textView;
    SimpleAdapter adapter;
    int cloud = 1;
    String[] from2 = {"Grallery","Content","Account","Check"};
    int[] to2 = {R.id.grallery , R.id.content,R.id.content2,R.id.check };
    public static List<HashMap<String,String>> drivelist;
    static final int 	RESULT_STORE_FILE = 4;
    static final int PICK_PICTURE =22; // Dropbox upload
    static final int 	REQUEST_AUTHORIZATION = 2;
    private static Uri mFileUri;
    LinearLayout all;

    String listPath = "/"; // Now path.
    ArrayList<String> listArray; // Add all folder to this ArrayList.
    String[] fnames = null; // Show this list.
    AlertDialog.Builder ab; // Choose.

    String dropboxUploadPath;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_choose_activity);

        //介面物件
        processViews();

        drivelist = new ArrayList<HashMap<String, String>>();

        for(int i=0;i<Main_Activity.mList2.size();i++) {
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("Grallery", Main_Activity.mList2.get(i).get("Grallery"));
            hm.put("Content", Main_Activity.mList2.get(i).get("Content"));
            hm.put("Account", Main_Activity.mList2.get(i).get("Account"));
            if(i==0)
                hm.put("Check", Integer.toString(R.drawable.ic_checkmark_holo_light));
            else
                hm.put("Check", null);
            drivelist.add(hm);
        }


        adapter = new SimpleAdapter(this, drivelist,R.layout.drive_simpleadapter, from2, to2);
        list.setAdapter(adapter);

        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        //p.alpha = 0.9f;      //设置本身透明度
        p.dimAmount = 0.1f;      //设置黑暗度

        getWindow().setAttributes(p);     //设置生效
        getWindow().setGravity(Gravity.CENTER);       //设置靠右对齐

        //物件操作
        processControllers();


    }

    private void processViews() {
        list = (ListView) findViewById(R.id.listView2);
        textView = (TextView) findViewById(R.id.textView);
    }

    private void processControllers() {

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {

                for(int i=0;i<drivelist.size();i++){
                    drivelist.get(i).put("Check",null);
                }

                drivelist.get(position).put("Check",Integer.toString(R.drawable.ic_checkmark_holo_light));

                if(drivelist.get(position).get("Content").equals("Dropbox"))
                    cloud = 1;
                else if(drivelist.get(position).get("Content").equals("GoogleDrive"))
                    cloud = 2;

                list.setAdapter(adapter);
            }
        });


        if(getIntent().getAction().equals("Add_File"))
            textView.setText("上傳雲端選擇");
        if(getIntent().getAction().equals("Add_Fold"))
            textView.setText("新增雲端選擇");

    }

    public void click(View view){

        String action = getIntent().getAction();
        Log.e("UUUUU",action);
        if(view.getId() == R.id.ok){

            if(action.equals("Add_File")) {

                Log.e("UP","Add_File");
                if (cloud == 1) {
                    Log.e("UP","DB");
                    dropboxUploadPath = "/";
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("*/*");
                    //Intent destIntent = Intent.createChooser( intent, "選擇檔案" );
                    startActivityForResult(intent, PICK_PICTURE);
                    Log.e("UP","DBGO");
                } else if (cloud == 2) {
                    Log.e("UP","GD");
                    final Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                    galleryIntent.setType("*/*");
                    startActivityForResult(galleryIntent, RESULT_STORE_FILE);
                    Log.e("UP","GDGO");
                }
            }
            else if(action.equals("Add_Fold")){
               // Toast.makeText(this, "新增檔案", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(Upload_Activity.this, Item_Add_Activity.class);
                if(cloud==1){
                    Log.e("Dpath",Mix_Fragment.mPath);
                    intent.putExtra("path",Mix_Fragment.mPath);
                    intent.putExtra("AddFolderCloud","1");
                }
                else if(cloud==2){
                    Log.e("Gpath",Mix_Fragment.folder);
                    intent.putExtra("FolderID",Mix_Fragment.folder);
                    intent.putExtra("AddFolderCloud","2");
                }
                startActivity(intent);
            }

        }
        else{
            if(action.equals("Add_File")) {
                Toast.makeText(this, "取消上傳", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        //finish(); //這行有錯，上傳無法使用

    }
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        Log.e("UP","onAc");
        switch (requestCode) {
            case PICK_PICTURE:

                Log.e("UP","PICK");
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();

                String[] proj = { MediaStore.Images.Media.DATA };

                Cursor actualimagecursor = managedQuery(uri,proj,null,null,null);

                int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                actualimagecursor.moveToFirst();

                String img_path = actualimagecursor.getString(actual_image_column_index);

                java.io.File file = new java.io.File(img_path);

                Log.e("DB","123");
                if (uri != null) {
                    Dropbox.Upload a =new Dropbox.Upload(this,dropboxUploadPath,file); //根目錄
                    a.execute();
                }
                else{

                    Log.e("DB","456");
                }
                finish();
                }
                break;
            case RESULT_STORE_FILE:

                Log.e("UP","RESULT");
                if (resultCode == Activity.RESULT_OK) {
                    mFileUri = data.getData();
                    Log.e("Yeeeeeeeeeeeeeeeeeeee", "");
                    // Save the file to Google Drive

                    if (mFileUri != null) {
                        mHandler.sendEmptyMessage(1);
                        saveFileToDrive();
                    }

                    finish();
                }
                break;
        }

    }
    private void saveFileToDrive()
    {
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    // Create URI from real path
                    String path;
                    path = getPathFromUri(mFileUri);
                    mFileUri = Uri.fromFile(new java.io.File(path));

                    ContentResolver cR = Upload_Activity.this.getContentResolver();

                    // File's binary content
                    java.io.File fileContent = new java.io.File(mFileUri.getPath());
                    FileContent mediaContent = new FileContent(cR.getType(mFileUri), fileContent);

                    // showToast("Selected " + mFileUri.getPath() + "to upload");

                    // File's meta data.
                    File body = new File();
                    body.setTitle(fileContent.getName());
                    body.setMimeType(cR.getType(mFileUri));

                    com.google.api.services.drive.Drive.Files f1 = Main_Activity.mService.files();
                    com.google.api.services.drive.Drive.Files.Insert i1 = f1.insert(body, mediaContent);
                    File file = i1.execute();

                    mHandler.sendEmptyMessage(2);

                    if (file != null)
                    {
                        //        Toast.makeText(getApplicationContext(),"Uploaded: " + file.getTitle(), Toast.LENGTH_LONG).show();
                    }
                } catch (UserRecoverableAuthIOException e) {
                    startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        });
        t.start();



    }
    public String getPathFromUri(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private void showToast(String msg) {
        Toast error = Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG);
        error.show();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);//google
            if(msg.what==1){
                showToast("開始上傳");
            }
            else if(msg.what==2){/*
                Mix_Fragment mix_fragment = new Mix_Fragment();
                mix_fragment.initList();
                mix_fragment.sortList();*/
                showToast("完成上傳");

            }
        }
    };
}

