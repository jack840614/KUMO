package com.example.lab530.myapplication;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lab530 on 2016/9/8.
 */
/*
public class test extends ActionBarActivity{

    String[] from = {"Grallery","Content"};
    int[] to = {R.id.grallery , R.id.content };

    public Content_Fragment cFragment;
    FragmentTransaction transaction;
    FragmentManager fm;

    List<HashMap<String,String>> mList;

    int[] grallery = new int[]{
            R.drawable.icon_image,
            R.drawable.icon_image,
            R.drawable.icon_image,
            R.drawable.icon_image,
            R.drawable.icon_image,
            R.drawable.icon_image,
            R.drawable.icon_image,
            R.drawable.icon_image,

    };

    String[] content = {"圖片","PDF","文字檔","簡報","音樂","影片","資料夾","封存"};

    SimpleAdapter mAdapter;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        setTitle("Test");

        mList = new ArrayList<HashMap<String, String>>();
        for(int i=0;i<8;i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("Grallery",Integer.toString(grallery[i]));
            hm.put("Content", content[i]);
            mList.add(hm);
        }

        mAdapter = new SimpleAdapter(this, mList, R.layout.drawer_simpleadapter, from, to);
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //圖片
                if(position==0){
                    showFragment();
                }
            }
        });

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

    public void showFragment(){

        Main_Activity.title=false;
        if(cFragment==null) {
            cFragment = new Content_Fragment();
        }
        fm = getSupportFragmentManager();

        transaction = fm.beginTransaction();

        transaction.replace(R.id.content_frame, cFragment);

        transaction.commit();

    }


}
*/