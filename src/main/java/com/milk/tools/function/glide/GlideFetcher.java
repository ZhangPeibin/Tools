package com.milk.tools.function.glide;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.util.Util;
import com.milk.tools.function.glide.customImageSize.CustomImageSizeModel;
import com.milk.tools.function.glide.customImageSize.CustomImageSizeModelFutureStudio;
import com.milk.tools.common.Preconditions;
import com.milk.tools.utils.StringUtil;

/**
 * Created by Administrator on 2016/11/30.
 */

public class GlideFetcher {

    private static final GlideFetcher INSTANCE = new GlideFetcher();

    public static final int NO_PLACEHOLDER = -1;

    private RequestManager mRequestManager;

    public static CustomImageSizeModel getCustomImageSizeModelFutureStudio(String url) {
        return new CustomImageSizeModelFutureStudio(url);
    }

    private void makeRequestMangaer(Context context){
        if (context instanceof FragmentActivity) {
            mRequestManager = Glide.with((FragmentActivity) context);
        } else if (context instanceof Activity) {
            mRequestManager = Glide.with((Activity) context);
        } else if (context instanceof ContextWrapper) {
            mRequestManager = Glide.with(((ContextWrapper) context).getBaseContext());
        }
        Glide.get(context).setMemoryCategory(MemoryCategory.NORMAL);
    }

    private void fragmentMakeRequestManager(Fragment fragment){
        if (fragment.getActivity() == null) {
            return;
        }
        if (Util.isOnBackgroundThread() || Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            makeRequestMangaer(fragment.getActivity());
        } else {
            makeRequestMangaer(fragment.getActivity());
        }
    }

    public static GlideFetcher get(Context context) {
        Preconditions.checkNotNull(context, "context can not be null");
        INSTANCE.makeRequestMangaer(context);
        return INSTANCE;
    }

    public static GlideFetcher get(Activity activity) {
        Preconditions.checkNotNull(activity, "activity can not be null");
        INSTANCE.makeRequestMangaer(activity);
        return INSTANCE;
    }

    public static GlideFetcher get(Fragment fragment) {
        Preconditions.checkNotNull(fragment, "fragment can not be null");
        INSTANCE.fragmentMakeRequestManager(fragment);
        return INSTANCE;
    }

    public void fetchFixedImageByViewSize(String url, ImageView imageView, int placeHolderResId) {
        if (mRequestManager == null) return;
        predealUrl(url);
        checkUrlAndTargetNotNull(url, imageView);
        final int width = imageView.getWidth();
        final int height = imageView.getHeight();
        BitmapRequestBuilder drawableTypeRequest =
                mRequestManager.load(getCustomImageSizeModelFutureStudio(url)).asBitmap().
                        diskCacheStrategy(DiskCacheStrategy.RESULT);

        if (placeHolderResId != NO_PLACEHOLDER) {
            drawableTypeRequest = drawableTypeRequest.placeholder(placeHolderResId);
        }
        if (width == 0 || height == 0) {
            drawableTypeRequest.into(imageView);
        } else {
            drawableTypeRequest
                    .override(width, height) // resizes the image to these dimensions (in pixel). does not respect aspect ratio
                    .into(imageView);
        }
    }

    public void fetchFixedImageByViewSizeNoCache(String url, ImageView imageView, int placeHolderResId) {
        if (mRequestManager == null) return;
        predealUrl(url);
        checkUrlAndTargetNotNull(url, imageView);
        final int width = imageView.getWidth();
        final int height = imageView.getHeight();
        BitmapRequestBuilder drawableTypeRequest =
                mRequestManager.load(getCustomImageSizeModelFutureStudio(url)).asBitmap().
                        skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.RESULT);

