package com.example.lab530.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lab530 on 2016/9/9.
 */
public class FileType_Fragment extends Fragment implements Serializable {

    View v;

    String[] from = {"Grallery","Content"};
    int[] to = {R.id.grallery , R.id.content };

    List<HashMap<String,String>> mList;

    int[] grallery = new int[]{
            R.drawable.type_image,
            R.drawable.type_pdf,
            R.drawable.type_doc,
            R.drawable.type_ppt,
            R.drawable.type_music,
            R.drawable.type_video,
            R.drawable.type_zip,

    };

    String[] content = {"圖片","PDF","文字檔","簡報","音樂","影片","壓縮檔"};

    SimpleAdapter mAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.test, container, false);

        mList = new ArrayList<HashMap<String, String>>();
        for(int i=0;i<7;i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("Grallery",Integer.toString(grallery[i]));
            hm.put("Content", content[i]);
            mList.add(hm);
        }

        mAdapter = new SimpleAdapter(getActivity(), mList, R.layout.drawer_simpleadapter, from, to);
        ListView listView = (ListView)v.findViewById(R.id.listView);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                //圖片
                if(position==0){
                    intent.setClass(getActivity(),Image_Activity.class);
                }
                //圖片以外
                else{
                    intent.setClass(getActivity(),FileType_Activity.class);
                    intent.putExtra("type",position+"");
                }
                startActivity(intent);
            }
        });

        return v;
    }

}
