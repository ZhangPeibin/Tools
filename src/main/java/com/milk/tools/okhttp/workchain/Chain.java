package com.milk.tools.okhttp.workchain;


import okhttp3.Request;

/**
 * Created by Administrator on 2016/12/30.
 */

public abstract class Chain {

    private Chain next = null;

    private Request mRequest;

    public Chain(Request request){
        this.mRequest = request;
    }


    public void setNextChain(Chain chain){
        next = chain;
    }

    public void excute(){
        process();

        if (next!=null){
            next.excute();
        }
    }

    public abstract void process();
}
