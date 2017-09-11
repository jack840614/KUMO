package com.example.lab530.myapplication;

import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.model.File;
import com.lhh.ptrrv.library.PullToRefreshRecyclerView;
import com.lhh.ptrrv.library.footer.loadmore.BaseLoadMoreView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by lab530 on 2016/9/9.
 */
public class Image_Activity extends ActionBarActivity {

    private boolean arrangestatus;
    public static List<File> mResultList;
    private List<File> 	     googleFolderList;
    ArrayList<String> dropboxFolderList;
    static final int REQUEST_AUTHORIZATION = 2;
    static ArrayList<Mix_Item> al = new ArrayList<Mix_Item>();
    //static GDAdapter adapter;
    static Image_Adapter adapter;

    private String 					mDLVal;

    static long lastClickTime;
    static boolean oneboo=false;  //一次開關
    static boolean complete=false; //載完檔案
    static PullToRefreshRecyclerView list;
    public static final int MSG_CODE_REFRESH = 2;
    public static final int MSG_CODE_LOADMORE = 3;
    public static final int TIME = 1000;
    public static final int DEFAULT_ITEM_SIZE = 10;
    public static final int ITEM_SIZE_OFFSET = 5;
    public static  int ITEM_SIZE_TEMP = 0;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        setTitle("圖片");
        oneboo=true;

        //介面物件
        processViews();

        if(Main_Activity.GoolgleIsConnect==true&&Dropbox.DBApi.getSession().isLinked()==false)
            googleSearchAllfiles();

        else if(Main_Activity.GoolgleIsConnect==false&&Dropbox.DBApi.getSession().isLinked()==true)
            dropboxSearchAllFiles();

        else if(Main_Activity.GoolgleIsConnect==true&&Dropbox.DBApi.getSession().isLinked()==true) {
            dropboxSearchAllFiles();
            googleSearchAllfiles();
        }
        list.setSwipeEnable(true);//open swipe
        DemoLoadMoreView loadMoreView = new DemoLoadMoreView(Image_Activity.this, list.getRecyclerView());
        loadMoreView.setLoadmoreString(getString(R.string.demo_loadmore));
        loadMoreView.setLoadMorePadding(100);
        list.setLayoutManager(new GridLayoutManager(Image_Activity.this, 2));
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
        list.getRecyclerView().addItemDecoration(new DividerItemDecoration(Image_Activity.this,
                DividerItemDecoration.VERTICAL_LIST));


        list.setLoadMoreFooter(loadMoreView);
        list.getLoadMoreFooter().setOnDrawListener(new BaseLoadMoreView.OnDrawListener() {
            @Override
            public boolean onDrawLoadMore(Canvas c, RecyclerView parent) {
                Log.i("onDrawLoadMore","draw load more");
                return false;
            }
        });

        if(adapter==null) {
            adapter = new Image_Adapter(Image_Activity.this, al); // init adapter
            adapter.changeArrayList(al);
        }else {
            mHandler.sendEmptyMessage(1);
        }
        list.setAdapter(adapter);
        list.onFinishLoading(true, false);

