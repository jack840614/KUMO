package com.example.lab530.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by lab530 on 2016/8/29.
 */
public class Item_Details_Activity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 取消元件的應用程式標題
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.item_details);

        //dialog畫面控制
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高

        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 1);   //高度设置为屏幕的0.7
        p.width = (int) (d.getWidth() * 0.7);    //宽度设置为屏幕的1.0
        //p.alpha = 0.9f;      //设置本身透明度
        p.dimAmount = 0.1f;      //设置黑暗度

        getWindow().setAttributes(p);     //设置生效
        getWindow().setGravity(Gravity.RIGHT);       //设置靠右对齐

        //滑動關閉
        SlideLayout rootView = new SlideLayout(this);
        rootView.bindActivity(this);

        //按鈕關閉
        ImageButton button = (ImageButton)findViewById(R.id.back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageView icon=(ImageView)findViewById(R.id.icon);

        TextView title = (TextView)findViewById(R.id.textView);
        TextView mime = (TextView)findViewById(R.id.textView20);
        TextView time = (TextView)findViewById(R.id.textView23);
        TextView cloud = (TextView)findViewById(R.id.textView26);
        TextView size = (TextView)findViewById(R.id.textView24);


        if( getIntent().getStringExtra("DetailCloud").equals("1")){
            title.setText(getIntent().getStringExtra("DetailTitle"));
            if(getIntent().getStringExtra("DetailMime").equals("0")) {
                mime.setText("資料夾");
                icon.setImageResource(R.drawable.icon_folder);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("1")){
                mime.setText("圖片");
                icon.setImageResource(R.drawable.icon_image2);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("2")){
                mime.setText("音樂");
                icon.setImageResource(R.drawable.icon_music);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("3")){
                mime.setText("影片");
                icon.setImageResource(R.drawable.icon_video);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("4")){
                mime.setText("WORD 檔案");
                icon.setImageResource(R.drawable.icon_docs);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("5")){
                mime.setText("PDF 檔案");
                icon.setImageResource(R.drawable.icon_pdf);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("6")){
                mime.setText("PPT 檔案");
                icon.setImageResource(R.drawable.icon_ppt);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("7")){
                mime.setText("TXT 檔案");
                icon.setImageResource(R.drawable.icon_txt);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("8")){
                mime.setText("EXCEL 檔案");
                icon.setImageResource(R.drawable.icon_xlss);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("9")){
                mime.setText("HTML 檔案");
                icon.setImageResource(R.drawable.icon_htmls);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("10")){
                mime.setText("壓縮檔");
                icon.setImageResource(R.drawable.icon_zip);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("11")){
                mime.setText("其他檔案");
                icon.setImageResource(R.drawable.icon_other);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("12")){
                mime.setText("Google 文件");
                icon.setImageResource(R.drawable.icon_gg);
            }

            time.setText(getIntent().getStringExtra("DetailTime"));

            String temp = getIntent().getStringExtra("DetailParent");
            temp=temp.substring(0,temp.length()-1);
            if(temp.equals("")) {
                temp="";
            }
            else{
                temp=temp.substring(temp.lastIndexOf("/")+1,temp.length());
                temp="/"+temp;
            }

            cloud.setText("Dropbox 雲端"+temp);

            String inputSize=getIntent().getStringExtra("DetailSize");
            String outputSize=transBytes(Double.parseDouble(inputSize),0);
            size.setText(outputSize);
            Log.e("BIGSMALL1",inputSize);
            Log.e("BIGSMALL2",outputSize);
        }else if( getIntent().getStringExtra("DetailCloud").equals("2")){
            title.setText(getIntent().getStringExtra("DetailTitle"));

            if(getIntent().getStringExtra("DetailMime").equals("0")) {
                mime.setText("資料夾");
                icon.setImageResource(R.drawable.icon_folder);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("1")){
                mime.setText("圖片");
                icon.setImageResource(R.drawable.icon_image2);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("2")){
                mime.setText("音樂");
                icon.setImageResource(R.drawable.icon_music);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("3")){
                mime.setText("影片");
                icon.setImageResource(R.drawable.icon_video);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("4")){
                mime.setText("WORD 檔案");
                icon.setImageResource(R.drawable.icon_docs);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("5")){
                mime.setText("PDF 檔案");
                icon.setImageResource(R.drawable.icon_pdf);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("6")){
                mime.setText("PPT 檔案");
                icon.setImageResource(R.drawable.icon_ppt);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("7")){
                mime.setText("TXT 檔案");
                icon.setImageResource(R.drawable.icon_txt);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("8")){
                mime.setText("EXCEL 檔案");
                icon.setImageResource(R.drawable.icon_xlss);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("9")){
                mime.setText("HTML 檔案");
                icon.setImageResource(R.drawable.icon_htmls);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("10")){
                mime.setText("壓縮檔");
                icon.setImageResource(R.drawable.icon_zip);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("11")){
                mime.setText("其他檔案");
                icon.setImageResource(R.drawable.icon_other);
            }
            else if(getIntent().getStringExtra("DetailMime").equals("12")){
                mime.setText("Google 文件");
                icon.setImageResource(R.drawable.icon_gg);
            }

            time.setText(getIntent().getStringExtra("DetailTime"));

            if(PtrrvBaseAdapter.googlenowfoldername.equals("root"))
                cloud.setText("Google 雲端");
            else
                cloud.setText("Google 雲端/"+PtrrvBaseAdapter.googlenowfoldername);

            String inputSize=getIntent().getStringExtra("DetailSize");
            String outputSize=transBytes(Double.parseDouble(inputSize),0);
            size.setText(outputSize);
        }
    }


    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    static String transBytes(double bytes,int index){
        String[] b ={"Bytes","KB","MB","GB","GB以上"};
        DecimalFormat df=new DecimalFormat("#.#");
        if(bytes<1024)return df.format(bytes)+b[index];
        else return transBytes(bytes/1024.0,index+1);
    }
}
