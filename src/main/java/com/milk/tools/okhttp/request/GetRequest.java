package com.milk.tools.okhttp.request;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/29.
 */

public class GetRequest extends Request {

    private String mFinalUrl = null;

    @Override
    protected void prepareRequestBody() {
        Map<String, String> params = mBuilder.params;
        if (params != null && !params.isEmpty()) {
            mFinalUrl = splicingUrl(mBuilder.url, params);
        }
        if (mFinalUrl == null) {
            mFinalUrl = mBuilder.url;
        }

        Map<String, String> headers = mBuilder.headers;
        if (headers != null && !headers.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator =
                    headers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && value != null)
                    requestBuilder.header(key, value);
            }
        }
    }

    @Override
    protected okhttp3.Request prepareRequest() {
        return requestBuilder.url(mFinalUrl).build();
    }


    private String splicingUrl(String sourceUrl, Map<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder(sourceUrl);
        stringBuilder.append("?");
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> keyValue = iterator.next();
            String key = keyValue.getKey();
            String value = keyValue.getValue();
            stringBuilder.append(key)
                    .append("=")
                    .append(value)
                    .append("&");
        }
        if (stringBuilder.length() == 0)
            return null;

        String resultString = stringBuilder.toString();
        if (resultString.endsWith("&"))
            return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
        return resultString;
    }
}
