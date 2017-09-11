package com.example.lab530.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by lab530 on 2016/7/19.
 */
public class Dropbox_Adapter extends BaseAdapter {
    private LayoutInflater myInflater;
    private ArrayList<Dropbox_Item> items;
    Context context;
    public Dropbox_Adapter(Context context, ArrayList<Dropbox_Item> item){
        myInflater = LayoutInflater.from(context);
        this.items = item;
        this.context=context;
    }

    private class ViewHolder {
        TextView txtTitle;
        TextView txtTime;
        ImageView txtIcon;
        public ViewHolder(TextView txtTitle, TextView txtTime, ImageView txtIcon){
            this.txtTitle = txtTitle;
            this.txtTime = txtTime;
            this.txtIcon = txtIcon;
        }
    }
    @Override
    public int getCount() {
        return items.size();
    }
    @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return items.indexOf(getItem(position));
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView==null){
            convertView = myInflater.inflate(R.layout.item_adapter, null);
            holder = new ViewHolder(
                    (TextView) convertView.findViewById(R.id.content),
                    (TextView) convertView.findViewById(R.id.time),
                    (ImageView)convertView.findViewById(R.id.imageView)
            );
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        Dropbox_Item dbitem = (Dropbox_Item)getItem(position);
        holder.txtTitle.setText(dbitem.itemName);
        holder.txtTime.setText(dbitem.itemTime);
        switch (dbitem.itemMime){
            case 0:holder.txtIcon.setImageResource(R.drawable.icon_folder);break;
            case 2:holder.txtIcon.setImageResource(R.drawable.icon_qusetion);break;
            //case 1:holder.txtIcon.setImageBitmap(dbitem.itemBitmap);
            case 1:holder.txtIcon.setImageResource(R.drawable.icon_image); // 使用預設圖片
               LoadImageTask task = new LoadImageTask(convertView);
               task.execute(dbitem.itemFullPath); // 用完整路徑去找cache
                break;
        }
        return convertView;
    }
    public void changeArrayList(ArrayList<Dropbox_Item> item){
        this.items = item;
    }

    class LoadImageTask extends AsyncTask<String,Void,Bitmap> {
        private View resultView;

        LoadImageTask(View resultView) {
            this.resultView = resultView;
        }
        // doInBackground完成后才会被调用
        @Override
        protected Bitmap doInBackground(String... params) {
            String key=params[0];
            Bitmap image=null;
            while(!Dropbox_Item.mLruKey.contains(key)); // 直到快取出現才繼續執行
            image=Dropbox_Item.mLruCache.get(key);
            return image;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //调用setTag保存图片以便于自动更新图片

           // resultView.setTag(bitmap);

            ViewHolder holder = (ViewHolder) resultView.getTag();
            Log.e("Change1",holder.txtTitle.toString());
            holder.txtIcon.setImageBitmap(bitmap);
            resultView.setTag(holder);
            Log.e("Change2",holder.txtTitle.toString());
        }
    }

}
