package com.example.lab530.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.dropbox.client2.DropboxAPI;
import com.google.api.services.drive.model.About;

import java.text.DecimalFormat;

/**
 * Created by lab530 on 2016/7/20.
 */
public class Set_Fragment extends Fragment {

    View v;
    Button Password;
    Button Photo;
    Button Notice;
    Button Concerning;
    Button CloudManagement;

    Switch switchPassword;

    static SharedPreferences settings;
    static final String data = "DATA";
    static boolean switchboo ;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Thread t = new Thread(new Runnable()  //quota
        {
            @Override
            public void run() {
                try {
                    DropboxAPI.Account DBacc =Dropbox.DBApi.accountInfo();
                    DecimalFormat df = new DecimalFormat("##.00");
                    Log.e("USE",DBacc.quotaNormal+"");
                    Log.e("TOTAL",DBacc.quota+"");
                    MyAdapter.dropbox_capacity+="已使用"+Double.parseDouble(df.format(DBacc.quotaNormal/(1024*1024*1024.0)))+"GB  總共"+(DBacc.quota/(1024*1024*1024.0) )+"GB";

                   // Log.e("dropbox_capacity",dropbox_capacity);
                    MyAdapter.dropbox_percent= Double.parseDouble(df.format (DBacc.quotaNormal/(1024*1024*1024.0) / (DBacc.quota/(1024*1024*1024.0) )*100) )+"%";
                //    Log.e("dropbox_percent",dropbox_percent);

                }catch (Exception e){
                    Log.e("Error","DB");
                }
            }
        });
        t.start();

        Thread t2 = new Thread(new Runnable()  //quota
        {
            @Override
            public void run() {
                try {
                    About about = Main_Activity.mService.about().get().execute();
                    DecimalFormat df = new DecimalFormat("##.00");
                    MyAdapter.google_capacity+="已使用"+Double.parseDouble(df.format(about.getQuotaBytesUsed()/(1024*1024*1024.0)))+"GB  總共"+(about.getQuotaBytesTotal()/(1024*1024*1024.0) )+"GB";
                    //   about.getQuotaBytesTotal() + "\n" + "Used quota (bytes): " + about.getQuotaBytesUsed());
                    MyAdapter.google_percent= Double.parseDouble(df.format (about.getQuotaBytesUsed()/(1024*1024*1024.0) / (about.getQuotaBytesTotal()/(1024*1024*1024.0) )*100) )+"%";

                }catch (Exception e){

                }
            }
        });
        t2.start();
        v = inflater.inflate(R.layout.set_fragment, container, false);
        //介面物件
        processViews();
        //物件操作
        processControllers();

        switchPassword.setChecked(switchboo);

        return v;

    }

    public void onPause(){
        super.onPause();
        settings = getActivity().getSharedPreferences(data,0);
        settings.edit()
                .putBoolean("Switch",switchboo)
                .commit();
    }

    private void processViews() {

        switchPassword = (Switch) v.findViewById(R.id.switchPassword);
        //Photo = (Button) v.findViewById(R.id.Photo);
        //Notice = (Button) v.findViewById(R.id.Notice);
        //Concerning = (Button) v.findViewById(R.id.Concerning);
        CloudManagement = (Button) v.findViewById(R.id.CloudManagement);

    }

    private void processControllers() {
    /*
        //上傳
        CompoundButton.OnCheckedChangeListener listener=new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {

                if(isChecked)
                {
                    switchboo=true;
                }
                else
                {
                    switchboo=false;
                }
            }

        };

        switchPassword.setOnCheckedChangeListener(listener);
        //相片
        Photo.setOnClickListener(new Button.OnClickListener(){

            @Override

            public void onClick(View v) {


                Intent intent = new Intent();
                intent.setClass(getActivity(),Set_Picture_Activity.class);
                startActivity(intent);
            }

        });*/
        //CloudManagement
        CloudManagement.setOnClickListener(new Button.OnClickListener(){

            @Override

            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(getActivity(),Set_DriveManagement_Activity.class);
                startActivity(intent);
            }

        });

    }


}
