package com.example.lab530.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by lab530 on 2016/7/19.
 */
public class Menu_Activity extends Activity {

    LinearLayout all;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        //介面物件
        processViews();

        //dialog 畫面控制
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.2);   //高度设置为屏幕的0.3
        p.width = (int) (d.getWidth() * 1.0);    //宽度设置为屏幕的1.0
        p.dimAmount = 0.1f;      //设置黑暗度 dailog以外畫面

        getWindow().setAttributes(p);     //设置生效
        getWindow().setGravity(Gravity.BOTTOM);       //设置靠下对齐

    }

    private void processViews() {
        all = (LinearLayout) findViewById (R.id.all);
    }

    // 建立資料夾 上傳檔案 選擇 排序選擇  relativelayout onclick 觸碰事件
    public void function(View view){

        //選擇
        if( view.getId() == R.id.choose){
            Intent intent = new Intent();
            intent.setClass(Menu_Activity.this,Select_Activity.class);
            startActivity(intent);
        }
        //排序選擇
        if( view.getId() == R.id.sort ){
            Intent intent = new Intent();
            intent.setClass(Menu_Activity.this, Item_Arrange_Activity.class);
            startActivity(intent);
        }
        finish();
    }

}
