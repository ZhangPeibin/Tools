package com.milk.tools.function.glide.customImageSize;

/**
 * Created by Administrator on 2016/12/2.
 */

public class CustomImageSizeModelFutureStudio implements CustomImageSizeModel {

    private final static String URL_FILTER = "aliyuncs.com";

    private final static String ACTION_KEY = "x-oss-process=image/";

    private final static String RESIZE_ACTION = "resize";
    private final static String FORMAT_ACTION = "format";

    private final static String WIDTH_PREFIX = "w_";
    private final static String HEIGHT_PREFIX = "h_";
    private final static String LIMIT_PREFIX = "limit_";

    private final static String FORMAT_VALUE = "webp";

    private String baseImageUrl;

    public CustomImageSizeModelFutureStudio(String baseImageUrl) {
        this.baseImageUrl = baseImageUrl;
    }

    public void setUrl(String baseImageUrl) {
        this.baseImageUrl = baseImageUrl;
    }

    @Override
    public String requestCustomSizeUrl(int width, int height) {
        String url = append(baseImageUrl, width, height);
        System.out.println("resize url :" + url);
        return url;
    }

    //http://app-resource-sd.oss-cn-hangzhou.aliyuncs.com/handImg/242153529095.jpg?
    // x-oss-process=image/resize,w_100,h_200,limit_0/format,webp
    private String append(String baseImageUrl, int width, int height) {
        if (baseImageUrl != null) {
            if (width < 0 || height < 0) return baseImageUrl;
            StringBuilder urlBuilder = new StringBuilder(baseImageUrl);
            int newWidth = calculateImageWidth(width);
            int newHeight = calculateImageHeight(width,height,newWidth);
            urlBuilder.append("?")
                    .append(ACTION_KEY).append(RESIZE_ACTION)
                    .append(",")
                    .append(WIDTH_PREFIX).append(newWidth)
                    .append(",")
                    .append(HEIGHT_PREFIX).append(newHeight)
                    .append(",")
                    .append(LIMIT_PREFIX).append(1)
                    .append("/")//start append format
                    .append(FORMAT_ACTION)
                    .append(",")
                    .append(FORMAT_VALUE);
            return urlBuilder.toString();
        }
        return null;
    }


    private static int calculateImageWidth(int width) {
        if (width == 0) return 1;
        if (width <= 480) return width;
        if (width <= 1080) return width * 2/ 3;
        if (width <= 1080*2) return width /2;
        else return calculateImageWidth(width/2);
    }

    private static int calculateImageHeight(int oldWidth,int oldHeight,int newWidth){
        float f = newWidth * oldHeight / oldWidth;
        return (int) f;
    }
}
