package com.example.lab530.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.dropbox.client2.exception.DropboxException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;

/**
 * Created by user on 2016/3/4.
 */
public class Item_Add_Activity extends Activity {

    private EditText title_text;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_add_activity);

        //介面物件
        processViews();

        // dialog 畫面控制
        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        //p.alpha = 0.9f;      //设置本身透明度
        p.dimAmount = 0.1f;      //设置黑暗度

        getWindow().setAttributes(p);     //设置生效
        getWindow().setGravity(Gravity.CENTER);       //设置靠右对齐

    }

    private void processViews() {
        title_text = (EditText) findViewById(R.id.editText);
    }

    // 取消 與 確定 button onclick 觸碰事件
    public void onSubmit(View view) {
        // 確定按鈕
        if (view.getId() ==R.id.ok) {
            /*
            //新增資料
            Main_Activity.datalist.add(new Item(new Date().getTime(),title_text.getText().toString()));
            // 通知資料已經改變，ListView元件才會重新顯示

            //依照名稱排序
            if(Main_Activity.arrangestatus) {
                Collections.sort(Main_Activity.datalist,
                        new Comparator<Item>() {
                            public int compare(Item o1, Item o2) {
                                return o1.title.compareTo(o2.title);
                            }
                        });
            }
            //依照時間排序
            else{
                Collections.sort(Main_Activity.datalist,
                        new Comparator<Item>() {
                            public int compare(Item o1, Item o2) {
                                return String.valueOf(o1.datetime).compareTo(String.valueOf(o2.datetime));
                            }
                        });
            }
            */
            //Log.e("WHAT",getIntent().getStringExtra("AddFolderCloud")+"");

            if(getIntent().getStringExtra("AddFolderCloud").equals("1")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Dropbox.DBApi.createFolder(getIntent().getStringExtra("path")+title_text.getText().toString());
                            mHandler.sendEmptyMessage(1);
                        } catch (DropboxException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            }
            else  if(getIntent().getStringExtra("AddFolderCloud").equals("2")){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            File body = new File();
                            body.setTitle(title_text.getText().toString());
                            body.setMimeType("application/vnd.google-apps.folder");
                            File newFolder=Main_Activity.mService.files().insert(body).execute();


                            Main_Activity.mService.files().update(newFolder.getId(), null)
                                    .setAddParents(getIntent().getStringExtra("FolderID"))
                                    .setRemoveParents("root")
                                    .setFields("id, parents")
                                    .execute();
                            mHandler.sendEmptyMessage(2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            }

        }

        // 結束
        finish();
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                showToast("新增完成");
                //Mix_Dropbox_Fragment mix_dropbox_fragment = new Mix_Dropbox_Fragment();

                if(Mix_Fragment.tmp_doing==2){ // 全部
                    /*
                    Mix_Fragment mix_fragment = new Mix_Fragment();
                    mix_fragment.initList();
                    mix_fragment.sortList();*/
                }
                else{ // 個別
                   // Main_Activity.dropboxFragment.runSetListThread();
                }

            }
            else if(msg.what ==2){
                showToast("新增完成");
/*
                Mix_Google_Fragment mix_google_fragment = new Mix_Google_Fragment();//個別
                mix_google_fragment.getDriveContents(Mix_Google_Fragment.folder);
*/
            }
        }
    };
    private void showToast(String msg) {
        Toast error = Toast.makeText(Item_Add_Activity.this, msg, Toast.LENGTH_LONG);
        error.show();
    }

}
