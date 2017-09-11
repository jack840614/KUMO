package com.example.lab530.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.Collections;
import java.util.Comparator;

/**
 * Created by user on 2016/3/31.
 */
public class Item_Arrange_Activity extends Activity {

    ImageView namestatus;
    ImageView datestatus;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_arrange_activity);

        //介面物件
        processViews();

        // dialog 畫面控制
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高

        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.2);   //高度设置为屏幕的0.3
        p.width = (int) (d.getWidth() * 1.0);    //宽度设置为屏幕的1.0
        //p.alpha = 0.9f;      //设置本身透明度
        p.dimAmount = 0.1f;      //设置黑暗度

        getWindow().setAttributes(p);     //设置生效
        getWindow().setGravity(Gravity.BOTTOM);       //设置靠下对齐

        // 排序狀態
        if(Main_Activity.arrangestatus){
            namestatus.setVisibility(View.VISIBLE);
            datestatus.setVisibility(View.INVISIBLE);
        }
        else{
            datestatus.setVisibility(View.VISIBLE);
            namestatus.setVisibility(View.INVISIBLE);
        }

    }

    private void processViews(){

        namestatus = (ImageView)findViewById(R.id.namestatus);
        datestatus  = (ImageView)findViewById(R.id.datestatus);

    }

    // 名稱排序 與 日期排序 relativelayout onclick 觸碰事件
    public void status(View view){

        if(view.getId() == R.id.name){

            namestatus.setVisibility(View.VISIBLE);
            datestatus.setVisibility(View.INVISIBLE);
            Main_Activity.arrangestatus=true;

            Collections.sort(Main_Activity.datalist,
                    new Comparator<Item>() {
                        public int compare(Item o1, Item o2) {
                            return o1.title.compareTo(o2.title);
                        }
                    });

        }

        if(view.getId() == R.id.date){

            datestatus.setVisibility(View.VISIBLE);
            namestatus.setVisibility(View.INVISIBLE);
            Main_Activity.arrangestatus=false;

            Collections.sort(Main_Activity.datalist,
                    new Comparator<Item>() {
                        public int compare(Item o1, Item o2) {
                            return String.valueOf(o1.datetime).compareTo(String.valueOf(o2.datetime));
                        }
                    });

        }

        finish();
    }
}
