package com.milk.tools.image;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

/**
 * we all know we should recycle bitmap when we never ever use bitmap with touched imageView
 * but if we invoke {@link Bitmap} Recycle() method when the imageView still
 * using , it will make throw a exception.This BitmapDrawable will use "reference counting".
 * Created by wiki on 15/12/13.
 */
public class RecycleBitmapDrawable extends BitmapDrawable{

    /**
     * when we put this drawable to cache collection , this will add one
     */
    private int mCacheReferenceCount = 0;


    private int mDisplayReferenceCount = 0;

    private boolean mDisplay = false;

    public RecycleBitmapDrawable(Resources resource, Bitmap bitmap) {
        super(resource,bitmap);
    }


    /**
     * we will count {@code mDisplayReferenceCount} whether this drawable is display
     * @param display
     */
    public void setIsDisplay(boolean display){
        synchronized (this){
            if(display){
                mDisplayReferenceCount++;
                mDisplay = true;
            }else{
                mDisplayReferenceCount--;
            }

            checkState();
        }
    }


    /**
     * we will count {@code mDisplayReferenceCount} whether this drawable is display
     * @param cached
     */
    public void setIsCached(boolean cached){
        synchronized (this){
            if(cached){
                mCacheReferenceCount++;
            }else{
                mCacheReferenceCount--;
            }

            checkState();
        }
    }


    /**
     * check whether this bitmap can recycle
     */
    private void checkState(){
        if(mCacheReferenceCount <= 0
                && mDisplayReferenceCount <= 0
                && !hasValidBitmap() && mDisplay){
            getBitmap().recycle();
        }
    }


    /**
     * check this bitmap is not valid
     * @return
     */
    private boolean hasValidBitmap(){
        Bitmap lBitmap = getBitmap();
        if(lBitmap !=null){
            return lBitmap.isRecycled();
        }else{
            return false;
        }
    }


}
