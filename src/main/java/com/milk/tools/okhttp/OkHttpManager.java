package com.milk.tools.okhttp;


import android.content.Context;

import com.milk.tools.okhttp.callback.CallBack;
import com.milk.tools.okhttp.cookie.CookieJarImpl;
import com.milk.tools.okhttp.cookie.store.PersistentCookieStore;
import com.milk.tools.okhttp.interceptor.GzipRequestInterceptor;
import com.milk.tools.okhttp.request.GetRequest;
import com.milk.tools.okhttp.request.RequestFactory;
import com.milk.tools.okhttp.util.CallBackThread;
import com.milk.tools.utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.*;

/**
 * Created by Administrator on 2016/12/29.
 * 对okhttp的封装
 */
public class OkHttpManager {

    private static final MediaType TEXT_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpManager Instance;

    //OkHttpClient
    private OkHttpClient mOkHttpClient;

    //used to handle result back
    private CallBackThread mCallBackThread = CallBackThread.getUiThread();

    private Configuration mConfiguration;

    private GzipRequestInterceptor mGzipRequestInterceptor;

    private Context mContext;

    private OkHttpManager (Configuration configuration) {
        this.mConfiguration = configuration;
        createOkHttpClient();
    }


    /**
     * 创建OKHttpClient
     */
    private void createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        mGzipRequestInterceptor = new GzipRequestInterceptor();
        if (mConfiguration == null) {
            mOkHttpClient = builder.addInterceptor(mGzipRequestInterceptor).build();
        } else {
            CookieJar cookieJar = mConfiguration.mCookieJar;
            if (cookieJar != null) {
                builder.cookieJar(cookieJar);
            }
            long connectTimeOut = mConfiguration.mConnectTimeOut;
            if (connectTimeOut > 0) {
                builder.connectTimeout(connectTimeOut, TimeUnit.SECONDS);
            }
            mOkHttpClient = builder.build();
        }
    }


    /**
     * 获取OkHttpManager实例,这种不需要做cookie
     * @return
     */
    public static OkHttpManager getOkHttpManager() {
        return getOkHttpManager(null);
    }

    /**
     * 获取OkHttpManager实例
     * @return
     */
    public static OkHttpManager getCookieOkHttpManager(Context context) {
        Configuration configuration = new Configuration();
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(context));
        configuration.mCookieJar = cookieJar;
        return getOkHttpManager(configuration);
    }


    /**
     * 获取OKHttpManager实例
     * @param configuration
     * @return
     */
    public static OkHttpManager getOkHttpManager(Configuration configuration) {
        if (Instance == null) {
            synchronized (OkHttpManager.class) {
                if (Instance == null) {
                    Instance = new OkHttpManager(configuration);
                }
            }
        }
        return Instance;
    }


    public static final class Configuration {
        CookieJar mCookieJar;
        long mConnectTimeOut = 20L;

        public Configuration setCookieJar(CookieJar cookieJar) {
            this.mCookieJar = cookieJar;
            return this;
        }

        public Configuration setConnectTimeout(long connectTimeOut) {
            mConnectTimeOut = connectTimeOut;
            return this;
        }
    }

    public void setCallBackThread(CallBackThread callBackThread) {
        this.mCallBackThread = callBackThread;
    }

    /**
     * 同步get方法
     *
     * @param url
     */
    public String get(String url) {
        return get(url, null);
    }

    /**
     * post方法提交表单
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public String post(String url, Map<String, String> params) {
        Response response = null;
        try {
            FormBody.Builder body = new FormBody.Builder();
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String key = entry.getKey();
                String value = entry.getValue();
                body.add(key, value);
            }
            FormBody requestBody = body.build();
            Request request = new Request.Builder()
                    .url(url)
                    //add Content-Encoding to skip gzip
                    .addHeader("Content-Encoding", "deflate")
                    .post(requestBody)
                    .build();
            checkOkHttpClientUsable();
            response = mOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyString = response.body().string();
                return bodyString;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (response!=null){
                response.body().close();
                response.close();
            }
        }
        return null;
    }

    /**
     * post方法提交string
     *
     * @param url
     * @param body
     * @return
     * @throws IOException
     */
    public String post(String url, String body) {
        Response response = null;
        try {
            String encodeBody = URLEncoder.encode(body, "utf-8");
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(TEXT_MEDIA_TYPE, encodeBody)).build();
            checkOkHttpClientUsable();
            response = mOkHttpClient.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                String responseString = response.body().string();
                return responseString;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null){
                response.body().close();
                response.close();
            }
        }
        return null;
    }

    /**
     * 同步带参数的get方法
     *
     * @param url
     */
    public String get(String url, Map<String, String> params) {
        RequestFactory factory = new RequestFactory.Builder().url(url)
                .addParams(params)
                .request(GetRequest.class)
                .build();
        Request request = factory.createRequest();
        checkOkHttpClientUsable();
        Response response = null;
        try {
            response = mOkHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                if (response.body() != null) {
                    response.body().close();
                }
                response.close();
            }
        }
        return null;
    }

    /**
     * 异步带参数的get方法
     * @param url
     */
    public void asynGet(String url, Map<String, String> params, final CallBack callBack) {
        RequestFactory factory = new RequestFactory.Builder().url(url)
                .addParams(params)
                .request(GetRequest.class)
                .callBack(callBack)
                .build();
        Request request = factory.createRequest();
        checkOkHttpClientUsable();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailureResult(call, e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                sendSuccessResult(call, response, callBack);
            }
        });
    }


    /**
     * 同步下载文件的方法
     * @param downloadUrl
     * @param filePath
     * @throws Exception
     */
    public void downloadFileSync(String downloadUrl, String filePath) throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Failed to download file: " + response);
        }
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(response.body().bytes());
        fos.close();
    }



    /**
     * 异步下载文件的方法
     * @param downloadUrl
     * @param filePath
     */
    public void downloadFileAsync(final String downloadUrl, final String filePath, final DownloadFileCallBack downloadFileCallBack) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl).build();
        client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    if ( downloadFileCallBack != null ) downloadFileCallBack.callBack(false,filePath);
                    return;
                }
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    fos = new FileOutputStream(filePath);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    if ( downloadFileCallBack != null ) downloadFileCallBack.callBack(true,filePath);
                } catch (IOException e) {
                    Logger.e(e.toString());
                    if ( downloadFileCallBack != null ) downloadFileCallBack.callBack(false,filePath);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        Logger.e(e.toString());
                    }
                }
            }
        });
    }


    /**
     * 下载完文件的回调
     */
    public interface DownloadFileCallBack{
        void callBack(boolean success,String localFilePath);
    }



    /**
     * 上传文件
     *
     * @param requestUrl 接口地址
     * @param paramsMap  参数
     * @param filePath   文件路径
     */
    public void upLoadFile(String requestUrl, String filePath, HashMap<String, Object> paramsMap) {
        try {
            //补全请求地址
            MultipartBody.Builder builder = new MultipartBody.Builder();
            //设置类型
            builder.setType(MultipartBody.FORM);
            //追加参数
            for (String key : paramsMap.keySet()) {
                Object object = paramsMap.get(key);
                if (!(object instanceof File)) {
                    builder.addFormDataPart(key, object.toString());
                } else {
                    File file = (File) object;
                    builder.addFormDataPart(key, file.getName(), RequestBody.create(null, file));
                }
            }
            //创建RequestBody
            RequestBody body = builder.build();
            //创建Request
            final Request request = new Request.Builder().url(requestUrl).post(body).build();
            //单独设置参数 比如读取超时时间
            final Call call = mOkHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Logger.e(e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        Logger.e("response ----->" + string);
                    } else {
                    }
                }
            });
        } catch (Exception e) {
        }
    }


    private void sendFailureResult(Call call, final IOException e, final CallBack callBack) {
        mCallBackThread.handle(new Runnable() {
            @Override
            public void run() {
                callBack.error(e);
            }
        });
    }

    private void sendSuccessResult(Call call, final Response response, final CallBack callBack) {
        mCallBackThread.handle(new Runnable() {
            @Override
            public void run() {
                try {
                    callBack.success(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (response != null) {
                        response.body().close();
                    }
                }
            }
        });
    }


    private void checkOkHttpClientUsable() {
        if (mOkHttpClient == null) {
            createOkHttpClient();
        }
    }

}
