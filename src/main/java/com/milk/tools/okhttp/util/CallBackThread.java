package com.milk.tools.okhttp.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/12/29.
 * used for {@link com.milk.tools.okhttp.callback.CallBackk}
 * our callback all at ui thread
 */
public class CallBackThread {

    private static CallBackThread callBackThread = get();

    private ExecutorService mExecutorService = null;

    public static CallBackThread getUiThread() {
        return callBackThread;
    }

    private static CallBackThread get() {
        return new UIThread();
    }

    //our callback not at main ui thread
    protected Executor createExecutor() {
        if (mExecutorService == null){
            mExecutorService = Executors.newCachedThreadPool();
        }
        return mExecutorService;
    }

    public void handle(Runnable runnable) {
        createExecutor().execute(runnable);
    }

    static final class UIThread extends CallBackThread{
        @Override
        protected Executor createExecutor() {
            return new MainThreadExecutor();
        }

        static class MainThreadExecutor implements Executor
        {
            private final Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void execute(Runnable r)
            {
                handler.post(r);
            }
        }
    }

}
