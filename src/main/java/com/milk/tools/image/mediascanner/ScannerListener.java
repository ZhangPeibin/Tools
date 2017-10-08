package com.milk.tools.image.mediascanner;


import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by peibin on 17-5-17.
 */

public class ScannerListener implements MediaScannerConnection.MediaScannerConnectionClient {
    private MediaScanner mediaScanner;
    private List<ImageKV> imageKVS = null;
    private List<ImageKV> c = null;
    private MediaScannerConnection mediaScannerConnection;

    public ScannerListener(MediaScanner lVar, Context context, List<ImageKV> list) {
        this.mediaScanner = lVar;
        this.imageKVS = list;
        this.mediaScannerConnection = new MediaScannerConnection(context, this);
        this.c = new ArrayList<>();
    }

    private void a(ImageKV nVar) {
        File file = new File(nVar.a);
        if (file.isFile()) {
            this.c.add(nVar);
            return;
        }
        File[] listFiles = file.listFiles();
        if (listFiles != null && listFiles.length > 0) {
            for (File absolutePath : listFiles) {
                a(new ImageKV(absolutePath.getAbsolutePath(), nVar.b));
            }
        }
    }

    private void b() {
        if (this.c == null || this.c.isEmpty()) {
            this.mediaScannerConnection.disconnect();
            return;
        }
        ImageKV nVar = (ImageKV) this.c.remove(this.c.size() - 1);
        this.mediaScannerConnection.scanFile(nVar.a, nVar.b);
    }

    public final void a() {
        if (this.imageKVS != null && !this.imageKVS.isEmpty()) {
            for (ImageKV a : this.imageKVS) {
                a(a);
            }
            this.mediaScannerConnection.connect();
        }
    }

    public final void onMediaScannerConnected() {
        b();
    }

    public final void onScanCompleted(String str, Uri uri) {
        b();
    }
}
