package com.example.lab530.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by linhonghong on 2015/11/13.
 */
public class Image_Adapter extends RecyclerView.Adapter<Image_Adapter.ListItemViewHolder>{

    protected int mCount = 0;
    protected Context mContext = null;
    private ArrayList items;
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_HISVIDEO = 1;
    public static final int TYPE_MESSAGE = 2;

    public ProgressDialog selfdialog;
    private PtrrvBaseAdapter main;
    View itemView;

    //public String dropboxPath = "/";
    public Image_Adapter(Context context , ArrayList item) {
        mContext = context;
        this.items = item;
    }


    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_adapter,null);
        ListItemViewHolder a=new ListItemViewHolder(itemView);
        return  a ;
    }


    @Override
    public void onBindViewHolder(final ListItemViewHolder holder, int position) {

        //Dropbox_Item dbitem = (Dropbox_Item)getItem(position);
        Log.e("SSSSSSSSIZE","pos"+position+"/"+items.size());
        Mix_Item item = (Mix_Item)getItem(position);
        Log.e("SIZEEEEEEEE","pos"+position+"/"+items.size());
        if(item.itemCloud==1) {
            holder.driveIcon.setImageResource(R.drawable.db);
            switch (item.itemMime) {
                case 0:
                    holder.txtIcon.setImageResource(R.drawable.icon_folder);
                    break;
                case 1:
                    if(Mix_Item.mLruKey.contains(item.itemFullPath)){
                        holder.txtIcon.setImageBitmap(Mix_Item.mLruCache.get(item.itemFullPath)); //使用快取圖片
                    }
                    else{
                        holder.txtIcon.setImageResource(R.drawable.icon_image); // 使用預設圖片
                        LoadImageTask task = new LoadImageTask(holder);
                        task.execute(item.itemFullPath); // 用完整路徑去找cache
                    }
                    break;
                case 2:
                    holder.txtIcon.setImageResource(R.drawable.icon_music);
                    break;
                case 3:
                    holder.txtIcon.setImageResource(R.drawable.icon_video);
                    break;
                case 4:
                    holder.txtIcon.setImageResource(R.drawable.icon_docs);
                    break;
                case 5:
                    holder.txtIcon.setImageResource(R.drawable.icon_pdf);
                    break;
                case 6:
                    holder.txtIcon.setImageResource(R.drawable.icon_ppt);
                    break;
                case 7:
                    holder.txtIcon.setImageResource(R.drawable.icon_txt);
                    break;
                case 8:
                    holder.txtIcon.setImageResource(R.drawable.icon_xlss);
                    break;
                case 9:
                    holder.txtIcon.setImageResource(R.drawable.icon_htmls);
                    break;
                case 10:
                    holder.txtIcon.setImageResource(R.drawable.icon_zip);
                    break;
                case 11:
                    holder.txtIcon.setImageResource(R.drawable.icon_other);
                    break;
            }
        }else if(item.itemCloud==2){
            holder.driveIcon.setImageResource(R.drawable.gd);
            switch (item.itemMime) {
                case 0:
                    holder.txtIcon.setImageResource(R.drawable.icon_folder);
                    break;
                case 1:
                    if(Mix_Item.mLruKey.contains(item.itemFullPath)){
                        holder.txtIcon.setImageBitmap(Mix_Item.mLruCache.get(item.itemFullPath)); //使用快取圖片
                    }
                    else {
                        holder.txtIcon.setImageResource(R.drawable.icon_image); // 使用預設圖片
                        LoadImageTask task = new LoadImageTask(holder);
                        task.execute(item.itemFullPath); // 用完整路徑去找cache
                    }
                    break;
                case 2:
                    holder.txtIcon.setImageResource(R.drawable.icon_music);
                    break;
                case 3:
                    holder.txtIcon.setImageResource(R.drawable.icon_video);
                    break;
                case 4:
                    holder.txtIcon.setImageResource(R.drawable.icon_docs);
                    break;
                case 5:
                    holder.txtIcon.setImageResource(R.drawable.icon_pdf);
                    break;
                case 6:
                    holder.txtIcon.setImageResource(R.drawable.icon_ppt);
                    break;
                case 7:
                    holder.txtIcon.setImageResource(R.drawable.icon_txt);
                    break;
                case 8:
                    holder.txtIcon.setImageResource(R.drawable.icon_xlss);
                    break;
                case 9:
                    holder.txtIcon.setImageResource(R.drawable.icon_htmls);
                    break;
                case 10:
                    holder.txtIcon.setImageResource(R.drawable.icon_zip);
                    break;
                case 11:
                    holder.txtIcon.setImageResource(R.drawable.icon_other);
                    break;
                case 12:
                    holder.txtIcon.setImageResource(R.drawable.icon_gg);
                    break;
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                LayoutInflater inflater = (LayoutInflater) itemView.getContext()
                        .getSystemService(itemView.getContext().LAYOUT_INFLATER_SERVICE);
                //view
                View view = inflater.inflate(R.layout.item_add_activity, null);
                AlertDialog.Builder ad = new AlertDialog.Builder(itemView.getContext());
                selfdialog = ad.create();
                selfdialog.setView(view);
                //屏蔽掉点击
                selfdialog.setCancelable(false);
                selfdialog.show();*/
                final ProgressDialog dialog = new ProgressDialog(itemView.getContext());
                dialog.setMessage("Please wait while loading...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(1500);
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                        finally{
                            dialog.dismiss();
                        }
                    }
                }).start();
                /*
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //Message message = new Message();
                        //message.what = 9527;
                        //main.mHandler.sendMessage(message);
                        //关闭dialog
                        selfdialog.dismiss();
                        interrupt();
                    }
                }.start();*/




                int index = holder.getAdapterPosition();
                Mix_Item item=(Mix_Item) items.get(index);
                if(Mix_Fragment.doing>=1){ // 全部檔案
                    Log.e("ALL FILE","YA");
                    if(item.itemCloud==1){
                        Mix_Fragment.doing=2;
                        if (item.isFolder) {
                            Mix_Fragment.mPath = item.itemFullPath + "/";
                            setListThread2 thread2 = new setListThread2();
                            thread2.start();
                        }
                    }
                    else if(item.itemCloud==2){
                        Mix_Fragment.doing=3;
                        if (item.isFolder){
                            Mix_Fragment.folder=item.itemFullPath;
                            Mix_Fragment.folder_level.add(Mix_Fragment.folder);
                            getDriveContents2(Mix_Fragment.folder);

                        }
                    }
                }
                else{ // Dropbox , GoogleDrive
                    if(item.itemCloud==1) {
                        if (item.isFolder) {
                            Mix_Dropbox_Fragment.mPath = item.itemFullPath + "/";
                            setListThread thread = new setListThread();
                            thread.start();
                        }
                    }
                    else if(item.itemCloud == 2){
                        if (item.isFolder){
                            Mix_Google_Fragment.folder=item.itemFullPath;
                            Mix_Google_Fragment.folder_level.add(Mix_Google_Fragment.folder);
                            getDriveContents(Mix_Google_Fragment.folder);

                        }
                    }
                }

            }
        });
    }

    public void setCount(int count){
        mCount = count;
    }

    @Override
    public int getItemCount() {
        return mCount;
    }
    /*
        public Object getItem(int position){
            return null;
        }
    */
    public Object getItem(int arg0) {
        Log.e("D",Mix_Dropbox_Fragment.al.size()+"");
        Log.e("G",Mix_Google_Fragment.al.size()+"");
        Log.e("M",Mix_Fragment.al.size()+"");
        Log.e("do",Mix_Fragment.doing+"");
        Log.e("==========","=========");
        return items.get(arg0);
    }
    @Override
    public long getItemId(int position) {
        return items.indexOf(getItem(position));
    }

    public class ListItemViewHolder extends RecyclerView.ViewHolder{
        //TextView title,time;
        //mageView itmeimage;

        ImageView txtIcon;
        ImageView driveIcon;
        View itemView;

        public ListItemViewHolder(View itemView){
            super(itemView);
            this.itemView = itemView;

            txtIcon = (ImageView) itemView.findViewById(R.id.imageView);
            driveIcon = (ImageView) itemView.findViewById(R.id.imageView2);
        }
    }

    public void changeArrayList(ArrayList item){
        this.items = item;
    }

    class LoadImageTask extends AsyncTask<String,Void,Bitmap> {
        private ListItemViewHolder resultView;

        LoadImageTask(ListItemViewHolder resultView) {
            this.resultView = resultView;
        }
        // doInBackground完成后才会被调用
        @Override
        protected Bitmap doInBackground(String... params) {
            String key=params[0];
            Bitmap image=null;
            while(!Mix_Item.mLruKey.contains(key)); // 直到快取出現才繼續執行
            image=Mix_Item.mLruCache.get(key);
            return image;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //调用setTag保存图片以便于自动更新图片

            // resultView.setTag(bitmap);

            //resultView.getTag();
            resultView.txtIcon.setImageBitmap(bitmap);
            //resultView.setTag(holder);
        }
    }
    class setListThread extends Thread {
        // http://blog.twtnn.com/2014/10/android-4x-http-thread-handletextview.html

        @Override
        public void run() {

            super.run();

            Mix_Dropbox_Fragment.al = new ArrayList<Mix_Item>();
            if (Dropbox.DBApi.getSession().isLinked()) { // isLinked
                try {
                    DropboxAPI.Entry dirent = Dropbox.DBApi.metadata(Mix_Dropbox_Fragment.mPath, 0, null, true, null); // init path
                    for (DropboxAPI.Entry ent : dirent.contents) {
                        if (ent.isDir) { // Folder
                            String iP = Mix_Dropbox_Fragment.mPath;
                            String iN = ent.fileName();
                            String iT = ent.clientMtime;
                            boolean iF = true;
                            long iS=ent.bytes;
                            Mix_Dropbox_Fragment.al.add(new Mix_Item(1, iN, iP, "", iT, iF, 0,iS,""));
                        }
                    }
                    for (DropboxAPI.Entry ent : dirent.contents) {
                        if (!ent.isDir) { //File
                            String iP = Mix_Dropbox_Fragment.mPath;
                            String iN = ent.fileName();
                            String iT = ent.clientMtime;
                            boolean iF = false;
                            long iS=ent.bytes;
                            int type = 11;
                            if (ent.mimeType.contains("image"))
                                type = 1;
                            else if (ent.mimeType.contains("audio"))
                                type = 2;
                            else if (ent.mimeType.contains("video"))
                                type = 3;
                            else if (ent.mimeType.contains("vnd.openxmlformats-officedocument.wordprocessingml.document"))
                                type = 4;
                            else if (ent.mimeType.contains("pdf"))
                                type = 5;
                            else if (ent.mimeType.contains("vnd.openxmlformats-officedocument.presentationml.presentation") || ent.mimeType.contains("vnd.ms-powerpoint"))
                                type = 6;
                            else if (ent.mimeType.contains("plain"))
                                type = 7;
                            else if (ent.mimeType.contains("vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                                type = 8;
                            else if (ent.mimeType.contains("html"))
                                type = 9;
                            else if (ent.mimeType.contains("zip") || ent.mimeType.contains("rar"))
                                type = 10;
                            else
                                type = 11;
                            Mix_Dropbox_Fragment.al.add(new Mix_Item(1, iN, iP, "", iT, iF, type,iS,""));

                        }
                    }
                } catch (Exception e) {
                }
                //以Message資料類型（不能單純以String來傳遞）傳遞資料並呼叫Handler（就是上面自訂的mHandler函數來執行更改ui的值）
                mHandler.sendEmptyMessage(1);

            }

        }
    }

    class setListThread2 extends Thread {
        // http://blog.twtnn.com/2014/10/android-4x-http-thread-handletextview.html

        @Override
        public void run() {

            super.run();

            Mix_Fragment.al = new ArrayList<Mix_Item>();
            if (Dropbox.DBApi.getSession().isLinked()) { // isLinked
                try {
                    DropboxAPI.Entry dirent = Dropbox.DBApi.metadata(Mix_Fragment.mPath, 0, null, true, null); // init path
                    for (DropboxAPI.Entry ent : dirent.contents) {
                        if (ent.isDir) { // Folder
                            String iP = Mix_Fragment.mPath;
                            String iN = ent.fileName();
                            String iT = ent.clientMtime;
                            boolean iF = true;
                            long iS=ent.bytes;
                            Mix_Fragment.al.add(new Mix_Item(1, iN, iP, "", iT, iF, 0,iS,""));
                        }
                    }
                    for (DropboxAPI.Entry ent : dirent.contents) {
                        if (!ent.isDir) { //File
                            String iP = Mix_Fragment.mPath;
                            String iN = ent.fileName();
                            String iT = ent.clientMtime;
                            boolean iF = false;
                            long iS=ent.bytes;
                            int type = 11;
                            if (ent.mimeType.contains("image"))
                                type = 1;
                            else if (ent.mimeType.contains("audio"))
                                type = 2;
                            else if (ent.mimeType.contains("video"))
                                type = 3;
                            else if (ent.mimeType.contains("vnd.openxmlformats-officedocument.wordprocessingml.document"))
                                type = 4;
                            else if (ent.mimeType.contains("pdf"))
                                type = 5;
                            else if (ent.mimeType.contains("vnd.openxmlformats-officedocument.presentationml.presentation") || ent.mimeType.contains("vnd.ms-powerpoint"))
                                type = 6;
                            else if (ent.mimeType.contains("plain"))
                                type = 7;
                            else if (ent.mimeType.contains("vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                                type = 8;
                            else if (ent.mimeType.contains("html"))
                                type = 9;
                            else if (ent.mimeType.contains("zip") || ent.mimeType.contains("rar"))
                                type = 10;
                            else
                                type = 11;
                            Mix_Fragment.al.add(new Mix_Item(1, iN, iP, "", iT, iF, type,iS,""));

                        }
                    }
                } catch (Exception e) {
                }
                //以Message資料類型（不能單純以String來傳遞）傳遞資料並呼叫Handler（就是上面自訂的mHandler函數來執行更改ui的值）
                Mix_Fragment.choice=1;
                mHandler.sendEmptyMessage(3);

            }

        }
    }

    public void getDriveContents(final String str) {


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Mix_Google_Fragment.mResultList = new ArrayList<File>();
                com.google.api.services.drive.Drive.Files f1 = Main_Activity.mService.files();
                com.google.api.services.drive.Drive.Files.List request = null;

                //Log.e("!!",);
                do {
                    try {
                        request = f1.list();
                        //request.setQ("trashed=false");
                        request.setQ("'" + str + "' in parents and trashed=false");

                        com.google.api.services.drive.model.FileList fileList = request.execute();
                        Mix_Google_Fragment.mResultList.addAll(fileList.getItems()); // !!

                        for (int i = 0; i < Mix_Google_Fragment.mResultList.size(); i++) {
                            if (Mix_Google_Fragment.mResultList.get(i).getMimeType().equals("application/vnd.google-apps.folder")) {
                                Mix_Google_Fragment.mResultList.add(0, Mix_Google_Fragment.mResultList.get(i));
                                Mix_Google_Fragment.mResultList.remove(i + 1);
                            }
                        }
                        request.setPageToken(fileList.getNextPageToken());
                    } catch (UserRecoverableAuthIOException e) {
                        //startActivityForResult(e.getIntent(), Mix_Google_Fragment.REQUEST_AUTHORIZATION);
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (request != null) {
                            request.setPageToken(null);
                        }
                    }
                } while (request.getPageToken() != null && request.getPageToken().length() > 0);
                populateListView();

            }
        });
        t.start();
    }



    public void populateListView() {

        Mix_Google_Fragment.al = new ArrayList<Mix_Item>();
        for (File tmp : Mix_Google_Fragment.mResultList) {
            if (tmp.getMimeType().equals("application/vnd.google-apps.folder")) {
                String iP = tmp.getThumbnailLink();
                String iN = tmp.getTitle();
                String ID = tmp.getId();
                String iT = tmp.getCreatedDate().toString();
                boolean iF = true;
                Mix_Google_Fragment.al.add(new Mix_Item(2, iN, iP, ID, iT, iF, 0,0,""));
            } else {
                String iP = tmp.getThumbnailLink();
                String iN = tmp.getTitle();
                String ID = tmp.getId();
                String iT = tmp.getCreatedDate().toString();
                int type = 11;
                boolean iF = false;
                long iS=0;
                String downloadURL=null;

                Log.e("MIME : ", tmp.getTitle() + "  " + tmp.getMimeType());
                if (tmp.getMimeType().equals("image/jpeg") || tmp.getMimeType().equals("image/png") || tmp.getMimeType().equals("image/gif") || tmp.getMimeType().equals("image/bmp"))
                    type = 1;
                else if (tmp.getMimeType().equals("audio/mpeg"))
                    type = 2;
                else if (tmp.getMimeType().equals("video/mp4"))
                    type = 3;
                else if (tmp.getMimeType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                    type = 4;
                else if (tmp.getMimeType().equals("application/pdf"))
                    type = 5;
                else if (tmp.getMimeType().equals("application/vnd.openxmlformats-officedocument.presentationml.presentation") || tmp.getMimeType().equals("application/vnd.ms-powerpoint"))
                    type = 6;
                else if (tmp.getMimeType().equals("text/plain"))
                    type = 7;
                else if (tmp.getMimeType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    type = 8;
                else if (tmp.getMimeType().equals("text/html"))
                    type = 9;
                else if (tmp.getMimeType().equals("application/zip") || tmp.getMimeType().equals("application/rar"))
                    type = 10;
                else if (tmp.getMimeType().equals("application/vnd.google-apps.document") || tmp.getMimeType().equals("application/vnd.google-apps.spreadsheet") || tmp.getMimeType().equals("application/vnd.google-apps.form"))
                    type = 12;
                else
                    type = 11;
                if(type!=12) {
                    iS = tmp.getFileSize();
                    downloadURL=tmp.getDownloadUrl();
                }
                Mix_Google_Fragment.al.add(new Mix_Item(2, iN, iP, ID, iT, iF, type,iS,downloadURL));
            }

        }
        mHandler.sendEmptyMessage(2);

    }

    public void getDriveContents2(final String str) {


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Mix_Fragment.mResultList = new ArrayList<File>();
                com.google.api.services.drive.Drive.Files f1 = Main_Activity.mService.files();
                com.google.api.services.drive.Drive.Files.List request = null;

                //Log.e("!!",);
                do {
                    try {
                        request = f1.list();
                        //request.setQ("trashed=false");
                        request.setQ("'" + str + "' in parents and trashed=false");

                        com.google.api.services.drive.model.FileList fileList = request.execute();
                        Mix_Fragment.mResultList.addAll(fileList.getItems()); // !!

                        for (int i = 0; i < Mix_Fragment.mResultList.size(); i++) {
                            if (Mix_Fragment.mResultList.get(i).getMimeType().equals("application/vnd.google-apps.folder")) {
                                Mix_Fragment.mResultList.add(0, Mix_Fragment.mResultList.get(i));
                                Mix_Fragment.mResultList.remove(i + 1);
                            }
                        }
                        request.setPageToken(fileList.getNextPageToken());
                    } catch (UserRecoverableAuthIOException e) {
                        //startActivityForResult(e.getIntent(), Mix_Fragment.REQUEST_AUTHORIZATION);
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (request != null) {
                            request.setPageToken(null);
                        }
                    }
                } while (request.getPageToken() != null && request.getPageToken().length() > 0);
                populateListView2();

            }
        });
        t.start();
    }



    public void populateListView2() {

        Mix_Fragment.al = new ArrayList<Mix_Item>();
        for (File tmp : Mix_Fragment.mResultList) {
            if (tmp.getMimeType().equals("application/vnd.google-apps.folder")) {
                String iP = tmp.getThumbnailLink();
                String iN = tmp.getTitle();
                String ID = tmp.getId();
                String iT = tmp.getCreatedDate().toString();
                boolean iF = true;
                Mix_Fragment.al.add(new Mix_Item(2, iN, iP, ID, iT, iF, 0,0,""));
            } else {
                String iP = tmp.getThumbnailLink();
                String iN = tmp.getTitle();
                String ID = tmp.getId();
                String iT = tmp.getCreatedDate().toString();
                int type = 11;
                boolean iF = false;
                long iS=0;
                String downloadURL=null;

                Log.e("MIME : ", tmp.getTitle() + "  " + tmp.getMimeType());
                if (tmp.getMimeType().equals("image/jpeg") || tmp.getMimeType().equals("image/png") || tmp.getMimeType().equals("image/gif") || tmp.getMimeType().equals("image/bmp"))
                    type = 1;
                else if (tmp.getMimeType().equals("audio/mpeg"))
                    type = 2;
                else if (tmp.getMimeType().equals("video/mp4"))
                    type = 3;
                else if (tmp.getMimeType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                    type = 4;
                else if (tmp.getMimeType().equals("application/pdf"))
                    type = 5;
                else if (tmp.getMimeType().equals("application/vnd.openxmlformats-officedocument.presentationml.presentation") || tmp.getMimeType().equals("application/vnd.ms-powerpoint"))
                    type = 6;
                else if (tmp.getMimeType().equals("text/plain"))
                    type = 7;
                else if (tmp.getMimeType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    type = 8;
                else if (tmp.getMimeType().equals("text/html"))
                    type = 9;
                else if (tmp.getMimeType().equals("application/zip") || tmp.getMimeType().equals("application/rar"))
                    type = 10;
                else if (tmp.getMimeType().equals("application/vnd.google-apps.document") || tmp.getMimeType().equals("application/vnd.google-apps.spreadsheet") || tmp.getMimeType().equals("application/vnd.google-apps.form"))
                    type = 12;
                else
                    type = 11;
                if(type!=12) {
                    iS = tmp.getFileSize();
                    downloadURL=tmp.getDownloadUrl();
                }
                Mix_Fragment.al.add(new Mix_Item(2, iN, iP, ID, iT, iF, type,iS,downloadURL));
            }

        }
        Mix_Fragment.choice=2;
        mHandler.sendEmptyMessage(3);
    }


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                changeArrayList(Mix_Dropbox_Fragment.al);
                setCount(items.size() >= 10 ? Mix_Dropbox_Fragment.DEFAULT_ITEM_SIZE : items.size());
                Mix_Dropbox_Fragment.ITEM_SIZE_TEMP = 0;
//                    Mix_Dropbox_Fragment.list.setOnRefreshComplete();
                Mix_Dropbox_Fragment.list.onFinishLoading(true, false);
                notifyDataSetChanged();
            }
            else if (msg.what == 2) {
                Mix_Google_Fragment.adapter.setCount(Mix_Google_Fragment.al.size() >= 10 ? Mix_Google_Fragment.DEFAULT_ITEM_SIZE : Mix_Google_Fragment.al.size());
                Mix_Google_Fragment.adapter.changeArrayList(Mix_Google_Fragment.al);
                Mix_Google_Fragment.adapter.notifyDataSetChanged();
                Mix_Google_Fragment.list.onFinishLoading(true, false);
                Mix_Google_Fragment.ITEM_SIZE_TEMP = 0;
            }
            else if(msg.what==3){
                changeArrayList(Mix_Fragment.al);
                setCount(items.size() >= 10 ? Mix_Fragment.DEFAULT_ITEM_SIZE : items.size());
                Mix_Fragment.ITEM_SIZE_TEMP = 0;
                //  Mix_Fragment.list.setOnRefreshComplete();
                Mix_Fragment.list.onFinishLoading(true, false);

                notifyDataSetChanged();
            }
            else if(msg.what==9527){

                Toast.makeText(itemView.getContext(), "成功", Toast.LENGTH_SHORT).show();
            }
        }
    };


}

