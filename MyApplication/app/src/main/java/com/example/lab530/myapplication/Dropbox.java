package com.example.lab530.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 *    Main : 登入、登出+清除偏好值、儲存+載入偏好值
 *    DB   : 上傳、下載、登入狀態
 */
public class Dropbox {
    public static final String APP_KEY = "wnlkfe4kktwd57k";
    public static final String APP_SECRET = "b8yja5rpqqrhykd";
    public static final String ACCOUNT_PREFS_NAME = "prefs";
    public static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    public static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    public static DropboxAPI<AndroidAuthSession> DBApi;
    public static boolean DBLoggIn=false;
    public static String rootPath = "/";
    public static String AccountName="";


    public static class Upload extends AsyncTask<Void, Long, Boolean> {

        private String mPath;
        private File mFile;

        private long mFileLen;
        private DropboxAPI.UploadRequest mRequest;
        private Context mContext;
        private final ProgressDialog mDialog;

        private String mErrorMsg;

        public Upload(Context context, String dropboxPath, File file) { // context , upload path , upload file
            // We set the context this way so we don't accidentally leak activities
            mContext = context.getApplicationContext();

            mFileLen = file.length();
            mPath = dropboxPath;
            mFile = file;

            mDialog = new ProgressDialog(context);
            mDialog.setMax(100);
            mDialog.setMessage("Uploading " + file.getName());
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDialog.setProgress(0);
            mDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // This will cancel the putFile operation
                    mRequest.abort();
                }
            });
            //mDialog.show();
            Log.e("DB","init");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Log.e("DB","doinbackground");
                // By creating a request, we get a handle to the putFile operation,
                // so we can cancel it later if we want to
                FileInputStream fis = new FileInputStream(mFile);
                String path = mPath + mFile.getName();
                mRequest = DBApi.putFileOverwriteRequest(path, fis, mFile.length(),
                        new ProgressListener() {
                            @Override
                            public long progressInterval() {
                                // Update the progress bar every half-second or so
                                return 500;
                            }

                            @Override
                            public void onProgress(long bytes, long total) {
                                publishProgress(bytes);
                            }
                        });
                if (mRequest != null) {

                    //showToast("開始上傳");
                    mHandler.sendEmptyMessage(1);
                    Log.e("START","UPLOAD");
                    mRequest.upload();
                    return true;
                }

            } catch (DropboxUnlinkedException e) {
                // This session wasn't authenticated properly or user unlinked
                mErrorMsg = "This app wasn't authenticated properly.";
            } catch (DropboxFileSizeException e) {
                // File size too big to upload via the API
                mErrorMsg = "This file is too big to upload";
            } catch (DropboxPartialFileException e) {
                // We canceled the operation
                mErrorMsg = "Upload canceled";
            } catch (DropboxServerException e) {
                // Server-side exception.  These are examples of what could happen,
                // but we don't do anything special with them here.
                if (e.error == DropboxServerException._401_UNAUTHORIZED) {
                    // Unauthorized, so we should unlink them.  You may want to
                    // automatically log the user out in this case.
                } else if (e.error == DropboxServerException._403_FORBIDDEN) {
                    // Not allowed to access this
                } else if (e.error == DropboxServerException._404_NOT_FOUND) {
                    // path not found (or if it was the thumbnail, can't be
                    // thumbnailed)
                } else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
                    // user is over quota
                } else {
                    // Something else
                }
                // This gets the Dropbox error, translated into the user's language
                mErrorMsg = e.body.userError;
                if (mErrorMsg == null) {
                    mErrorMsg = e.body.error;
                }
            } catch (DropboxIOException e) {
                // Happens all the time, probably want to retry automatically.
                mErrorMsg = "Network error.  Try again.";
            } catch (DropboxParseException e) {
                // Probably due to Dropbox server restarting, should retry
                mErrorMsg = "Dropbox error.  Try again.";
            } catch (DropboxException e) {
                // Unknown error
                mErrorMsg = "Unknown error.  Try again.";
            } catch (FileNotFoundException e) {
            }
            Log.e("XXX","4");
            return false;
        }

        @Override
        protected void onProgressUpdate(Long... progress) {
            int percent = (int) (100.0 * (double) progress[0] / mFileLen + 0.5);
            mDialog.setProgress(percent);
        }

        @Override
        protected void onPostExecute(Boolean result) {
           // mDialog.dismiss();
            if (result) {
                mHandler.sendEmptyMessage(2);
            } else {
                showToast(mErrorMsg);
            }
        }

        private void showToast(String msg) {
            Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
            error.show();
        }

        Handler mHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==1){
                    showToast("開始上傳");
                }
                else if(msg.what==2){
                    if(Mix_Dropbox_Fragment.dropbox_doing==true) { //個別
                        Main_Activity.dropboxFragment.runSetListThread();
                    }
                    else{ //全部
                    /*
                    Mix_Fragment mix_fragment = new Mix_Fragment();
                    mix_fragment.initList();
                    mix_fragment.sortList();
                    */
                    }
                    showToast("完成上傳");
                }
            }
        };

    }

    public static class Download extends AsyncTask<Void, Void, Void> {

        private String mPath,dbPath;

        private DropboxAPI.UploadRequest mRequest;
        private Context mContext;

        private String mErrorMsg;

        public Download(Context context, String dropboxPath) { // context , file path , file name
            // We set the context this way so we don't accidentally leak activities
            mContext = context.getApplicationContext();
            dbPath = dropboxPath;
            //mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"123.jpg";
        }

        @Override
        protected Void doInBackground(Void... params) {
            FileOutputStream outputStream = null;
            File file = new java.io.File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(),"1234.jpg");
            try {
                outputStream = new FileOutputStream(file);
                DropboxAPI.DropboxFileInfo info =DBApi.getFile(dbPath, null, outputStream, null);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (DropboxException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            showToast("Image successfully downloaded");
        }

        private void showToast(String msg) {
            Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
            error.show();
        }
    }
    public static void setLoggedIn(boolean loggedIn) {
        Dropbox.DBLoggIn = loggedIn;
    }

}
