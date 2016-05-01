package com.onemdm.installer;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

public class OneMDMInstallerService extends IntentService {

    private static final String TAG = "OneMDMInstaller";
    public static final String ONEMDM_PACKAGE = "com.multunus.onemdm";
    public static final String ONEMDM_URL = "http://bit.ly/onemdmapp";


    public OneMDMInstallerService() {
        super("OneMDMInstallerService");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "AppInstallerService  started");
        if(!isAppInstalledAlready()){
            startDownload();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void startDownload() {
        final DownloadManager downloadManager = (DownloadManager)
                getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        if(!isAppDownloading(downloadManager)){
            downloadAndInstall(downloadManager);
        }
    }

    private boolean isAppDownloading(DownloadManager downloadManager) {
        boolean isDownloading = false;
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(
                DownloadManager.STATUS_PAUSED |
                        DownloadManager.STATUS_PENDING |
                        DownloadManager.STATUS_RUNNING);
        Cursor cur = downloadManager.query(query);
        int col = cur.getColumnIndex(
                DownloadManager.COLUMN_LOCAL_FILENAME);
        if(cur.moveToFirst()) {
            String downloadPath = cur.getString(col);
            Log.d(TAG, " download file path"+downloadPath);
            if(downloadPath != null){
                isDownloading = downloadPath.contains("");
            }
        }
        cur.close();
        Log.d(TAG, " isDownloading  " + isDownloading);
        return isDownloading;
    }

    private void downloadAndInstall(final DownloadManager downloadManager) {
        final long downloadId = enqueueDownload(downloadManager);
        final BroadcastReceiver receiver = configureDownloadCompleteBroadcastReceiver(
                downloadManager, downloadId);
        IntentFilter intentFilter
                = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        getApplicationContext().registerReceiver(receiver, intentFilter);
    }

    private long enqueueDownload(DownloadManager downloadManager) {
        Uri uri = Uri.parse(ONEMDM_URL);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDescription("Downloading...");
        request.setTitle(ONEMDM_PACKAGE);
        request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS, ONEMDM_PACKAGE + ".apk");
        return downloadManager.enqueue(request);
    }

    private BroadcastReceiver configureDownloadCompleteBroadcastReceiver
            (final DownloadManager downloadManager, final long downloadId) {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor cursor = downloadManager.query(query);

                if (cursor.moveToFirst()) {
                    Log.d(TAG, "matched the download id");
                    int columnIndex = cursor
                            .getColumnIndex(DownloadManager.COLUMN_STATUS);
                    Log.d(TAG,"status "+columnIndex);
                    if (DownloadManager.STATUS_SUCCESSFUL == cursor
                            .getInt(columnIndex)) {
                        Log.d(TAG, " download successfully completed");
                        context.unregisterReceiver(this);
                        showInstallScreen();
                    }
                    cursor.close();
                }
            }

        };
    }

    private boolean isAppInstalledAlready(){
        PackageManager pm=getPackageManager();
        try {
            pm.getPackageInfo(ONEMDM_PACKAGE,PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }
    private void showInstallScreen() {
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String packageName = ONEMDM_PACKAGE;
        String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                "/" + packageName + ".apk";
        Log.d(TAG, "file name  "+fileName);
        File apkFile = new File(fileName);
        intent.setData(Uri.fromFile(apkFile));
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, packageName);
        startActivity(intent);

    }
}
