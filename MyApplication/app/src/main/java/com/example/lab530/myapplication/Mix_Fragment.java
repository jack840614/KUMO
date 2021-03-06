package com.example.lab530.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by user on 2016/3/20.
 */
public class Mix_Fragment extends Fragment implements Serializable {

    View v;

    //public ArrayList<Item> data;
    boolean arrangestatus;
    static List<File> mResultList;
    static final int REQUEST_AUTHORIZATION = 2;
    static ArrayList<Mix_Item> al = new ArrayList<Mix_Item>();
    static PtrrvBaseAdapter adapter;
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
    static final int ALL_DROPBOX_REFRESH = 4;
    static final int ALL_GOOGLE_REFRESH = 5;
    static final int TIME = 1000;
    static final int DEFAULT_ITEM_SIZE = 10;
    static final int ITEM_SIZE_OFFSET = 10;
    static int ITEM_SIZE_TEMP = 0;
    int lastALsize =0;
    //static boolean doing =false;
    static int doing =0; // 0 = 其他Fragment , 1 = 全部檔案 , 2 = 全部檔案的Dropbox , 3 = 全部檔案的GoogleDrive
    static int tmp_doing=1;
    static Activity context;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //畫面宣告
        v = inflater.inflate(R.layout.activity_listview, container, false);

        context = getActivity();
        //排序狀態
        arrangestatus = getArguments().getBoolean("status");

        //介面物件
        processViews();

        //物件操作
        processControllers();


