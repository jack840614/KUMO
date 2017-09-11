package com.example.lab530.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by lab530 on 2016/7/27.
 */
public class Select_Delete_Activity extends Activity{

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_delete_activity);

        // dialog 畫面控制
        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.alpha = 0.9f;      //设置本身透明度
        p.dimAmount = 0.1f;      //设置黑暗度

        getWindow().setAttributes(p);     //设置生效
        getWindow().setGravity(Gravity.CENTER);       //设置靠右对齐

    }

    // 取消 與 確定 button onclick 觸碰事件
    public void onSubmit(View view) {
        // 確定按鈕
        if (view.getId() ==R.id.ok) {

            for(int i = 0; i< Main_Activity.datalist.size(); i++){
                if(Main_Activity.datalist.get(i).check==true){
                    Main_Activity.datalist.remove(i);
                    i--;
                }
            }
        }
        for(int i = 0; i< Main_Activity.datalist.size(); i++){
            Main_Activity.datalist.get(i).check=false;
        }
        Select_Activity.index=0;
        // 結束
        finish();
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }


}
