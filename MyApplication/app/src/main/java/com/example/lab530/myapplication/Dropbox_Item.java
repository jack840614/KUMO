package com.example.lab530.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.io.BufferedInputStream;
import java.util.TreeSet;

/**
 * Created by lab530 on 2016/7/19.
 */
public class Dropbox_Item {
    String itemName;
    String itemPath;
    String itemFullPath;
    String itemTime;
    boolean isFolder;
    int itemMime;  //  0 = folder , 1 = image , 2 = qusetion
    static TreeSet<String> mLruKey = new TreeSet<String>();
    static LruCache<String, Bitmap> mLruCache= new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 1024)/2){
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount() / 1024;
        }
    };
    Dropbox_Item(String iN, String iP, String iT, boolean iF, int iM){
        itemName=iN;
        itemPath=iP;
        itemFullPath=itemPath+itemName;
        itemTime=iT;
        isFolder=iF;
        itemMime=iM;

        if(itemMime==1){
            if(!mLruKey.contains(itemFullPath)){ // If mLruKey doesn's have key of image's name , download it into mLruCache.
                getBitmapCache gbc = new getBitmapCache();
                gbc.execute();
            }
        }
        Log.e("SIZE",mLruCache.size()+" , "+mLruCache.maxSize());
    }
    class getBitmapCache extends AsyncTask<Void, Void, Bitmap> { // 參數：第一個是Params 參數的類別
                                                                //             第二個是Progress 參數的類別
                                                                //             第三個是Result 參數的類別
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

}