        // Enabling Up navigation 這段很重要~
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void processViews() {
        list = (PullToRefreshRecyclerView) findViewById(R.id.ptrrv);
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

    private void googleSearchAllfiles(){
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                mResultList = new ArrayList<File>();
                al = new ArrayList<Mix_Item>();
                com.google.api.services.drive.Drive.Files f1 = Main_Activity.mService.files();
                com.google.api.services.drive.Drive.Files.List request = null;
                com.google.api.services.drive.model.FileList fileList = null;
                do
                {
                    try
                    {

                        final Queue<String> qq=new LinkedList<>();
                        qq.add("root");


                        //request.setQ("trashed=false");
                        do{
                            googleFolderList = new ArrayList<File>();
                            request = f1.list();
                            request.setQ("'" + qq.poll() + "' in parents and trashed=false");

                            fileList = request.execute();
                            //mResultList.addAll(fileList.getItems());
                            googleFolderList.addAll(fileList.getItems());

                            for(File tmp : googleFolderList) {

                                if(tmp.getMimeType().equals("application/vnd.google-apps.folder")) {
                                    Log.e("DDDDDDDDDDDDDDDDD",tmp.getTitle()+"");
                                    qq.add(tmp.getId());
                                }
                                else  if(tmp.getMimeType().equals("image/jpeg")||tmp.getMimeType().equals("image/png")||tmp.getMimeType().equals("image/gif")||tmp.getMimeType().equals("image/bmp")){
                                    String iP=tmp.getThumbnailLink();
                                    String iN=tmp.getTitle();
                                    String ID=tmp.getId();
                                    String iT=tmp.getCreatedDate().toString();
                                    boolean iF=false;
                                    long iS=tmp.getFileSize();
                                    al.add(new Mix_Item(2,iN,iP,ID,iT,iF,1,iS,""));
                                    mHandler.sendEmptyMessage(1);

                                    //       mResultList.add(tmp);
                                }

                            }

                            request.setPageToken(fileList.getNextPageToken());

                        }while(!qq.isEmpty());


                    } catch (UserRecoverableAuthIOException e) {
                        startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (request != null)
                        {
                            request.setPageToken(null);
                        }
                    }
                } while (request.getPageToken() !=null && request.getPageToken().length() > 0);


                complete=true;
                //populateListView();
            }
        });
        t.start();

    }

    private void dropboxSearchAllFiles(){
        new Thread(new Runnable()
        {
            public void run()
            {
                try {
                    Queue<DropboxAPI.Entry> dqq = new LinkedList<DropboxAPI.Entry>();
                    Log.e("ERROR","QAQ!");
                    DropboxAPI.Entry init = Dropbox.DBApi.metadata("/", 0, null, true, null);
                    Log.e("ERROR","QAQ!!");
                    dqq.add(init);
                    Log.e("ERROR","QAQ!!!");
                    do{
                        DropboxAPI.Entry nowPath=dqq.poll();
                        Log.e("ERRORsize",dqq.size()+"");

                        if(nowPath!=null) {
                            Log.e("ERRORpath",nowPath.path);
                            for (DropboxAPI.Entry entry : nowPath.contents) {
                                if (entry.isDir) {
                                    Log.e("PATH", entry.path);
                                    DropboxAPI.Entry addEntry = Dropbox.DBApi.metadata(entry.path + "/", 0, null, true, null);
                                    dqq.add(addEntry);
                                } else if (entry.mimeType.contains("image")) {
                                    Log.e("IMAGE", entry.fileName());
                                    String iP = entry.parentPath();
                                    String iN = entry.fileName();
                                    String iT = entry.clientMtime;
                                    boolean iF = false;
                                    long iS=entry.bytes;
                                    al.add(new Mix_Item(1, iN, iP, "", iT, iF, 1,iS,""));
                                    mHandler.sendEmptyMessage(1);
                                }
                            }
                        }
                        else{
                            Log.e("ERROR","Q口Q!!");
                        }
                    }while(dqq.size()>0);
                    complete=true;


                } catch (DropboxException e) {
                    Log.e("ERROR","QAQ");
                }
            }
        }).start();
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

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1&&al.size()>=10){
                if(oneboo) {
                    oneboo=false;

                    adapter.setCount(al.size()>=10?DEFAULT_ITEM_SIZE:al.size());
                    adapter.changeArrayList(al);
                    adapter.notifyDataSetChanged();
                    list.setOnRefreshComplete();
                    list.onFinishLoading(true, false);
                    ITEM_SIZE_TEMP=0;
                }

                Log.e("List","!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            else if (msg.what == MSG_CODE_REFRESH) {
                if(al.size()>0){
                    adapter.setCount(al.size()>=10?DEFAULT_ITEM_SIZE:al.size());
                    adapter.notifyDataSetChanged();
                    list.setOnRefreshComplete();
                    list.onFinishLoading(true, false);
                    ITEM_SIZE_TEMP=0;
                    Log.e("SIZE!!!",al.size()+"");
                }
            }   else if (msg.what == MSG_CODE_LOADMORE) {
                Log.e("SiZE???",al.size()+"");
                if(adapter.getItemCount() == al.size()){
                    //over
                    //   Toast.makeText(getActivity(), R.string.nomoredata, Toast.LENGTH_SHORT).show();
                    if(complete) {
                        Toast.makeText(Image_Activity.this, R.string.nomoredata, Toast.LENGTH_SHORT).show();
                        list.onFinishLoading(false, false);
                    }else
                        list.onFinishLoading(true, false);
                    Log.e("SIZE!!!!!",al.size()+"");
                }else {
                    if(adapter.getItemCount()+ITEM_SIZE_OFFSET>=al.size()){
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

}
