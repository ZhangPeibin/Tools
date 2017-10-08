package com.milk.tools.okhttp.request;

import android.util.Log;

import com.milk.tools.okhttp.callback.CallBack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/29.
 */

public class RequestFactory {

    private static final String TAG = "RequestFactory";

    protected Builder mBuilder;

    public RequestFactory(Builder builder) {
        this.mBuilder = builder;
    }

    public okhttp3.Request createRequest() {
        if (mBuilder == null) {
            throw new IllegalArgumentException("RequestFactory.Builder can not be null");
        }
        Class<? extends Request> clz = mBuilder.mClass;
        try {
            Request request = clz.newInstance();
            request.build(mBuilder);
            return request.obtain();
        } catch (InstantiationException e) {
            Log.e(TAG, "can not access constructor,please check your request can access.");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static final class Builder {
        public String url;
        public Map<String, String> headers = new HashMap<>();
        public Map<String, String> params = new HashMap<>();
        public CallBack mCallBack;
        public Class<? extends Request> mClass;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder addHeader(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public Builder addHeaders(Map<String, String> headers) {
            if (headers != null) {
                headers.putAll(headers);
            }
            return this;
        }

        public Builder addParam(String key, String value) {
            params.put(key, value);
            return this;
        }

        public Builder addParams(Map<String, String> headers) {
            if (params != null && headers!=null) {
                params.putAll(headers);
            }
            return this;
        }

        public Builder callBack(CallBack callBack) {
            mCallBack = callBack;
            return this;
        }

        public Builder request(Class<? extends Request> aClass) {
            mClass = aClass;
            return this;
        }

        public RequestFactory build() {
            return new RequestFactory(this);
        }
    }
}
