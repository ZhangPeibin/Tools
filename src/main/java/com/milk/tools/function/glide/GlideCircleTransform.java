package com.milk.tools.function.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

/**
 * Created by Administrator on 2016/12/1.
 */

public class GlideCircleTransform implements Transformation<Bitmap>{

    private BitmapPool mBitmapPool;

    public GlideCircleTransform(Context context) {
        this(Glide.get(context).getBitmapPool());
    }

    public GlideCircleTransform(BitmapPool bitmapPool){
        mBitmapPool = bitmapPool;
    }

    @Override
    public Resource<Bitmap> transform(Resource<Bitmap> resoure, int outWidth, int outHeight) {
        Bitmap toTransform = resoure.get();

        if(toTransform == null) return null;

        final int width = toTransform.getWidth();
        final int height = toTransform.getHeight();

        final int size = Math.min(width,height);
        int x = (toTransform.getWidth() - size) / 2;
        int y = (toTransform.getHeight() - size) / 2;

        Bitmap source = Bitmap.createBitmap(toTransform,x,y,size,size);

        //get bitmap from pool
        Bitmap poolBitmap = mBitmapPool.get(size,size, Bitmap.Config.ARGB_8888);
        if (poolBitmap == null){
            poolBitmap = Bitmap.createBitmap(size,size, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(poolBitmap);
        Paint paint = new Paint();
        BitmapShader bitmapShader = new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        if(x !=0 || y !=0){
            Matrix matrix = new Matrix();
            matrix.setTranslate(-x, -y);
            bitmapShader.setLocalMatrix(matrix);
        }
        paint.setShader(bitmapShader);
        paint.setAntiAlias(true);


        float r = size / 2f;
        canvas.drawCircle(r,r,r,paint);
        return BitmapResource.obtain(poolBitmap,mBitmapPool);
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
