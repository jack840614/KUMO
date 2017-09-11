package com.example.lab530.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.dropbox.client2.exception.DropboxException;
import com.google.api.services.drive.model.File;

import java.io.IOException;

/**
 * Created by lab530 on 2016/7/27.
 */
public class Touch_Delete_Activity extends Activity{

    TextView textView,textView2;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.touch_delete_activity);

        // dialog 畫面控制
        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        //p.alpha = 0.9f;      //设置本身透明度
        p.dimAmount = 0.1f;      //设置黑暗度

        getWindow().setAttributes(p);     //设置生效
        getWindow().setGravity(Gravity.CENTER);       //设置靠右对齐

        //介面物件
        processViews();


        textView.setText(getIntent().getStringExtra("DeleteTitle"));

        if(getIntent().getStringExtra("DeleteCloud").equals("1")){
            textView2.setText("要從Dropbox移除此檔案?");
        }else if(getIntent().getStringExtra("DeleteCloud").equals("2")){
            textView2.setText("要從Google移除此檔案?");
        }

    }

    public  void processViews(){
        textView = (TextView)findViewById(R.id.textView);
        textView2 = (TextView)findViewById(R.id.textView2);
    }

    // 取消 與 確定 button onclick 觸碰事件
    public void onSubmit(View view) {
        // 確定按鈕
        if (view.getId() ==R.id.ok) {
            if(getIntent().getStringExtra("DeleteCloud").equals("1")){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Dropbox.DBApi.delete(getIntent().getStringExtra("DeletePath"));
                            mHandler.sendEmptyMessage(1);
                        } catch (DropboxException e) {
                        }
                    }
                }).start();
            }else if(getIntent().getStringExtra("DeleteCloud").equals("2")){
                deleteItemFromList(getIntent().getStringExtra("DeletePath"));
            }
        }
        // 結束
        finish();
    }

    private void deleteItemFromList(final String str)
    {


        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                            try
                            {
                                Main_Activity.mService.files().delete(str).execute();
                                mHandler.sendEmptyMessage(1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

            }
        });
        t.start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                /* 全部
                Mix_Fragment mix_fragment = new Mix_Fragment();
                mix_fragment.initList();
                mix_fragment.sortList();
*/

                showToast("已刪除「"+getIntent().getStringExtra("DeleteTitle")+"」");

            }
        }
    };
    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }
}
