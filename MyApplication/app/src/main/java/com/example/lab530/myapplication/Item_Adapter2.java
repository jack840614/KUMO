package com.example.lab530.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Item_Adapter2 extends ArrayAdapter<Item> {

    // 畫面資源編號
    private int resource;
    // 包裝的記事資料
    private ArrayList<Item> items;
    //int[] Array = new int[items.size()];


    static Select_Activity s;


    public Item_Adapter2(Select_Activity context, int resource, ArrayList<Item> items) {
        super(context, resource, items);
        this.resource = resource;
        this.items = items;
        this.s = context;
    }

    public Item_Adapter2(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final RelativeLayout itemView;
        // 讀取目前位置的記事物件
        final Item item = getItem(position);

        s.index=0;

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
        CheckBox checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        final ImageView view = (ImageView) itemView.findViewById(R.id.imageView);
        final TextView titleView = (TextView) itemView.findViewById(R.id.content);
        TextView dateView = (TextView) itemView.findViewById(R.id.time);

        // 設定標題與日期時間
        titleView.setText(item.getTitle());
        dateView.setText(item.getLocaleDatetime());

        s.setTitle("選擇項目");
        s.delete.setTextColor(0xFFB2B2B2);
        s.move.setTextColor(0xFFB2B2B2);
        s.copy.setTextColor(0xFFB2B2B2);

        // item_custom2 的 RelativeLayout 觸碰事件 改變 check
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
                if(checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    items.get(position).check=false;
                    s.index--;
                }
                else {
                    checkBox.setChecked(true);
                    items.get(position).check=true;
                    s.index++;
                }

                if(s.index>0){
                    s.setTitle("已選擇 "+s.index+" 個檔案");
                    s.delete.setTextColor(0xFFF93F3F);
                    s.move.setTextColor(0xFF3476FA);
                    s.copy.setTextColor(0xFF3476FA);
                }
                else{
                    s.setTitle("選擇項目");
                    s.delete.setTextColor(0xFFB2B2B2);
                    s.move.setTextColor(0xFFB2B2B2);
                    s.copy.setTextColor(0xFFB2B2B2);
                }


            }
        });

        return itemView;
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(getContext(), msg, Toast.LENGTH_LONG);
        error.show();
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