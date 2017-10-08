package com.milk.tools.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import com.milk.tools.model.PictureInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/3/12.
 */
public class ImageQuery {
    private Context context;
    private Cursor cursor;
    private String sortOrder;

    private Message message;

    private ResultInvoke resultInvoke;

    private static final String[] STORE_IMAGES = new String[]{MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Thumbnails.DATA};

    private ImageQuery(Context context) {
        this.context = context;
    }
    /**
     * 获取实例化对象
     *
     * @param context
     * @return
     */
    public static ImageQuery getInstance(Context context) {
        return new ImageQuery(context);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            List<PictureInfo> imageFileInfos = (List<PictureInfo>) msg.obj;
            if (resultInvoke != null) {
                resultInvoke.invoke(imageFileInfos);
            }
        }
    };

    /**
     * 获取图片的list
     *
     * @param resultInvoke
     */
    public void getImage(ResultInvoke resultInvoke) {
        this.resultInvoke = resultInvoke;
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";
                        cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES,
                                null, null, sortOrder);
                        List<PictureInfo> pictureInfos = new ArrayList<PictureInfo>();
                        PictureInfo pictureInfo;
                        //cursor.moveToFirst();
                        int idColIndex = cursor.getColumnIndex(STORE_IMAGES[0]);
                        int urlColIndex = cursor.getColumnIndex(STORE_IMAGES[1]);
                        int colSizeIndex = cursor.getColumnIndex(STORE_IMAGES[2]);
                        int colNameIndex = cursor.getColumnIndex(STORE_IMAGES[3]);
                        int bucketIdIndex = cursor.getColumnIndex(STORE_IMAGES[4]);
                        int thumbnailsUrlColIndex = cursor.getColumnIndex(STORE_IMAGES[5]);
                        while (cursor.moveToNext()) {
                            pictureInfo = new PictureInfo();
                            pictureInfo.setUrl(cursor.getString(urlColIndex));
                            pictureInfo.setThumbnailsUrl(cursor.getString(thumbnailsUrlColIndex));
                            pictureInfo.setFilePath(cursor.getString(urlColIndex));
                            pictureInfo.setFilesize(cursor.getLong(colSizeIndex));
                            pictureInfo.setName(cursor.getString(colNameIndex));
                            pictureInfos.add(pictureInfo);
                        }
                        message = new Message();
                        message.obj = pictureInfos;
                        handler.sendMessage(message);
                        cursor.close();
                    }
                }).start();
    }

    public interface ResultInvoke {
        void invoke(List<PictureInfo> fileInfos);
    }

}
