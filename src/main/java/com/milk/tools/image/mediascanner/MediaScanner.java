package com.milk.tools.image.mediascanner;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class MediaScanner {
    private static volatile MediaScanner a;

    private MediaScanner() {
    }

    public static MediaScanner a() {
        synchronized (MediaScanner.class) {
            if (a == null) {
                a = new MediaScanner();
            }
        }
        return a;
    }

    public final void a(Context context, ImageKV nVar) {
        List arrayList = new ArrayList(1);
        arrayList.add(nVar);
        new ScannerListener(this, context, arrayList).a();
    }
}