        if (placeHolderResId != NO_PLACEHOLDER) {
            drawableTypeRequest = drawableTypeRequest.placeholder(placeHolderResId);
        }
        if (width == 0 || height == 0) {
            drawableTypeRequest.into(imageView);
        } else {
            drawableTypeRequest
                    .override(width, height) // resizes the image to these dimensions (in pixel). does not respect aspect ratio
                    .into(imageView);
        }
    }

    public void fetchFixedImageByViewSizeWithHighPriority(String url, ImageView imageView, int placeHolderResId) {
        predealUrl(url);
        checkUrlAndTargetNotNull(url, imageView);
        if (mRequestManager == null) return;
        final int width = imageView.getWidth();
        final int height = imageView.getHeight();
        BitmapRequestBuilder drawableTypeRequest =
                mRequestManager.load(url).asBitmap().
                        diskCacheStrategy(DiskCacheStrategy.RESULT).
                        placeholder(placeHolderResId).
                        priority(Priority.HIGH);
        if (placeHolderResId != NO_PLACEHOLDER) {
            drawableTypeRequest = drawableTypeRequest.placeholder(placeHolderResId);
        }
        if (width == 0 || height == 0) {
            drawableTypeRequest.into(imageView);
        } else {
            drawableTypeRequest
                    .override(width, height)
                    .into(imageView);
        }
    }


    public void fetchResizeImageByViewSizeWithHighPriority(String url, ImageView imageView, int placeHolderResId) {
        predealUrl(url);
        checkUrlAndTargetNotNull(url, imageView);
        if (mRequestManager == null) return;
        final int width = imageView.getWidth();
        final int height = imageView.getHeight();
        BitmapRequestBuilder drawableTypeRequest =
                mRequestManager.load(getCustomImageSizeModelFutureStudio(url)).asBitmap().
                        diskCacheStrategy(DiskCacheStrategy.RESULT).
                        placeholder(placeHolderResId).
                        priority(Priority.HIGH);
        if (placeHolderResId != NO_PLACEHOLDER) {
            drawableTypeRequest = drawableTypeRequest.placeholder(placeHolderResId);
        }
        if (width == 0 || height == 0) {
            drawableTypeRequest.into(imageView);
        } else {
            drawableTypeRequest
                    .override(width, height)
                    .into(imageView);
        }
    }

    public void fetchResizeImageByViewSizeWithHighPriorityNoCache(String url, ImageView imageView, int placeHolderResId) {
        predealUrl(url);
        checkUrlAndTargetNotNull(url, imageView);
        if (mRequestManager == null) return;
        final int width = imageView.getWidth();
        final int height = imageView.getHeight();
        BitmapRequestBuilder drawableTypeRequest =
                mRequestManager.load(getCustomImageSizeModelFutureStudio(url)).asBitmap().
                        skipMemoryCache(true).
                        diskCacheStrategy(DiskCacheStrategy.RESULT).
                        placeholder(placeHolderResId).
                        priority(Priority.HIGH);
        if (placeHolderResId != NO_PLACEHOLDER) {
            drawableTypeRequest = drawableTypeRequest.placeholder(placeHolderResId);
        }
        if (width == 0 || height == 0) {
            drawableTypeRequest.into(imageView);
        } else {
            drawableTypeRequest
                    .override(width, height)
                    .into(imageView);
        }
    }

    public void fetch(String url, ImageView imageView, int placeHolderResId) {
        if (mRequestManager == null) return;
        predealUrl(url);
        BitmapRequestBuilder drawableTypeRequest = mRequestManager.load(getCustomImageSizeModelFutureStudio(url))
                .asBitmap().
                diskCacheStrategy(DiskCacheStrategy.RESULT).thumbnail(0.1f).priority(Priority.HIGH);

        if (placeHolderResId != NO_PLACEHOLDER) {
            drawableTypeRequest = drawableTypeRequest.placeholder(placeHolderResId);
        }
        drawableTypeRequest.into(imageView);
    }

    public void fetch(String url, ImageView imageView) {
        if (mRequestManager == null) return;
        predealUrl(url);
        mRequestManager.load(getCustomImageSizeModelFutureStudio(url)).asBitmap().
                diskCacheStrategy(DiskCacheStrategy.RESULT).priority(Priority.HIGH)
                .into(imageView);
    }

    public void fetchNoCache(String url, ImageView imageView) {
        if (mRequestManager == null) return;
        predealUrl(url);
        mRequestManager.load(getCustomImageSizeModelFutureStudio(url)).asBitmap().
                skipMemoryCache(true).
                diskCacheStrategy(DiskCacheStrategy.RESULT).priority(Priority.HIGH)
                .into(imageView);
    }


    public void fetchCircleBitmap(Context context,String url, ImageView imageView, int placeHolderResId) {
        if (mRequestManager == null) return;
        predealUrl(url);
        BitmapRequestBuilder bitmapRequestBuilder = mRequestManager.load(getCustomImageSizeModelFutureStudio(url))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .transform(new GlideCircleTransform(context));
        if (placeHolderResId != NO_PLACEHOLDER) {
            bitmapRequestBuilder.placeholder(placeHolderResId);
        }
        bitmapRequestBuilder.into(imageView);
    }

    public void fetchCircleBitmapNoCache(Context context,String url, ImageView imageView, int placeHolderResId) {
        if (mRequestManager == null) return;
        predealUrl(url);
        BitmapRequestBuilder bitmapRequestBuilder = mRequestManager.load(getCustomImageSizeModelFutureStudio(url))
                .asBitmap()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .transform(new GlideCircleTransform(context));
        if (placeHolderResId != NO_PLACEHOLDER) {
            bitmapRequestBuilder.placeholder(placeHolderResId);
        }
        bitmapRequestBuilder.into(imageView);
    }


    /**
     * 根据imageView的大小获取对应size的圆形图片
     *
     * @param url              图片url
     * @param imageView        图片加载的target View
     * @param placeHolderResId 过渡图片,传{@code NO_PLACEHOLDER}则没有
     */
    public void fetchFixedCircleBitmap(Context context,String url, ImageView imageView, int placeHolderResId) {
        if (mRequestManager == null) return;
        predealUrl(url);
        checkUrlAndTargetNotNull(url, imageView);
        final int width = imageView.getWidth();
        final int height = imageView.getHeight();
        BitmapRequestBuilder bitmapRequestBuilder;
        BitmapTypeRequest bitmapTypeRequest = mRequestManager.load(getCustomImageSizeModelFutureStudio(url))
                .asBitmap();
        if (width == 0 && height == 0) {
            bitmapRequestBuilder = bitmapTypeRequest.transform(new GlideCircleTransform(context));
        } else {
            bitmapRequestBuilder =
                    bitmapTypeRequest
                            .override(width, height)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .transform(new GlideCircleTransform(context));
        }

        if (placeHolderResId != NO_PLACEHOLDER) {
            bitmapRequestBuilder.placeholder(placeHolderResId);
        }
        bitmapRequestBuilder.into(imageView);
    }

    public void fetchBitmapTarget(String url, final FetchBitmap fetchBitmap) {
        if (mRequestManager == null) return;
        predealUrl(url);
        checkUrlNotNull(url);
        final SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                if (fetchBitmap != null) {
                    fetchBitmap.fetch(resource);
                }
            }
        };
        mRequestManager.load(url)
                .asBitmap().into(target);
    }

    public void fetchCircleBitmapTarget(Context context,String url, final FetchBitmap fetchBitmap) {
        predealUrl(url);
        checkUrlNotNull(url);
        if (mRequestManager == null) return;
        final SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                if (fetchBitmap != null) {
                    fetchBitmap.fetch(resource);
                }
            }
        };
        mRequestManager.load(getCustomImageSizeModelFutureStudio(url))
                .asBitmap().transform(new GlideCircleTransform(context)).into(target);
    }


    public void fetchSizedBitmapTarget(String url, int width, int height, final FetchBitmap fetchBitmap) {
        predealUrl(url);
        checkUrlNotNull(url);
        final SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>(width, height) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                if (fetchBitmap != null) {
                    fetchBitmap.fetch(resource);
                }
            }
        };
        mRequestManager.load(getCustomImageSizeModelFutureStudio(url))
                .asBitmap().into(target);
    }


    public <T extends View> void fetchIntoView(String url, final T t, final FetchDrawable<T> fetchBitmap) {
        predealUrl(url);
        checkUrlNotNull(url);
        final ViewTarget<T, GlideDrawable> target = new ViewTarget<T, GlideDrawable>(t) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                if (fetchBitmap != null) {
                    fetchBitmap.fetch(resource.getCurrent(), t);
                }
            }
        };
        mRequestManager.load(url).into(target);
    }

    public void fetchNoCache(String url, ImageView imageView, int placeHolderResId) {
        this.fetchNoCache(url,imageView,placeHolderResId,null);
    }


    public void fetchNoCache(String url, ImageView imageView, int placeHolderResId, RequestListener listener) {
        if (mRequestManager == null) return;
        url = predealUrl(url);
        GenericRequestBuilder genericRequestBuilder;
        if ( url.endsWith("gif") ){
            genericRequestBuilder = mRequestManager.load(url)
                    .asGif().skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT).priority(Priority.HIGH);

        }else{
            genericRequestBuilder = mRequestManager.load(getCustomImageSizeModelFutureStudio(
                    url)).asBitmap().
                    skipMemoryCache(true).
                    diskCacheStrategy(DiskCacheStrategy.RESULT).priority(Priority.HIGH);
        }

        if (placeHolderResId != NO_PLACEHOLDER) {
            genericRequestBuilder.placeholder(placeHolderResId);
        }

        if ( listener!=null ){
            genericRequestBuilder.listener(listener).into(imageView);
        }else{
            genericRequestBuilder.into(imageView);
        }
    }


    public void pauseRequests() {
        if (mRequestManager != null)
            mRequestManager.pauseRequests();
    }

    public void resumeRequests() {
        if (mRequestManager != null)
            mRequestManager.resumeRequests();
    }

    public interface FetchBitmap {
        void fetch(Bitmap bitmap);
    }

    public interface FetchDrawable<T extends View> {
        void fetch(Drawable bitmap, T t);
    }

    public interface UploadListener {
        void error(String message);

        void success(String url);

        void progress(long current, long total);
    }

    private static void checkUrlAndTargetNotNull(String url, ImageView imageView) {
        Preconditions.checkNotNull(imageView, "imageView can not be null");
        Preconditions.checkNotNull(url, "url can not be null");
    }

    private static void checkUrlNotNull(String url) {
        Preconditions.checkNotNull(url, "url can not be null");
    }

    private static String predealUrl(String url) {
        if (StringUtil.empty(url)) {
            return "";
        }
        return url;
    }
}
