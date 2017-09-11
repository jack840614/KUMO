package com.example.lab530.myapplication;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.model.File;
import com.lhh.ptrrv.library.PullToRefreshRecyclerView;
import com.lhh.ptrrv.library.footer.loadmore.BaseLoadMoreView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.support.v7.widget.RecyclerView.*;

/**
 * Created by lab530 on 2016/7/25.
 */
public class Touch_Move_Activity extends ActionBarActivity {

    Button move,off;
    static int index = 0;

    static boolean arrangestatus;
    static List<File> mResultList;
    static final int REQUEST_AUTHORIZATION = 2;
    static ArrayList<Mix_Item> al = new ArrayList<Mix_Item>();
    static Select_Adapter adapter;
    static String mPath = "/"; // Dropbox default path
    static long lastClickTime;
    static setListThread thread;
    static String folder="root";
    private String 					mDLVal;
    static  ArrayList<String>	folder_level ;
    public static int  choice;

    static PullToRefreshRecyclerView list;
    static final int MSG_CODE_REFRESH = 2;
    static final int MSG_CODE_LOADMORE = 3;
    static final int TIME = 1000;
    static final int DEFAULT_ITEM_SIZE = 10;
    static final int ITEM_SIZE_OFFSET = 10;
    static int ITEM_SIZE_TEMP = 0;
    int lastALsize =0;
    //static boolean doing =false;
    static int doing =0; // 0 = 其他Fragment , 1 = 全部檔案 , 2 = 全部檔案的Dropbox , 3 = 全部檔案的GoogleDrive
    int tmp_doing=1;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.touch_move_activity);
        setTitle("選擇項目");

        Main_Activity.status = "移動";

        //介面物件
        processViews();
        //物件操作
        processControllers();

        //更新清單
        initList();

        list.setSwipeEnable(true);//open swipe

        DemoLoadMoreView loadMoreView = new DemoLoadMoreView(this, list.getRecyclerView());
        loadMoreView.setLoadmoreString(getString(R.string.demo_loadmore));
        loadMoreView.setLoadMorePadding(100);
        list.setLayoutManager(new LinearLayoutManager(this));
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
        list.getRecyclerView().addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));


        list.setLoadMoreFooter(loadMoreView);
        list.getLoadMoreFooter().setOnDrawListener(new BaseLoadMoreView.OnDrawListener() {
            @Override
            public boolean onDrawLoadMore(Canvas c, RecyclerView parent) {
                Log.i("onDrawLoadMore", "draw load more");
                return false;
            }
        });


        //adapter  = new Dropbox_Adapter(getActivity(),al); // init adapter
        adapter = new Select_Adapter(this, al); // init adapter
        adapter.changeArrayList(al);
        list.setAdapter(adapter);
        list.onFinishLoading(true, false);
        //Main_Activity.item_list.setAdapter(adapter); // set adapter
        //new Thread(new autoRefresh()).start();
        Log.e("thread","START");

        // Enabling Up navigation 這段很重要~
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_null, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



    private void processViews() {

        list = (PullToRefreshRecyclerView) findViewById(R.id.ptrrv);
        move = (Button) findViewById(R.id.move);
        off = (Button) findViewById(R.id.off);
    }

    private void processControllers() {

    }


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {

                Log.e("QAQ ","Network!!!");
                if(Main_Activity.connManager.getActiveNetworkInfo().isAvailable()){
                    Log.e("We have ","Network!!!");
                    Log.e("We use ",Main_Activity.info.getState()+"");
                }
                else {
                    Log.e("We haven'/t ","Newwork!!!");
                    return;
                }

                sortList();
                adapter.setCount(al.size() >= 10 ? DEFAULT_ITEM_SIZE : al.size());
                adapter.changeArrayList(al);
                adapter.notifyDataSetChanged();
                list.setOnRefreshComplete();
                list.onFinishLoading(true, false);
                ITEM_SIZE_TEMP=0;
                Log.e("List", "!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            } else if (msg.what == MSG_CODE_REFRESH) {
                initList();// 只刷最外層
                if(al.size()>0){
                    adapter.setCount(al.size() >= 10 ? DEFAULT_ITEM_SIZE : al.size());
                    adapter.notifyDataSetChanged();
                    list.setOnRefreshComplete();
                    list.onFinishLoading(true, false);
                    ITEM_SIZE_TEMP = 0;
                    Log.e("SIZE!!!", al.size() + "");
                }
            } else if (msg.what == MSG_CODE_LOADMORE) {
                Log.e("SiZE???", al.size() + "");
                if (adapter.getItemCount() == al.size()) {
                    //over
                    Toast.makeText(Touch_Move_Activity.this, R.string.nomoredata, Toast.LENGTH_SHORT).show();
                    list.onFinishLoading(false, false);
                    Log.e("SIZE!!!!!", al.size() + "");
                } else {
                    if (adapter.getItemCount() + ITEM_SIZE_OFFSET >= al.size()) {
                        adapter.setCount(al.size());
                    } else {
                        ITEM_SIZE_TEMP += ITEM_SIZE_OFFSET;
                        adapter.setCount(DEFAULT_ITEM_SIZE + ITEM_SIZE_TEMP);
                    }
                    adapter.notifyDataSetChanged();
                    list.onFinishLoading(true, false);
                    Log.e("SIZE!!!!!!!", al.size() + "");
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

            al = new ArrayList<Mix_Item>();
            if (Dropbox.DBApi.getSession().isLinked()) { // isLinked
                try {
                    DropboxAPI.Entry dirent = Dropbox.DBApi.metadata(mPath, 0, null, true, null); // init path
                    for (DropboxAPI.Entry ent : dirent.contents) {
                        if (ent.isDir) { // Folder
                            String iP = mPath;
                            String iN = ent.fileName();
                            String iT = ent.clientMtime;
                            boolean iF = true;
                            long iS=ent.bytes;
                            al.add(new Mix_Item(1, iN, iP, "", iT, iF, 0,iS,""));
                        }
                    }
                    for (DropboxAPI.Entry ent : dirent.contents) {
                        if (!ent.isDir) { //File
                            String iP = mPath;
                            String iN = ent.fileName();
                            String iT = ent.clientMtime;
                            boolean iF = false;
                            long iS=ent.bytes;
                            int type=11;
                            if (ent.mimeType.contains("image"))
                                type=1;
                            else if(ent.mimeType.contains("audio"))
                                type=2;
                            else if(ent.mimeType.contains("video"))
                                type=3;
                            else if(ent.mimeType.contains("vnd.openxmlformats-officedocument.wordprocessingml.document"))
                                type=4;
                            else if(ent.mimeType.contains("pdf"))
                                type=5;
                            else if(ent.mimeType.contains("vnd.openxmlformats-officedocument.presentationml.presentation")||ent.mimeType.contains("vnd.ms-powerpoint"))
                                type=6;
                            else if(ent.mimeType.contains("plain"))
                                type=7;
                            else if(ent.mimeType.contains("vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                                type=8;
                            else if(ent.mimeType.contains("html"))
                                type=9;
                            else if(ent.mimeType.contains("zip")||ent.mimeType.contains("rar"))
                                type=10;
                            else
                                type=11;
                            al.add(new Mix_Item(1,iN, iP, "",iT, iF, type,iS,""));

                        }
                    }
                } catch (Exception e) {
                }
                //以Message資料類型（不能單純以String來傳遞）傳遞資料並呼叫Handler（就是上面自訂的mHandler函數來執行更改ui的值）
                mHandler.sendEmptyMessage(1);

            }

        }
    }



    private void getDriveContents(final String str) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                mResultList = new ArrayList<File>();
                com.google.api.services.drive.Drive.Files f1 = Main_Activity.mService.files();
                com.google.api.services.drive.Drive.Files.List request = null;

                //Log.e("!!",);
                do {
                    try {
                        request = f1.list();
                        //request.setQ("trashed=false");
                        request.setQ("'" + str + "' in parents and trashed=false");

                        com.google.api.services.drive.model.FileList fileList = request.execute();
                        mResultList.addAll(fileList.getItems()); // !!

                        for (int i = 0; i < mResultList.size(); i++) {
                            if (mResultList.get(i).getMimeType().equals("application/vnd.google-apps.folder")) {
                                mResultList.add(0, mResultList.get(i));
                                mResultList.remove(i + 1);
                            }
                        }
                        request.setPageToken(fileList.getNextPageToken());
                    } catch (UserRecoverableAuthIOException e) {
                        startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
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

    private void populateListView() {

        Touch_Move_Activity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                for (File tmp : mResultList) {
                    if (tmp.getMimeType().equals("application/vnd.google-apps.folder")) {
                        String iP = tmp.getThumbnailLink();
                        String iN = tmp.getTitle();
                        String ID = tmp.getId();
                        String iT = tmp.getCreatedDate().toString();
                        boolean iF = true;
                        al.add(new Mix_Item(2, iN, iP, ID, iT, iF, 0,0,""));
                    } else {
                        String iP = tmp.getThumbnailLink();
                        String iN = tmp.getTitle();
                        String ID = tmp.getId();
                        String iT = tmp.getCreatedDate().toString();
                        int type=11;
                        boolean iF = false;
                        long iS=0;
                        String downloadURL=null;
                        if(tmp.getMimeType().equals("image/jpeg")||tmp.getMimeType().equals("image/png")||tmp.getMimeType().equals("image/gif")||tmp.getMimeType().equals("image/bmp"))
                            type=1;
                        else if(tmp.getMimeType().equals("audio/mpeg"))
                            type=2;
                        else if(tmp.getMimeType().equals("video/mp4"))
                            type=3;
                        else if(tmp.getMimeType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                            type=4;
                        else if(tmp.getMimeType().equals("application/pdf"))
                            type=5;
                        else if(tmp.getMimeType().equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")||tmp.getMimeType().equals("application/vnd.ms-powerpoint"))
                            type=6;
                        else if(tmp.getMimeType().equals("text/plain"))
                            type=7;
                        else if(tmp.getMimeType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                            type=8;
                        else if(tmp.getMimeType().equals("text/html"))
                            type=9;
                        else if(tmp.getMimeType().equals("application/zip")||tmp.getMimeType().equals("application/rar"))
                            type=10;
                        else if(tmp.getMimeType().equals("application/vnd.google-apps.document")||tmp.getMimeType().equals("application/vnd.google-apps.spreadsheet")||tmp.getMimeType().equals("application/vnd.google-apps.form"))
                            type=12;
                        else
                            type=11;
                        if(type!=12) {
                            iS = tmp.getFileSize();
                            downloadURL=tmp.getDownloadUrl();
                        }
                        al.add(new Mix_Item(2,iN,iP,ID,iT,iF,type,iS,downloadURL));
                    }

                }
                /*
                adapter = new  PtrrvBaseAdapter(getActivity(),al); // init adapter
                adapter.changeArrayList(al);
                list.setAdapter(adapter);
                */
                mHandler.sendEmptyMessage(1);

            }
        });
    }

    public void onResume() {
        super.onResume();
        doing=tmp_doing;
        Log.e("DDDDOOOO",tmp_doing+"");
    }

    @Override
    public void onPause() {
        super.onPause();
        tmp_doing=doing;
        doing=0;
        Log.e("thread","INTERRUPT");
    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            Log.e("Click", "Too fast");
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public void runSetListThread() {
        thread = new setListThread(1);
        thread.start();
    }

    public void sortList() {
        if (arrangestatus) { // 依名稱
            Collections.sort(al,
                    new Comparator<Mix_Item>() {
                        public int compare(Mix_Item o1, Mix_Item o2) {
                            return o1.itemName.compareTo(o2.itemName);
                        }
                    });
        } else { // 依時間
            Collections.sort(al,
                    new Comparator<Mix_Item>() {
                        public int compare(Mix_Item o1, Mix_Item o2) {
                            return String.valueOf(o1.itemTime).compareTo(String.valueOf(o2.itemTime));
                        }
                    });
        }
        // 資料夾優先
        Collections.sort(al, new Comparator<Mix_Item>() {
            public int compare(Mix_Item o1, Mix_Item o2) {
                return o1.itemMime - o2.itemMime;
            }
        });
        adapter.changeArrayList(al);
        adapter.notifyDataSetChanged();
    }

    public  void initList(){

        doing=1;
        //更新清單
        mPath = "/";

        folder_level = new ArrayList<String>();
        folder = "root";
        if(folder_level.isEmpty())
            folder_level.add(folder);

        runSetListThread();
        //getDriveContents(folder);
        /*
        adapter.setCount(al.size() >= 10 ? DEFAULT_ITEM_SIZE : al.size());
        adapter.notifyDataSetChanged();
        list.setOnRefreshComplete();
        list.onFinishLoading(true, false);
        ITEM_SIZE_TEMP = 0;
        */
    }


}
