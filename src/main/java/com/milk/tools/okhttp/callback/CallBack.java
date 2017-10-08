package com.milk.tools.okhttp.callback;

import java.io.IOException;

/**
 *  回调方法
 */
public abstract class CallBack {
    public abstract void error(IOException e);
    public abstract void success(Object o);
}
