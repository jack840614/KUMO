package com.example.lab530.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by lab530 on 2016/8/9.
 */
public class Mix_Item {
    int itemCloud ; // 1:Dropbox 2:GoogleDrive
    String itemName; // 1:name  2:Title
    String itemPath; // 1:parent path  2:ThumbnailLink
    String itemFullPath; // 1:full path  2;ID
    String itemTime; // 1:update time  2:update time
    boolean isFolder;
    Bitmap itemmp;
    long itemFileSize;
    String downloadURL;
    int itemMime;  // 1:<< 0 = folder , 1 = image ,2 = music , 3 = video , 4 = doc , 5 = pdf , 6 = ppt , 7 = txt , 8 = xlss 9 = html , 10 = zip , 11 = other
    static TreeSet<String> mLruKey = new TreeSet<String>();
    static LruCache<String, Bitmap> mLruCache= new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 1024)/2){
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount() / 1024;
        }
    };
    Mix_Item(int iC,String iN, String iP,String iD, String iT, boolean iF, int iM,long iS,String URL){
        itemCloud=iC;
        if(itemCloud==1){ // Dropbox constructor
            itemName=iN;
            itemPath=iP;
            itemFullPath=itemPath+itemName;
            if(iF)
            itemTime=iT;
            else
            itemTime=dropboxChangeTime(iT);
            isFolder=iF;
            itemMime=iM;
            itemFileSize=iS;
            if(itemMime==1){
                if(!mLruKey.contains(itemFullPath)){ // If mLruKey doesn's have key of image's name , download it into mLruCache.
                    getBitmapCache gbc = new getBitmapCache();
                    gbc.execute();
                }
            }
            Log.e("SIZE",mLruCache.size()+" , "+mLruCache.maxSize());
        } // end Dropbox constructor
        else if(itemCloud==2){ // GoogleDrive constructor
            itemName=iN;
            itemPath=iP;
            itemFullPath=iD;
            itemTime=googleChangeTime(iT);
            isFolder=iF;
            itemMime=iM;
            itemFileSize=iS;
            downloadURL=URL;
            if(itemMime==1){
                if (!mLruKey.contains(itemFullPath)) {
                    new AsyncTask<String, Void, Bitmap>() {
                        @Override
                        protected Bitmap doInBackground(String... params) {
                            String url = params[0];
                            return getBitmapFromURL(url);
                        }

                        @Override
                        protected void onPostExecute(Bitmap result) {
                            Log.e("!!!", result + "");
                            mLruCache.put(itemFullPath, result);
                            mLruKey.add(itemFullPath);
                            itemmp = result;
                            super.onPostExecute(result);
                        }
                    }.execute(iP);
                }
            }
        } // end GoogleDrive constructor
    }
    private static Bitmap getBitmapFromURL(String imageUrl)  //Thumbnail image
    {
        try
        {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    class getBitmapCache extends AsyncTask<Void, Void, Bitmap> { //Dropbox 下載Bitmap
                                                                 // 參數：第一個是Params 參數的類別
                                                                 //       第二個是Progress 參數的類別
                                                                 //       第三個是Result 參數的類別
        protected Bitmap doInBackground(Void... params) {
            Bitmap itemBitmap=null;
            try {
                Log.e("Start",itemFullPath);
                DropboxAPI.DropboxInputStream DS  = Dropbox.DBApi.getThumbnailStream(itemFullPath, DropboxAPI.ThumbSize.BESTFIT_320x240, DropboxAPI.ThumbFormat.JPEG);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(DS);
                itemBitmap =  BitmapFactory.decodeStream(bufferedInputStream);
            } catch (DropboxException e) {
                e.printStackTrace();
            }

            return itemBitmap;
        }
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap!=null){
                Log.e("End",itemFullPath);
                mLruCache.put(itemFullPath,bitmap);
                mLruKey.add(itemFullPath);
            }
        }
    }
    public String dropboxChangeTime(String str) {
        String input = str.substring(5, 22);
        String arr[] = input.split(" ");// {day,month,year,time}
        HashMap<String, String> map = new HashMap<String, String>() {
            {
                put("Jan", "1");
                put("Feb", "2");
                put("Mar", "3");
                put("Apr", "4");
                put("May", "5");
                put("Jun", "6");
                put("Jul", "7");
                put("Aug", "8");
                put("Sep", "9");
                put("Oct", "10");
                put("Nov", "11");
                put("Dec", "12");
            }
        };
        String output = arr[2] + "年" + map.get(arr[1]) + "月" + arr[0] + "日" + " " + arr[3];
        return output;
    }
    public String googleChangeTime(String str) {
        String input = str.substring(0, 16);
        input=input.replace("T","-");
        String arr[] = input.split("-");// {day,month,year,time}

        String output = arr[0] + "年" +arr[1] + "月" + arr[2] + "日" + " " + arr[3];
        return output;
    }
}