        //更新清單
        initList();
        /*
        runSetListThread();

        folder_level = new ArrayList<String>();
        folder = "root";
        if(folder_level.isEmpty())
            folder_level.add(folder);

        getDriveContents(folder);
        */
        //sortList(); // 預設排序
        /*
        //依照名稱排序
        if (arrangestatus) {
            Collections.sort(al,
                    new Comparator<Mix_Item>() {
                        public int compare(Mix_Item o1, Mix_Item o2) {
                            return o1.itemName.compareTo(o2.itemName);
                        }
                    });
        }
        //依照時間排序
        else {
            Collections.sort(al,
                    new Comparator<Mix_Item>() {
                        public int compare(Mix_Item o1, Mix_Item o2) {
                            return String.valueOf(o1.itemTime).compareTo(String.valueOf(o2.itemTime));
                        }
                    });
        }
        */
        /*
        //創建連接器 顯示list
        Item_Adapter adapter;
        adapter = new Item_Adapter(getActivity(), R.layout.item_adapter, Main_Activity.datalist);
        Main_Activity.item_list.setAdapter(adapter);
*/
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
                Log.i("onDrawLoadMore", "draw load more");
                return false;
            }
        });



        //adapter  = new Dropbox_Adapter(getActivity(),al); // init adapter
        adapter = new PtrrvBaseAdapter(getActivity(), al); // init adapter
        adapter.changeArrayList(al);
        list.setAdapter(adapter);
        list.onFinishLoading(true, false);
        //Main_Activity.item_list.setAdapter(adapter); // set adapter
        //new Thread(new autoRefresh()).start();
        Log.e("thread","START");



        return v;
    }

    private void processViews() {
        // Main_Activity.item_list = (ListView) v.findViewById(R.id.item_list);
        list = (PullToRefreshRecyclerView) v.findViewById(R.id.ptrrv);
    }

    private void processControllers() {
/*
        //清單觸碰事件
        Main_Activity.item_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //showToast("!!!");
            }

        });
*/
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
                if(doing==1){
                    initList();// 只刷最外層
                }
                else if (doing == 2) {
                            mHandler.sendEmptyMessage(ALL_DROPBOX_REFRESH);
                } else if (doing == 3){
                            al = new ArrayList<Mix_Item>();
                            mHandler.sendEmptyMessage(ALL_GOOGLE_REFRESH);
                }
                if(al.size()>=0){
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
                    Toast.makeText(Main_Activity.context, R.string.nomoredata, Toast.LENGTH_SHORT).show();
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
            }else if(msg.what==ALL_DROPBOX_REFRESH){

                runSetListThread();
                adapter.setCount(al.size() >= 10 ? DEFAULT_ITEM_SIZE : al.size());
                adapter.changeArrayList(al);
                adapter.notifyDataSetChanged();
                list.onFinishLoading(true, false);
                ITEM_SIZE_TEMP=0;
            }
            else if(msg.what==ALL_GOOGLE_REFRESH){
                getDriveContents(folder);
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

        context.runOnUiThread(new Runnable() {
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

    private void getFocus() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if(choice==1) {
                        if (!mPath.equals("/")) {
                            if(mPath.split("/").length==2){
                                initList();
                            }
                            else{
                                mPath = mPath.substring(0, mPath.length() - 1);
                                while (mPath.charAt(mPath.length() - 1) != '/') {
                                    mPath = mPath.substring(0, mPath.length() - 1);
                                }
                                runSetListThread();
                                adapter.setCount(al.size() >= 10 ? DEFAULT_ITEM_SIZE : al.size());
                                adapter.changeArrayList(al);
                                adapter.notifyDataSetChanged();
                                list.onFinishLoading(true, false);
                                ITEM_SIZE_TEMP=0;
                            }

                        } else {
                            return false;
                        }
                        return true;
                    }else if(choice==2){

                        if(folder_level.size()>1) {
                            al = new ArrayList<Mix_Item>();
                            PtrrvBaseAdapter.googlenowfolder= folder_level.remove(folder_level.size() - 1);
                            folder=PtrrvBaseAdapter.googlenowfolder;
                            if(folder_level.size()==1) {
                                doing=1;
                                folder="root";
                                PtrrvBaseAdapter.googlenowfolder=folder;
                                initList();
                            }else
                                getDriveContents(folder_level.get(folder_level.size() - 1));

                            Thread t = new Thread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    try {
                                        File file = Main_Activity.mService.files().get(PtrrvBaseAdapter.googlenowfolder).execute();
                                        PtrrvBaseAdapter.googlenowfoldername=file.getTitle();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            t.start();

                            adapter.setCount(al.size() >= 10 ? DEFAULT_ITEM_SIZE : al.size());
                                adapter.changeArrayList(al);
                                adapter.notifyDataSetChanged();
                                list.onFinishLoading(true, false);
                                ITEM_SIZE_TEMP=0;


                        }
                        else {
                            return false;
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void onResume() {
        super.onResume();
        getFocus();
        doing = tmp_doing;
        Mix_Dropbox_Fragment.dropbox_doing = false;
        Mix_Google_Fragment.google_doing = false;

        //initList(); // test
        if (doing == 2) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                    }
                    mHandler.sendEmptyMessage(ALL_DROPBOX_REFRESH);
                }
            }).start();
        } else if (doing == 3){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                    }
                    al = new ArrayList<Mix_Item>();
                    mHandler.sendEmptyMessage(ALL_GOOGLE_REFRESH);
                }
            }).start();
        }
        else if(doing==1){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                    }
                    mHandler.sendEmptyMessage(MSG_CODE_REFRESH);
                }
            }).start();
        }

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

    public void initList(){

        doing=1;
        //更新清單
        mPath = "/";

        folder_level = new ArrayList<String>();
        folder = "root";
        if(folder_level.isEmpty())
            folder_level.add(folder);

        runSetListThread();
        if(Main_Activity.GoolgleIsConnect)
            getDriveContents(folder);
        /*
        adapter.setCount(al.size() >= 10 ? DEFAULT_ITEM_SIZE : al.size());
        adapter.notifyDataSetChanged();
        list.setOnRefreshComplete();
        list.onFinishLoading(true, false);
        ITEM_SIZE_TEMP = 0;
        */
    }
}
