package com.milk.tools.common;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.milk.tools.okhttp.OkHttpManager;
import com.milk.tools.utils.Gson;
import com.milk.tools.utils.Logger;
import com.milk.tools.utils.PackageUtil;
import com.milk.tools.utils.StringUtil;
import java.util.Map;

/**
 * app更新相关
 * Created by peibin on 17-10-8.
 */
public class UpdateManager {

    private String mNewApkDownLoadUrl = null;

    /**
     * 检查是否有新的版本
     * 请求的返回数据,必须包含,当前服务器版本,最低的可用版本,以及body和下载apk的url
     * @param url 请求的网络链接
     */
    public void check(Context context,String url, PrepareUpdateListener updateListener){
        Preconditions.checkNotNull(updateListener,"PrepareUpdateListener can not be null");
        String result = OkHttpManager.getOkHttpManager().get(url);
        if ( StringUtil.isBlank(url) ){
            updateListener.onCheckFail();
            return;
        }

        Map<String,String> map = Gson.getGson().fromJson(result,new TypeToken<Map<String,String>>(){}.getType());
        final String apkUrl = map.get("url");
        final String serverApkVersion = map.get("version");
        final String minApkVersion = map.get("minVersion");
        final String body = map.get("body");

        if ( StringUtil.isBlank(apkUrl) ||
                StringUtil.isBlank(serverApkVersion) ){
            updateListener.onCheckFail();
            return;
        }

        mNewApkDownLoadUrl = apkUrl;

        int currentAppVersionCode = PackageUtil.getAppVersionCode(context);
        int serverAppVersionCode = Integer.parseInt(serverApkVersion);
        int minAppVersionCode = Integer.parseInt(minApkVersion);

        //如果最小支持版本大于当前的版本,那么就强制更新
        if ( minAppVersionCode > currentAppVersionCode ){
            Logger.d("有新的版本需要更新,需要强制更新");
            updateListener.needUpdate(true,body);
        }else if ( serverAppVersionCode > currentAppVersionCode ){
            Logger.d("有新的版本需要更新,但不必强制更新");
            updateListener.needUpdate(false,body);
        }else{
            Logger.d("当前版本已经是最新的版本");
        }
    }

    /**
     * 安装的核心方法
     */
    public void install(){

    }


    public interface PrepareUpdateListener{
        void needUpdate(boolean focusUpdate,String body);
        void onCheckFail();
    }
}
