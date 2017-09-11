package com.example.lab530.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.GenericUrl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by user on 2016/3/7.
 */
public class Item_Button_Touch_Activity extends Activity {

    ListView listview;
    int[] pic={android.R.drawable.arrow_down_float,android.R.drawable.ic_menu_revert,android.R.drawable.ic_menu_edit,R.drawable.ic_clear_search_api_disabled_holo_light,android.R.drawable.ic_menu_help,R.drawable.ic_menu_download};
    String[] wri={"取得連結","移動","重新命名","刪除","詳細資料","下載"};

    int[] pic2={android.R.drawable.arrow_down_float,android.R.drawable.ic_menu_revert,android.R.drawable.ic_menu_edit,R.drawable.ic_clear_search_api_disabled_holo_light,android.R.drawable.ic_menu_help};
    String[] wri2={"取得連結","移動","重新命名","刪除","詳細資料"};
    TextView text;
    ImageView imageView;
    int itemMime;
    int icon;
    int cloud;
    String path;
    String parentpath;
    static long downloadsize;
    String title;
    String FileURL;
    String link="";
    String FileTime="";

    String listPath = "/"; // Now path.
    ArrayList<String> listArray; // Add all folder to this ArrayList.
    String[] fnames = null; // Show this list.
    myThread thread;
    AlertDialog.Builder ab; // Choose.
    static final int MOVE_FILE =2+2+2; // Dropbox move


    String folderID="root",folderName="";
    ArrayList<String> listArray2; // Add all folder to this ArrayList.
    ArrayList<String> parentfolderID;
    ArrayList<String> parentfolderName;
    String[] fnames2 = null; // Show this list.
    myThread2 thread2;
    AlertDialog.Builder ab2;
    static final int Google_MOVE_FILE = 2+2+2+2;// Google move

