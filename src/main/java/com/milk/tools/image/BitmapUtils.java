package com.milk.tools.image;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by wiki on 15/11/1.
 */
public class BitmapUtils {


    /**
     *
     * @param pDrawable
     * @return bitmap
     */
    public static Bitmap convertDrawableToBitmap(Drawable pDrawable){
        BitmapDrawable lBitmapDrawable = (BitmapDrawable) pDrawable;
        return lBitmapDrawable.getBitmap();
    }

    /**
     *
     * @param pBitmap
     * @return drawable
     */
    public static Drawable convertBitmapToDrawable(Bitmap pBitmap){
        BitmapDrawable lBitmapDrawable = new BitmapDrawable(pBitmap);
        return lBitmapDrawable;
    }

    private static boolean isJPEG(byte[] b) {
        if (b.length < 2) {
            return false;
        }
        return (b[0] == (byte) 0xFF) && (b[1] == (byte) 0xD8);
    }

    private static boolean isGIF(byte[] b) {
        if (b.length < 6) {
            return false;
        }
        return b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8' && (b[4] == '7' || b[4] == '9') && b[5] == 'a';
    }

    private static boolean isPNG(byte[] b) {
        if (b.length < 8) {
            return false;
        }
        return (b[0] == (byte) 137 && b[1] == (byte) 80 && b[2] == (byte) 78 && b[3] == (byte) 71 && b[4] == (byte) 13
                && b[5] == (byte) 10 && b[6] == (byte) 26 && b[7] == (byte) 10);
    }

    private static boolean isBMP(byte[] b) {
        if (b.length < 2) {
            return false;
        }
        return (b[0] == 0x42) && (b[1] == 0x4d);
    }
}
