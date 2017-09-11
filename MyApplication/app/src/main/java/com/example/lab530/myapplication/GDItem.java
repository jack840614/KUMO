package com.example.lab530.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TreeSet;

/**
 * Created by X550C on 2016/7/25.
 */
public class GDItem {
    String itemName;
     String itemPath;
    String itemTime;
    String itemID;
    boolean isFolder;
    Bitmap itemmp;
    static TreeSet<String> mLruKey = new TreeSet<String>();
    static LruCache<String, Bitmap> mLruCache= new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 1024)/2){
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount() / 1024;
        }
    };
    int itemMime;  //  0 = folder , 1 = image , 2 = qusetion
    GDItem(String iN, String ID, String iP, String iT, boolean iF, int iM){
        itemName=iN;
        itemPath=iP;
        itemTime=iT;
        isFolder=iF;
        itemID=ID;
        itemMime=iM;
        if(iM==1) {

            if (!mLruKey.contains(itemID)) {
                new AsyncTask<String, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(String... params) {
                        String url = params[0];
                        return getBitmapFromURL(url);
                    }

                    @Override
                    protected void onPostExecute(Bitmap result) {
                        Log.e("!!!", result + "");
                        mLruCache.put(itemID, result);
                        mLruKey.add(itemID);
                        itemmp = result;
                        super.onPostExecute(result);
                    }
                }.execute(iP);
            }
        }
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

}
