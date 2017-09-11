package com.example.lab530.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;



import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.File;


import java.io.IOException;

/**
 * Created by lab530 on 2016/7/19.
 */
public class FAB_Activity extends Activity {
    static final int 	RESULT_STORE_FILE = 4;
    static final int 	REQUEST_AUTHORIZATION = 2;
    static final int PICK_PICTURE =22; // Dropbox upload
    private static Uri mFileUri;
    String dropboxUploadPath;
    String googlefolderpath;
    LinearLayout all;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fab_activity);

        //介面物件
        processViews();

        //dialog 畫面控制
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.2);   //高度设置为屏幕的0.3
        p.width = (int) (d.getWidth() * 1.0);    //宽度设置为屏幕的1.0
        p.dimAmount = 0.1f;      //设置黑暗度 dailog以外畫面

        getWindow().setAttributes(p);     //设置生效
        getWindow().setGravity(Gravity.BOTTOM);       //设置靠下对齐

    }

    private void processViews() {
        all = (LinearLayout) findViewById (R.id.all);
    }

    // 建立資料夾 上傳檔案 選擇 排序選擇  relativelayout onclick 觸碰事件
    public void function(View view){

        //建立資料夾
        if( view.getId() == R.id.folder){
            Log.e("OOOOOOO",Mix_Fragment.tmp_doing+"");
            Bundle bundle = new Bundle();
            Intent intent = new Intent();
            intent.setClass(FAB_Activity.this, Item_Add_Activity.class);
            Log.e("DDOOOO-->>",Mix_Fragment.tmp_doing+"");
            if(Mix_Fragment.tmp_doing==2){
                intent.putExtra("path",Mix_Fragment.mPath);
                intent.putExtra("AddFolderCloud","1");
                Log.e("WHAT",intent.getStringExtra("AddFolderCloud")+"");
            }else if(Mix_Fragment.tmp_doing==3){
                intent.putExtra("FolderID",Mix_Fragment.folder);
                intent.putExtra("AddFolderCloud","2");
                Log.e("WHAT",intent.getStringExtra("AddFolderCloud")+"");
            }else if(Mix_Fragment.doing==0){
                if(Main_Activity.driveCategory.equals("全部檔案")){
                    Log.e("OOOOOOO","00000000");
                    //選擇位置
                    if(Main_Activity.mList2.size()>1){
                        intent = new Intent("Add_Fold");
                        intent.setClass(this,Upload_Activity.class);

                    }
                    //一個的情況
                    else{

                    }

                }
                else if(Mix_Google_Fragment.google_doing==true){
                    intent.putExtra("FolderID",Mix_Google_Fragment.folder);
                    intent.putExtra("AddFolderCloud","2");

                }else if(Mix_Dropbox_Fragment.dropbox_doing==true){
                    intent.putExtra("path",Mix_Dropbox_Fragment.mPath);
                    intent.putExtra("AddFolderCloud","1");

                }
            }


            startActivity(intent);
            finish();
        }
        //上傳檔案
        if( view.getId() == R.id.upload){

            if(Main_Activity.mList2.size()>1&& Main_Activity.driveCategory.equals("全部檔案")&& Set_Fragment.switchboo == false&&Mix_Fragment.tmp_doing==1){
                Intent intent = new Intent("Add_File");
                intent.setClass(this,Upload_Activity.class);
                startActivity(intent);
                finish();
            }else {
                if(Mix_Fragment.tmp_doing==1 && Main_Activity.driveCategory.equals("全部檔案")){  //判斷空間上傳

                    Thread t = new Thread(new Runnable()
                    {
                        @Override
                        public void run() {
                            long GoogleBytes=0;
                            long DropboxBytes=0;
                            try {
                                About about = Main_Activity.mService.about().get().execute();
                                GoogleBytes=about.getQuotaBytesTotal();
                                DropboxBytes=Dropbox.DBApi.accountInfo().quota-Dropbox.DBApi.accountInfo().quotaNormal;
                                if(DropboxBytes>GoogleBytes){ //Dropbox 大
                                    dropboxUploadPath="/";
                                    Intent intent = new Intent( Intent.ACTION_PICK );
                                    intent.setType( "*/*" );
                                    //Intent destIntent = Intent.createChooser( intent, "Choose a file." );
                                    startActivityForResult( intent, PICK_PICTURE);
                                }
                                else{
                                    final Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                                    galleryIntent.setType("*/*");
                                    startActivityForResult(galleryIntent, RESULT_STORE_FILE);
                                    googlefolderpath="root";
                                }
                            }catch (Exception e){
                                showToast(e.toString());
                            }


                        }
                    });
                    t.start();



                }
                else if(Mix_Fragment.tmp_doing==2 &&Main_Activity.driveCategory.equals("全部檔案")){

                    dropboxUploadPath=Mix_Fragment.mPath;
                    Intent intent = new Intent( Intent.ACTION_PICK );
                    intent.setType( "*/*" );
                    //Intent destIntent = Intent.createChooser( intent, "選擇檔案" );
                    startActivityForResult( intent, PICK_PICTURE);
                }else if(Mix_Fragment.tmp_doing==3 && Main_Activity.driveCategory.equals("全部檔案")){
                    final Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                    galleryIntent.setType("*/*");
                    startActivityForResult(galleryIntent, RESULT_STORE_FILE);
                    googlefolderpath=Mix_Fragment.folder;
                }else if(Mix_Fragment.doing==0){
                    if(Mix_Google_Fragment.google_doing==true){
                        final Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                        galleryIntent.setType("*/*");
                        startActivityForResult(galleryIntent, RESULT_STORE_FILE);
                        googlefolderpath=Mix_Google_Fragment.folder;
                    }else if(Mix_Dropbox_Fragment.dropbox_doing==true){
                        dropboxUploadPath=Mix_Dropbox_Fragment.mPath;
                        Intent intent = new Intent( Intent.ACTION_PICK );
                        intent.setType( "*/*" );
                        //Intent destIntent = Intent.createChooser( intent, "Choose a file." );
                        startActivityForResult( intent, PICK_PICTURE);
                    }
                }



            }
        }

    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {

        switch (requestCode) {
            case PICK_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();

                    String[] proj = { MediaStore.Images.Media.DATA };

                    Cursor actualimagecursor = managedQuery(uri,proj,null,null,null);

                    int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                    actualimagecursor.moveToFirst();

                    String img_path = actualimagecursor.getString(actual_image_column_index);
                    Log.e("iiiii",img_path);
                    java.io.File file = new java.io.File(img_path);


                    if (uri != null) {
                        Dropbox.Upload a =new Dropbox.Upload(this,dropboxUploadPath,file);
                        a.execute();
                    }
                    finish();
                }
                break;
            case RESULT_STORE_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    mFileUri = data.getData();

                    // Save the file to Google Drive
                    if (mFileUri != null) {
                        mHandler.sendEmptyMessage(1);
                        saveFileToDrive();
                    }

                    finish();
                }
                break;
        }

    }
    private void saveFileToDrive()
    {

        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    // Create URI from real path
                    String path;
                    path = getPathFromUri(mFileUri);
                    mFileUri = Uri.fromFile(new java.io.File(path));


                    ContentResolver cR = FAB_Activity.this.getContentResolver();

                    // File's binary content
                    java.io.File fileContent = new java.io.File(mFileUri.getPath());
                    FileContent mediaContent = new FileContent(cR.getType(mFileUri), fileContent);

                   // showToast("Selected " + mFileUri.getPath() + "to upload");

                    // File's meta data.
                    File body = new File();
                    body.setTitle(fileContent.getName());
                    body.setMimeType(cR.getType(mFileUri));

                    Drive.Files f1 = Main_Activity.mService.files();
                    Drive.Files.Insert i1 = f1.insert(body, mediaContent);

                    MediaHttpUploader uploader = i1.getMediaHttpUploader();
                    uploader.setDirectUploadEnabled(false);
                   // uploader.setChunkSize(10*1024*1024);
                    uploader.setProgressListener(new FileUploadProgressListener());
                    File file = i1.execute();

                    Main_Activity.mService.files().update(file.getId(), null)
                            .setAddParents(googlefolderpath)
                            .setRemoveParents("root")
                            .setFields("id, parents")
                            .execute();

                    mHandler.sendEmptyMessage(2);
                } catch (UserRecoverableAuthIOException e) {
                    startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                } catch (IOException e) {
                    e.printStackTrace();
                    showToast("Transfer ERROR: " + e.toString());
                }
            }
        });
        t.start();


    }
    public String getPathFromUri(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);//google
            if(msg.what==1){
                showToast("開始上傳");
            }
            else if(msg.what==2){
                if(Mix_Google_Fragment.google_doing==true){
                    Mix_Google_Fragment mix_google_fragment = new Mix_Google_Fragment();
                    mix_google_fragment.getDriveContents(Mix_Google_Fragment.folder);
                }else if(Mix_Dropbox_Fragment.dropbox_doing==true) {
                    Main_Activity.dropboxFragment.runSetListThread();
                }

                showToast("完成上傳");
            }
        }
    };
}
 class FileUploadProgressListener implements MediaHttpUploaderProgressListener {

    @Override
    public void progressChanged(MediaHttpUploader mediaHttpUploader) throws IOException {
        if (mediaHttpUploader == null) return;
        switch (mediaHttpUploader.getUploadState()) {
            case INITIATION_STARTED:
                //System.out.println("Initiation has started!");
                break;
            case INITIATION_COMPLETE:
                //System.out.println("Initiation is complete!");
                break;
            case MEDIA_IN_PROGRESS:
                double percent = mediaHttpUploader.getProgress() * 100;
                    Log.e("", "Upload to Google:  " + String.valueOf(percent) + "%");
                break;
            case MEDIA_COMPLETE:
                //System.out.println("Upload is complete!");
        }
    }


}