    static String ShowMoveTitle="",ShowMoveFolder="";
    List<com.google.api.services.drive.model.File> mResultList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 取消元件的應用程式標題
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.item_button_touch_activity);

        //介面物件
        processViews();

        text.setText(getIntent().getStringExtra("titleText"));
        itemMime = Integer.parseInt(getIntent().getStringExtra("icon"));
        path = getIntent().getStringExtra("path");
        parentpath =  getIntent().getStringExtra("parentpath");
        cloud = Integer.parseInt(getIntent().getStringExtra("cloud"));
        downloadsize = Long.parseLong(getIntent().getStringExtra("FileSize"));
        title = getIntent().getStringExtra("titleText");
        FileURL = getIntent().getStringExtra("FileURL");
        FileTime = getIntent().getStringExtra("FileTime");
        icon = R.drawable.icon_folder;
        switch (itemMime) {
            case 0:
                icon=R.drawable.icon_folder;
                break;
            case 1:
                icon=R.drawable.icon_image; // 使用預設圖片
                break;
            case 2:
                icon=R.drawable.icon_music;
                break;
            case 3:
                icon=R.drawable.icon_video;
                break;
            case 4:
                icon=R.drawable.icon_docs;
                break;
            case 5:
                icon=R.drawable.icon_pdf;
                break;
            case 6:
                icon=R.drawable.icon_ppt;
                break;
            case 7:
                icon=R.drawable.icon_txt;
                break;
            case 8:
                icon=R.drawable.icon_xlss;
                break;
            case 9:
                icon=R.drawable.icon_htmls;
                break;
            case 10:
                icon=R.drawable.icon_zip;
                break;
            case 11:
                icon=R.drawable.icon_other;
                break;
            case 12:
                icon=R.drawable.icon_gg;
                break;
        }

        imageView.setImageDrawable(getResources().getDrawable(icon));

        MyAdapter adapter=new MyAdapter(this);
        listview.setAdapter(adapter);

        //dialog畫面控制
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高

        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.6);   //高度设置为屏幕的0.7
        p.width = (int) (d.getWidth() * 1.0);    //宽度设置为屏幕的1.0
        //p.alpha = 0.9f;      //设置本身透明度
        p.dimAmount = 0.1f;      //设置黑暗度

        getWindow().setAttributes(p);     //设置生效
        getWindow().setGravity(Gravity.BOTTOM);       //设置靠下对齐

        //物件操作
        processControllers();

    }

    private void processViews() {
        listview= (ListView)findViewById(R.id.listView);
        text = (TextView)findViewById(R.id.textView);
        imageView = (ImageView)findViewById(R.id.imageView7);
    }

    private void processControllers() {

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("DOO",Mix_Fragment.doing+"");
                //取得連結
                if(position==0){

                    if(cloud==1){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    link =Dropbox.DBApi.media(path,true).url;
                                    mHandler.sendEmptyMessage(1);
                                } catch (Exception e) {
                                    mHandler.sendEmptyMessage(2);
                                    Log.e("Share link:", e.toString());
                                }

                            }
                        }).start();
                    }
                    else if(cloud==2) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    com.google.api.services.drive.model.File f = Main_Activity.mService.files().get(path).execute();
                                    Log.e("Share link:", f.getAlternateLink());
                                    link = f.getAlternateLink();
                                    mHandler.sendEmptyMessage(1);

                                } catch (Exception e) {
                                    Log.e("Share link:", e.toString());
                                }

                            }
                        }).start();
                    }


                }
                //移動
                if(position==1){
                    /*
                    Intent intent = new Intent();
                    intent.setClass(Item_Button_Touch_Activity.this,Touch_Move_Activity.class);
                    startActivity(intent);
                    */
                    if(cloud==1){
                        listPath="/"; // init path
                        Log.e("Path",listPath);
                        thread = new myThread(listPath,MOVE_FILE); // set list
                        thread.start();

                    }else if(cloud==2){
                        folderID="root";
                        thread2 = new myThread2(folderID,Google_MOVE_FILE); // set list
                        parentfolderID=new ArrayList<String>();
                        parentfolderName=new ArrayList<String>();
                        thread2.start();
                    }
                }
                //重新命名
                if(position==2){
                    Intent intent = new Intent();
                    intent.setClass(Item_Button_Touch_Activity.this,Touch_Rename_Activity.class);
                    intent.putExtra("position",getIntent().getIntExtra("position",1));
                    intent.putExtra("RenamePath",path);
                    intent.putExtra("RenameParentPath",parentpath);
                    intent.putExtra("RenameTitle",title);
                    intent.putExtra("RenameCloud",cloud+"");
                    startActivity(intent);
                }
                //刪除
                if(position==3){
                    Intent intent = new Intent();
                    intent.setClass(Item_Button_Touch_Activity.this,Touch_Delete_Activity.class);
                    intent.putExtra("position",getIntent().getIntExtra("position",1));
                    intent.putExtra("DeletePath",path);
                    intent.putExtra("DeleteTitle",title);
                    intent.putExtra("DeleteCloud",cloud+"");
                    startActivity(intent);

                }
                //詳細資料
                if(position==4){
                    Intent intent = new Intent();
                    intent.setClass(Item_Button_Touch_Activity.this,Item_Details_Activity.class);
                    intent.putExtra("position",getIntent().getIntExtra("position",1));
                    intent.putExtra("DetailParent",parentpath);
                    intent.putExtra("DetailTitle",title);
                    intent.putExtra("DetailCloud",cloud+"");
                    intent.putExtra("DetailSize",downloadsize+"");
                    intent.putExtra("DetailTime",FileTime);
                    intent.putExtra("DetailMime",itemMime+"");

                    startActivity(intent);
                }
                if(position==5){
                    if(cloud==1){
                        dropboxDownloadItemFromList a = new dropboxDownloadItemFromList();
                        a.execute(path);

                    }
                    else if(cloud==2) {
                        GDdownloadItemFromList(FileURL);
                    }
                }


                if(position!=1) finish();
            }

        });
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                copyToClipboard(link);
                Toast.makeText(getApplicationContext(),"已複製連結到剪貼簿!",Toast.LENGTH_LONG).show();
            }
            else if(msg.what==2){
                Toast.makeText(getApplicationContext(),"Dropbox資料夾不支援取得連結!",Toast.LENGTH_LONG).show();
            }
            else if(msg.what==MOVE_FILE){
                ab = new AlertDialog.Builder(Item_Button_Touch_Activity.this)
                        .setTitle("選擇目錄"+listPath)

                        .setNeutralButton("回到上一步", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if(listPath.equals(Dropbox.rootPath)){

                                }
                                else{
                                    String changeList=listPath;
                                    changeList=changeList.substring(0,changeList.length()-1);
                                    changeList=changeList.substring(0,changeList.lastIndexOf("/")+1);
                                    listPath=changeList;
                                    thread = new myThread(listPath,MOVE_FILE);
                                    thread.start();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                showToast("取消移動!");
                            }
                        })
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Log.e("MOVE","FROM" +path+" TO "+listPath+title); //
                                                    Dropbox.DBApi.move(path,listPath+title);
                                                    ShowMoveTitle=title;
                                                    String temp=listPath;
                                                    temp=temp.substring(0,temp.length()-1);
                                                    if(temp.equals("")) {
                                                        temp="根目錄";
                                                    }
                                                    else{
                                                        temp=temp.substring(temp.lastIndexOf("/")+1,temp.length());
                                                    }
                                                    ShowMoveFolder=temp;
                                                    mHandler.sendEmptyMessage(10);//移動完成
                                                    Log.e("MOVE","SUCCESS");
                                                } catch (DropboxException e) {
                                                    Log.e("MOVE","FAIL");
                                                }
                                            }
                                        }).start();
                                    }
                                })
                        .setItems(fnames, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichcountry) {
                                listPath=listPath+fnames[whichcountry]+"/";
                                thread = new myThread(listPath,MOVE_FILE);
                                thread.start();

                            }
                        });
                Log.e("ERROR","0");
                ab.show();
                Log.e("ERROR","1");
            }else if(msg.what==Google_MOVE_FILE){
                ab2 = new AlertDialog.Builder(Item_Button_Touch_Activity.this)
                        .setTitle("選擇目錄"+folderName)

                        .setNeutralButton("回到上一步", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if(parentfolderID.size()==0){
                                }
                                else{
                                    folderID=parentfolderID.remove(parentfolderID.size()-1);
                                    folderName=parentfolderName.remove(parentfolderName.size()-1);
                                    thread2 = new myThread2(folderID,Google_MOVE_FILE);
                                    thread2.start();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                showToast("取消移動!");
                            }
                        })
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {

                                                    if(Mix_Fragment.tmp_doing==3){

                                                        Main_Activity.mService.files().update(path, null)
                                                                .setAddParents(folderID)
                                                                .setRemoveParents(PtrrvBaseAdapter.googlenowfolder)
                                                                .setFields("id, parents")
                                                                .execute();
                                                        ShowMoveTitle = title;
                                                        ShowMoveFolder = folderName;


                                                        if (folderName.equals(""))
                                                            ShowMoveFolder = "根目錄";
                                                    }else {
                                                        //個別移動
                                                        Main_Activity.mService.files().update(path, null)
                                                                .setAddParents(folderID)
                                                                .setRemoveParents(Mix_Google_Fragment.folder)
                                                                .setFields("id, parents")
                                                                .execute();
                                                        ShowMoveTitle = title;
                                                        ShowMoveFolder = folderName;



                                                        if (folderName.equals(""))
                                                            ShowMoveFolder = "根目錄";
                                                    }
                                                Log.e("移動",folderID);
                                                    mHandler.sendEmptyMessage(10);//移動完成
                                                } catch (Exception e) {
                                                    Log.e("MOVE","FAIL");
                                                }
                                            }
                                        }).start();
                                    }
                                })
                        .setItems(fnames2, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichcountry) {

                                for(com.google.api.services.drive.model.File tmp : mResultList) {
                                    if (tmp.getTitle().equalsIgnoreCase(fnames2[whichcountry])) {
                                        parentfolderID.add(folderID);
                                        parentfolderName.add(folderName);
                                        folderID=tmp.getId();
                                        folderName=tmp.getTitle();
                                    }
                                }
                                thread2 = new myThread2(folderID,Google_MOVE_FILE);
                                thread2.start();

                            }
                        });
                Log.e("ERROR","0");
                ab2.show();
                Log.e("ERROR","1");
            }else if(msg.what==10){ //移動
                showToast("已將「"+ShowMoveTitle+"」移動到「"+ShowMoveFolder+"」");
            }

        }
    };
   class dropboxDownloadItemFromList extends AsyncTask<String, Long, Void> {
        String path;
        @Override
        protected Void doInBackground(String... params) {
            path=params[0];

            final byte[] buffer = new byte[1024];
            int read;
            NotificationManager mNotifyManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext());
            mBuilder.setContentTitle("Download File")
                    .setContentText("Download in progress")
                    .setSmallIcon(R.drawable.db);
            int sum=0;
            int count=1;
            int now=0;
            mBuilder.setProgress(100, 0, false);
            mNotifyManager.notify(1, mBuilder.build());


            DropboxAPI.DropboxInputStream inputputStream;
            FileOutputStream outputStream = null;
            File file = new java.io.File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(),title);
            try {
                outputStream = new FileOutputStream(file);

                inputputStream=Dropbox.DBApi.getFileStream(path,null);

                while ((read = inputputStream.read(buffer)) != -1)
                {

                    sum+=read;  //已下載的量 byte
                    now=(int)((sum*1.0/downloadsize)*100); //已下載的量 %
                    outputStream.write(buffer, 0, read);
                    //Log.e("byte : ",sum+"   "+now);
                    Log.e("byte : ",sum+"   "+downloadsize+" "+now);
                    if(now>count && count<=100) { //控制跑0-100%
                        mBuilder.setProgress(100, count, false);
                        mBuilder.setContentText("Downloading "+count+"%");
                        mNotifyManager.notify(1, mBuilder.build());
                        count++;
                    }
                }

                mBuilder.setContentText("Download complete")
                        // Removes the progress bar
                        .setProgress(0,0,false);
                mNotifyManager.notify(1, mBuilder.build());
                outputStream.close();
            } catch (DropboxException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Log.e("END DOWNLOAD","!");
        }
    }

    private void GDdownloadItemFromList(final String str)
    {


        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                            try
                            {


                                com.google.api.client.http.HttpResponse resp =
                                        Main_Activity.mService.getRequestFactory()
                                                .buildGetRequest(new GenericUrl(str))
                                                .execute();
                                InputStream iStream = resp.getContent();

                                try
                                {
                                    final java.io.File file = new java.io.File(Environment
                                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(),
                                            title);

                                    GDstoreFile(file, iStream);
                                } finally {
                                    iStream.close();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }



            }
        });
        t.start();
    }
    private void GDstoreFile(java.io.File file, InputStream iStream)
    {

        try
        {
            final OutputStream oStream = new FileOutputStream(file);
            try
            {
                try
                {
                    final byte[] buffer = new byte[1024];
                    int read;
                    NotificationManager mNotifyManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                    mBuilder.setContentTitle("Download File")
                            .setContentText("Download in progress")
                            .setSmallIcon(R.drawable.gd);
                    int sum=0;
                    int count=1;
                    int now=0;
                    mBuilder.setProgress(100, 0, false);
                    mNotifyManager.notify(1, mBuilder.build());

                    while ((read = iStream.read(buffer)) != -1)
                    {

                        sum+=read;  //已下載的量 byte
                        now=(int)(sum*100.0/downloadsize); //已下載的量 %
                        oStream.write(buffer, 0, read);
                        Log.e("byte : ",sum+"   "+now);

                        if(now>count && count<=100) { //控制跑0-100%
                            mBuilder.setProgress(100, count, false);
                            mBuilder.setContentText("Downloading "+count+"%");
                            mNotifyManager.notify(1, mBuilder.build());
                            count++;
                        }
                    }
                    mBuilder.setContentText("Download complete")
                            // Removes the progress bar
                            .setProgress(0,0,false);
                    mNotifyManager.notify(1, mBuilder.build());
                    oStream.flush();
                } finally {
                    oStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public class MyAdapter extends BaseAdapter {

        private LayoutInflater myInflater;

        public MyAdapter(Context c){
            myInflater=LayoutInflater.from(c);
        }
        @Override
        public int getCount() {
            if(itemMime==0) return pic2.length;
            else return pic.length;
        }

        @Override
        public Object getItem(int position) {
            if(itemMime==0) return pic2[position];
            else return pic[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = myInflater.inflate(R.layout.item_button_touch_view,null);
            ImageView imgLogo=(ImageView)convertView.findViewById(R.id.imageView);
            TextView text1=(TextView)convertView.findViewById(R.id.textView);
            if(itemMime==0) {
                imgLogo.setImageResource(pic2[position]);
                text1.setText(wri2[position]);
            }
            else{
                imgLogo.setImageResource(pic[position]);
                text1.setText(wri[position]);
            }
            return convertView;
        }
    }
    private void copyToClipboard(String str){  //剪貼簿功能
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(str);
            Log.e("version","1 version");
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("text label",str);
            clipboard.setPrimaryClip(clip);
            Log.e("version","2 version");
        }
    }


    class myThread extends Thread {
        // http://blog.twtnn.com/2014/10/android-4x-http-thread-handletextview.html
        String path;
        int handler;
        myThread(String input_path,int handler_type){
            path = input_path;
            handler = handler_type;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();

            DropboxAPI.Entry dirent = null;
            try {
                dirent = Dropbox.DBApi.metadata(path, 1000, null, true, null);
            } catch (DropboxException e) {
            }
            listArray = new ArrayList<String >();
            for (DropboxAPI.Entry entry:dirent.contents){
                if(entry.isDir) {
                    Log.e("FIND",entry.fileName());
                    listArray.add(entry.fileName());
                }
            }
            fnames=listArray.toArray(new String[0]);
            //以Message資料類型（不能單純以String來傳遞）傳遞資料並呼叫Handler（就是上面自訂的mHandler函數來執行更改ui的值）
            Message msg = new Message();
            msg.what=handler;
            mHandler.sendMessage(msg);
        }

    }
    class myThread2 extends Thread {
        // http://blog.twtnn.com/2014/10/android-4x-http-thread-handletextview.html
        String path;
        int handler;
        myThread2(String input_path,int handler_type){
            path = input_path;
            handler = handler_type;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();

            mResultList = new ArrayList<com.google.api.services.drive.model.File>();
            com.google.api.services.drive.Drive.Files f1 = Main_Activity.mService.files();
            com.google.api.services.drive.Drive.Files.List request = null;

            do
            {
                try
                {
                    request = f1.list();
                    //request.setQ("trashed=false");
                    request.setQ("'"+path+"' in parents and trashed=false and mimeType='application/vnd.google-apps.folder'");

                    com.google.api.services.drive.model.FileList fileList = request.execute();
                    mResultList.addAll(fileList.getItems()); // !!
                    request.setPageToken(fileList.getNextPageToken());
                } catch (UserRecoverableAuthIOException e) {

                } catch (IOException e) {
                    e.printStackTrace();
                    if (request != null)
                    {
                        request.setPageToken(null);
                    }
                }
            } while (request.getPageToken() !=null && request.getPageToken().length() > 0);

            fnames2=new String[mResultList.size()];

            for(int i=0;i<mResultList.size();i++){
                fnames2[i]=mResultList.get(i).getTitle();
            }
            //以Message資料類型（不能單純以String來傳遞）傳遞資料並呼叫Handler（就是上面自訂的mHandler函數來執行更改ui的值）
            Message msg = new Message();
            msg.what=handler;
            mHandler.sendMessage(msg);
        }

    }
    private void showToast(String msg) {
        Toast error = Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG);
        error.show();
    }
}
