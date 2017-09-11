package com.example.lab530.myapplication;

/**
 * Created by X550C on 2016/7/26.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lab530 on 2016/7/19.
 */


public class GDAdapter extends BaseAdapter {
    private LayoutInflater myInflater;
    private ArrayList<GDItem> items;

    public GDAdapter(Context context, ArrayList<GDItem> item){
        myInflater = LayoutInflater.from(context);
        this.items = item;
    }

    private class ViewHolder {
        TextView txtTitle;
        TextView txtTime;
        ImageView txtIcon;
        public ViewHolder(TextView txtTitle, TextView txtTime,ImageView txtIcon){
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
            convertView = myInflater.inflate(R.layout.gd_adapter, null);
            holder = new ViewHolder(
                    (TextView) convertView.findViewById(R.id.title),
                    (TextView) convertView.findViewById(R.id.time),
                    (ImageView)convertView.findViewById(R.id.icon)
            );
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        GDItem dbitem = (GDItem)getItem(position);
        holder.txtTitle.setText(dbitem.itemName);
        holder.txtTime.setText(dbitem.itemTime);



        switch (dbitem.itemMime){
            case 0:holder.txtIcon.setImageResource(R.drawable.icon_folder);break;
            case 1:
                    LoadImageTask task = new LoadImageTask(convertView);
                    task.execute(dbitem.itemID);
                break;
            case 2:holder.txtIcon.setImageResource(R.drawable.icon_qusetion);break;
        }

        return convertView;
    }
    public void changeArrayList(ArrayList<GDItem> item){
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
            while(!GDItem.mLruKey.contains(key)); // 直到快取出現才繼續執行
            image=GDItem.mLruCache.get(key);
            return image;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //调用setTag保存图片以便于自动更新图片
            // resultView.setTag(bitmap);
            ViewHolder holder = (ViewHolder) resultView.getTag();
            holder.txtIcon.setImageBitmap(bitmap);
            resultView.setTag(holder);
        }
    }
}
