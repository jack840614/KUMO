package com.example.lab530.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.dropbox.client2.exception.DropboxException;

/**
 * Created by lab530 on 2016/8/30.
 */
public class Drive_Delete_Activity extends Activity {

    private EditText title_text;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.drive_delete_activity);

        // dialog 畫面控制
        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        //p.alpha = 0.9f;      //设置本身透明度
        p.dimAmount = 0.1f;      //设置黑暗度

        getWindow().setAttributes(p);     //设置生效
        getWindow().setGravity(Gravity.CENTER);       //设置靠右对齐

    }

    // 取消 與 確定 button onclick 觸碰事件
    public void onSubmit(View view) {
        // 確定按鈕

        if (view.getId() ==R.id.ok) {

            if(getIntent().getStringExtra("DeleteCloud").equals("2")) {
                SharedPreferences settings =
                        getSharedPreferences(Main_Activity.PREF_ACCOUNT_NAME, 0);
                settings.edit()
                        .clear()
                        .commit();
                Main_Activity.accountName = null;
                Main_Activity.GoolgleIsConnect = false;
                Main_Activity.mList2.remove(Integer.parseInt(getIntent().getStringExtra("position")));
                Main_Activity.goboo=false;
            }else if(getIntent().getStringExtra("DeleteCloud").equals("1")){
                Dropbox.DBApi.getSession().unlink();
                SharedPreferences prefs = getSharedPreferences(Dropbox.ACCOUNT_PREFS_NAME, 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.clear();
                edit.commit();
                Dropbox.setLoggedIn(false);
                Main_Activity.mList2.remove(Integer.parseInt(getIntent().getStringExtra("position")));
                Main_Activity.drboo=false;
            }

        }
        // 結束
        finish();
    }



}
