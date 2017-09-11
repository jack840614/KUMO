package com.example.lab530.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.google.api.services.drive.model.About;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lab530 on 2016/8/29.
 */
public class Set_DriveManagement_Activity extends ActionBarActivity {

    final private String Grallery = "Grallery";
    final private String Content = "Content";

    String[] from = {Grallery,Content};
    int[] to = {R.id.grallery , R.id.content};
    ListView listView;
    MyAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.drive_fragment);
        setTitle("雲端管理");

        adapter = new MyAdapter(this,Main_Activity.mList2);
        // Setting the adapter to the listView
        listView = (ListView)findViewById(R.id.item_list);
        listView.setAdapter(adapter);


        // Enabling Up navigation 這段很重要~
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_null, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onResume()
    {
        super.onResume();
        listView.setAdapter(adapter);
    }

}

class MyAdapter extends BaseAdapter {

    private LayoutInflater myInflater;
    List<HashMap<String,String>> mItemList;
    Context context;
    ImageView imageView ;
    TextView textView;
    TextView textView2 ;
    TextView textView3 ;
    TextView textView4 ;

    static String dropbox_capacity="";
    static String dropbox_percent="";

    static String google_capacity="";
    static String google_percent="";

    public MyAdapter(Context context, List<HashMap<String,String>> itemList)
    {
        myInflater = LayoutInflater.from(context);
        mItemList = itemList;
        this.context=context;
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, final ViewGroup parent)
    {

        RelativeLayout itemView;

        if (convertView == null) {
            // 建立項目畫面元件
            itemView = new RelativeLayout(context);
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater)
                    context.getSystemService(inflater);
            li.inflate(R.layout.drive_management_simpleadapter, itemView, true);
        }

        else {
            itemView = (RelativeLayout) convertView;
        }

        ImageButton imageButton = (ImageButton)itemView.findViewById(R.id.imageButton);


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if(mItemList.get(position).get("Content").toString().equals("GoogleDrive")){
                    intent.putExtra("DeleteCloud","2");
                }else{

                    intent.putExtra("DeleteCloud","1");
                }
                intent.putExtra("position",""+position);
                intent.setClass(context,Drive_Delete_Activity.class);
                context.startActivity(intent);
            }
        });

         imageView = (ImageView) itemView.findViewById(R.id.grallery);
         textView = (TextView) itemView.findViewById(R.id.content);
         textView2 = (TextView) itemView.findViewById(R.id.content2);
         textView3 = (TextView) itemView.findViewById(R.id.content3);
         textView4 = (TextView) itemView.findViewById(R.id.content4);


        imageView.setImageResource(Integer.valueOf(mItemList.get(position).get("Grallery").toString()));
        textView.setText(mItemList.get(position).get("Content").toString());
        textView2.setText(mItemList.get(position).get("Account").toString());

        if(mItemList.get(position).get("Content").toString().equals("Dropbox")) {
            textView4.setText(dropbox_capacity);
            textView3.setText(dropbox_percent);

        }

        else if(mItemList.get(position).get("Content").toString().equals("GoogleDrive")){
            textView4.setText(google_capacity);
            textView3.setText(google_percent);

        }
        return itemView;
    }

}


// 已使用1.35%  (總共 0.2 / 15 GB)
