package com.example.lab530.myapplication;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.model.File;
import com.lhh.ptrrv.library.PullToRefreshRecyclerView;
import com.lhh.ptrrv.library.footer.loadmore.BaseLoadMoreView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * Created by user on 2016/3/20.
 */
public class Dropbox_Fragment extends Fragment implements Serializable {

    View v;
    //public ArrayList<Item> data;
    private boolean arrangestatus;
    private List<File> mResultList;
    static final int REQUEST_AUTHORIZATION = 2;
    static ArrayList<Dropbox_Item> al = new ArrayList<Dropbox_Item>();
    //static Dropbox_Adapter adapter;
    static PtrrvBaseAdapter adapter;
    static String mPath="/";
    static long lastClickTime;
    static setListThread thread;


    PullToRefreshRecyclerView list;
    private static final int MSG_CODE_REFRESH = 2;
    private static final int MSG_CODE_LOADMORE = 3;
    private static final int TIME = 1000;
    private static final int DEFAULT_ITEM_SIZE = 10;
    private static final int ITEM_SIZE_OFFSET = 5;
    private static  int ITEM_SIZE_TEMP = 0;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //畫面宣告
        //v = inflater.inflate(R.layout.drive_fragment, container, false);
        v = inflater.inflate(R.layout.activity_listview, container, false);


        //排序狀態
        arrangestatus = getArguments().getBoolean("status");

        //介面物件
        processViews();
        //物件操作
        processControllers();

        //更新清單
        runSetListThread();


        list.setSwipeEnable(true);//open swipe
        DemoLoadMoreView loadMoreView = new DemoLoadMoreView(getActivity(), list.getRecyclerView());
        loadMoreView.setLoadmoreString(getString(R.string.demo_loadmore));
        loadMoreView.setLoadMorePadding(100);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setPagingableListener(new PullToRefreshRecyclerView.PagingableListener() {
            @Override
            public void onLoadMoreItems() {
                mHandler.sendEmptyMessageDelayed(MSG_CODE_LOADMORE, TIME);
            }
        });
        list.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessageDelayed(MSG_CODE_REFRESH, TIME);
            }
        });
        list.getRecyclerView().addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));


        list.setLoadMoreFooter(loadMoreView);
        list.getLoadMoreFooter().setOnDrawListener(new BaseLoadMoreView.OnDrawListener() {
            @Override
            public boolean onDrawLoadMore(Canvas c, RecyclerView parent) {
                Log.i("onDrawLoadMore","draw load more");
                return false;
            }
        });



        //adapter  = new Dropbox_Adapter(getActivity(),al); // init adapter
        adapter = new PtrrvBaseAdapter(getActivity(),al); // init adapter
        adapter.changeArrayList(al);
        list.setAdapter(adapter);
        list.onFinishLoading(true, false);
        //Main_Activity.item_list.setAdapter(adapter); // set adapter
        return v;
    }

    private void processViews() {
        //Main_Activity.item_list = (ListView) v.findViewById(R.id.item_list);
        list = (PullToRefreshRecyclerView) v.findViewById(R.id.ptrrv);

    }

    private void processControllers() {

        //清單觸碰事件
        /*Main_Activity.item_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(isFastDoubleClick()) return; // 防止1秒內連按
                Dropbox_Item item = (Dropbox_Item)adapter.getItem(position);
                //final Dropbox_Item link_item=item;
                if(item.isFolder){
                    mPath=item.itemFullPath+"/";
                    runSetListThread();
                }
                else{
                }
            }

        });*/

        /*list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(isFastDoubleClick()) return; // 防止1秒內連按
                Dropbox_Item item = (Dropbox_Item)adapter.getItem(position);
                //final Dropbox_Item link_item=item;
                if(item.isFolder){
                    mPath=item.itemFullPath+"/";
                    runSetListThread();
                }
                else{
                }
            }

        });*/


    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                adapter.setCount(al.size()>=10?DEFAULT_ITEM_SIZE:al.size());
                adapter.changeArrayList(al);
                adapter.notifyDataSetChanged();
                list.onFinishLoading(true, false);
                Log.e("List","!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            else if (msg.what == MSG_CODE_REFRESH) {
                adapter.setCount(DEFAULT_ITEM_SIZE);
                adapter.notifyDataSetChanged();
                list.setOnRefreshComplete();
                list.onFinishLoading(true, false);
                ITEM_SIZE_TEMP=0;
                Log.e("SIZE!!!",al.size()+"");
            }   else if (msg.what == MSG_CODE_LOADMORE) {
                Log.e("SiZE???",al.size()+"");
                if(adapter.getItemCount() == al.size()){
                    //over
                    Toast.makeText(getActivity(), R.string.nomoredata, Toast.LENGTH_SHORT).show();
                    list.onFinishLoading(false, false);
                    Log.e("SIZE!!!!!",al.size()+"");
                }else {
                    if(adapter.getItemCount()+ITEM_SIZE_OFFSET>al.size()){
                        adapter.setCount(al.size());
                    }
                    else {
                        ITEM_SIZE_TEMP+=ITEM_SIZE_OFFSET;
                        adapter.setCount(DEFAULT_ITEM_SIZE + ITEM_SIZE_TEMP);
                    }
                    adapter.notifyDataSetChanged();
                    list.onFinishLoading(true, false);
                    Log.e("SIZE!!!!!!!",al.size()+"");
                }
            }
        }
    };
    class setListThread extends Thread {
        // http://blog.twtnn.com/2014/10/android-4x-http-thread-handletextview.html
        int handler;

        setListThread(int handler_type) {
            handler = handler_type;
        }

        @Override
        public void run() {

            super.run();

            al = new ArrayList<Dropbox_Item>();
            if (Dropbox.DBApi.getSession().isLinked()) { // isLinked
                try {
                    DropboxAPI.Entry dirent = Dropbox.DBApi.metadata(mPath, 0, null, true, null); // init path
                    for (DropboxAPI.Entry ent : dirent.contents) {
                        if (ent.isDir) { // Folder
                            String iP = mPath;
                            String iN = ent.fileName();
                            String iT = ent.clientMtime;
                            boolean iF = true;
                            al.add(new Dropbox_Item(iN, iP, iT, iF, 0));
                        }
                    }
                    for (DropboxAPI.Entry ent : dirent.contents) {
                        if (!ent.isDir) { //File
                            String iP = mPath;
                            String iN = ent.fileName();
                            String iT = ent.clientMtime;
                            boolean iF = false;
                            if (ent.mimeType.contains("image")) {
                                al.add(new Dropbox_Item(iN, iP, iT, iF, 1));
                            } else
                                al.add(new Dropbox_Item(iN, iP, iT, iF, 2));

                        }
                    }
                } catch (Exception e) {
                }
                //以Message資料類型（不能單純以String來傳遞）傳遞資料並呼叫Handler（就是上面自訂的mHandler函數來執行更改ui的值）
                mHandler.sendEmptyMessage(1);

            }

        }
    }

    private void getFocus() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if(!mPath.equals("/")){
                        mPath=mPath.substring(0,mPath.length()-1);
                        while(mPath.charAt(mPath.length()-1)!='/'){
                            mPath=mPath.substring(0,mPath.length()-1);
                        }
                        runSetListThread();
                    }else {
                        return false;
                    }
                    return true;
                }
                return false;
            }
        });
    }
    public void onResume() {
        super.onResume();
        getFocus();
    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            Log.e("Click","Too fast");
            return true;
        }
        lastClickTime = time;
        return false;
    }
    public void runSetListThread(){
        thread = new setListThread(1);
        thread.start();
    }
}
