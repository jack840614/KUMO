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

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


import com.dropbox.client2.exception.DropboxException;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
/**
 * Created by user on 2016/3/4.
 */
public class Touch_Rename_Activity extends Activity {

    private EditText title_text;
    static String ShowOddName="",ShowNewName="";
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.touch_rename_activity);

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
            if(getIntent().getStringExtra("RenameCloud").equals("1")){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String oldPath=getIntent().getStringExtra("RenamePath");
                        String newPath=getIntent().getStringExtra("RenameParentPath");
                        String oldName=getIntent().getStringExtra("RenameTitle");
                        ShowOddName=oldName;
                        String newName=title_text.getText().toString();
                        Log.e("ERROR",newName);
                        if(oldName.contains(".")){
                            newName=newName+oldName.substring(oldName.lastIndexOf("."));
                        }
                        Log.e("ERROR",newName);
                        newPath=newPath+newName;
                        try{
                            Dropbox.DBApi.move(oldPath,newPath);
                            ShowNewName=newName;

                            mHandler.sendEmptyMessage(1);
                        } catch (DropboxException e) {
                            Log.e("ERROR","PATH");
                        }
                    }
                }).start();
            }else if(getIntent().getStringExtra("RenameCloud").equals("2")){
                renameFile(getIntent().getStringExtra("RenamePath"),title_text.getText().toString());
            }

        }

        // 結束
        finish();
    }
    private  void renameFile(final String fileId,final String newTitle) {
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    File file = new File();
                    String oddname=getIntent().getStringExtra("RenameTitle");
                    ShowOddName=oddname;
                    String lastTitle="";
                    try {
                        lastTitle = oddname.substring(oddname.lastIndexOf("."));
                    }
                    catch(Exception e){}
                    String finalTitle=newTitle+lastTitle;

                    file.setTitle(finalTitle);

                    // Rename the file.
                    Files.Patch patchRequest = Main_Activity.mService.files().patch(fileId, file);
                    patchRequest.setFields("title");

                    patchRequest.execute();

                    ShowNewName=finalTitle;
                    mHandler.sendEmptyMessage(1);
                } catch (IOException e) {
                    System.out.println("An error occurred: " + e);

                }
            }
        });
        t.start();
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                showToast("已將「"+ShowOddName+"」重新命名為「"+ShowNewName+"」");
            }

        }
    };
    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }
}
