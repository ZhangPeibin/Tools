package com.milk.tools.okhttp.request;

/**
 * Created by Administrator on 2016/12/29.
 */

public abstract class Request {

    public RequestFactory.Builder mBuilder;

    public okhttp3.Request.Builder requestBuilder =  new okhttp3.Request.Builder();

    public void build(RequestFactory.Builder builder){
        this.mBuilder = builder;
    }

    public okhttp3.Request obtain(){
        if (mBuilder !=null){
            prepareRequestBody();
            return prepareRequest();
        }
        return null;
    }

    protected abstract void prepareRequestBody();

    protected abstract okhttp3.Request prepareRequest();
}
