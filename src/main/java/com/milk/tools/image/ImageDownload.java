package com.milk.tools.image;

/**
 * Created by peibin on 17-5-17.
 */

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public final class ImageDownload {

    public static final String ROOT = (Environment.getExternalStorageDirectory() + "/mogucloud/DCIM/");
    private String finalRoot = ROOT + this.position;
    private String url;
    private String destPath = finalRoot + "/" + this.fileName;
    private String position = "default";
    private String fileName;

    public ImageDownload(String position) {
        this.position = position;
        finalRoot = ROOT + this.position;
    }

    public void setUrl(String url){
        this.url = url;
        this.fileName = url.substring(url.lastIndexOf("/") + 1);
        this.destPath = finalRoot + "/" + this.fileName;
    }

    public final String getDestPath() {
        return this.destPath;
    }

    public final static String getRoot(){
        return ROOT;
    }

    public final File getFileRoot(){
        return new File(this.finalRoot);
    }

    public final void startScanFile(Context context) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(this.finalRoot)));
        context.sendBroadcast(intent);
//        MediaScanner.a().a(context, new ImageKV(this.destPath, "image/*"));

//        new MediaScannerConnection(context, new MediaScannerConnection.MediaScannerConnectionClient() {
//            @Override
//            public void onMediaScannerConnected() {
//
//            }
//
//            @Override
//            public void onScanCompleted(String path, Uri uri) {
//            }
//        }).scanFile(destPath,null);
    }

    public final String getFileName() {
        return this.fileName;
    }

    public final void download() {
        File file = new File(finalRoot);
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(this.destPath);
        //如果文件存在,则跳出此url下载,需要求服务器图片链接一定不同
        if (file.exists()) {
            return;
        }

        try {
            URLConnection openConnection = new URL(this.url).openConnection();
            openConnection.getContentLength();
            InputStream inputStream = openConnection.getInputStream();
            byte[] bArr = new byte[1024];
            OutputStream fileOutputStream = new FileOutputStream(this.destPath);
            while (true) {
                int read = inputStream.read(bArr);
                if (read == -1) {
                    fileOutputStream.close();
                    inputStream.close();
                    return;
                }
                fileOutputStream.write(bArr, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}