package com.milk.tools.function.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;
import com.milk.tools.function.glide.customImageSize.CustomImageSizeModel;
import com.milk.tools.function.glide.customImageSize.CustomImageSizeModelFactory;

import java.io.InputStream;

/**
 * Created by Administrator on 2016/12/2.
 * <meta-data
 * android:name="com.milk.tools.util.glide.CustomImageSizeGlideModule"
 * android:value="GlideModule" />
 */

public class CustomImageSizeGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        //do nothing
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(CustomImageSizeModel.class, InputStream.class, new CustomImageSizeModelFactory());
    }
}
