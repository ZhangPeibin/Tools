package com.milk.tools.function.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by Administrator on 2016/12/2.
 *  <meta-data
 *  android:name="com.milk.tools.glide.ARGB8888Module"
 *  android:value="GlideModule" />
 */
public class OptionConfiguration implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        //用来在Glide单例创建之前应用所有的选项配置，该方法每次实现只会被调用一次
        //所有的选项配置都应该集成在同一个GlideModule
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        //todo nothing
    }
}
