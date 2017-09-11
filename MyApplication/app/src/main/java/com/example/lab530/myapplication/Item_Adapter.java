package com.example.lab530.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class Item_Adapter extends ArrayAdapter<Item> {

    // 畫面資源編號
    private int resource;
    // 包裝的記事資料
    private ArrayList<Item> items;

    public Item_Adapter(Context context, int resource, ArrayList<Item> items) {
        super(context, resource, items);
        this.resource = resource;
        this.items = items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        RelativeLayout itemView;

        // 讀取目前位置的記事物件
        final Item item = getItem(position);

        if (convertView == null) {
            // 建立項目畫面元件
            itemView = new RelativeLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater)
                    getContext().getSystemService(inflater);
            li.inflate(resource, itemView, true);
        }

        else {
            itemView = (RelativeLayout) convertView;
        }

        // 標題與日期時間元件
        ImageView view = (ImageView) itemView.findViewById(R.id.imageView);
        TextView titleView = (TextView) itemView.findViewById(R.id.content);
        TextView dateView = (TextView) itemView.findViewById(R.id.time);
        ImageButton button = (ImageButton) itemView.findViewById(R.id.imageButton2);

        // item_custom 的 三點點 button 觸碰事件
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), Item_Button_Touch_Activity.class);
                // 設定記事編號與標題
                intent.putExtra("position", position);
                intent.putExtra("titleText", item.getTitle());
                intent.putExtra("data",items);
                getContext().startActivity(intent);
            }
        });

        // 設定標題與日期時間
        titleView.setText(item.getTitle());
        dateView.setText(item.getLocaleDatetime());

        return itemView;
    }

    // 設定指定編號的記事資料
    public void set(int index, Item item) {
        if (index >= 0 && index < items.size()) {
            items.set(index, item);
            notifyDataSetChanged();
        }
    }
    // 讀取指定編號的記事資料
    public Item get(int index) {
        return items.get(index);
    }

}