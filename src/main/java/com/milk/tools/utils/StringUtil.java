package com.milk.tools.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wiki on 15/11/21.
 */
public class StringUtil {

    private static final String URL_KEY_APPEND = "=";
    private static final String URL_PARAM_APPEND = "&";

    /**
     * check {@param pCheckString} is not null or not a empty string
     *
     * @param pCheckString CheckString to checked
     * @return true the string is not empty ,false is that string is empty
     */
    public static boolean empty(String pCheckString) {
        return pCheckString == null || pCheckString.length() == 0;
    }


    public static String encodeUrl(String url, Map<String, String> params) {
        if (url == null) {
            return null;
        }

        if (params == null) {
            return url;
        }

        try {
            StringBuilder urlString = new StringBuilder(url);
            urlString.append("?");
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                final String key = entry.getKey();
                final String value = entry.getValue();
                if (!empty(key) && !empty(value)) {
                    urlString.append(key);
                    urlString.append(URL_KEY_APPEND);
                    urlString.append(value);
                    urlString.append(URL_PARAM_APPEND);
                }
            }
            url = urlString.toString();
            if (url.endsWith(URL_PARAM_APPEND))
                url = url.substring(0, url.length() - 1);
        }catch (Exception e){}
        return url;
    }


    /**
     * 根据拼音获取到第一个字母,如果没有则返回"#"
     *
     * @param sortKey
     * @return
     */
    public static String getAlpha(String sortKey) {
        if (sortKey == null) {
            return "#";
        }

        if (sortKey.trim().length() == 0) {
            return "#";
        }

        char c = sortKey.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        } else {
            return "#";
        }
    }



    public static String filterNullWithBlank(String stringMaybeContainNull){
        if(stringMaybeContainNull == null){
            return "";
        }
        return stringMaybeContainNull;
    }



    public static final String getUniqueID() {
        int t1 = (int)(System.currentTimeMillis() / 1000L);
        int t2 = (int)System.nanoTime();
        int t3 = (new Random()).nextInt();
        int t4 = (new Random()).nextInt();
        byte[] b1 = getBytes(t1);
        byte[] b2 = getBytes(t2);
        byte[] b3 = getBytes(t3);
        byte[] b4 = getBytes(t4);
        byte[] bUniqueID = new byte[16];
        System.arraycopy(b1, 0, bUniqueID, 0, 4);
        System.arraycopy(b2, 0, bUniqueID, 4, 4);
        System.arraycopy(b3, 0, bUniqueID, 8, 4);
        System.arraycopy(b4, 0, bUniqueID, 12, 4);
        return Base64.encodeToString(bUniqueID, 2);
    }



    public static String getFormatString(Context context, int resId, Object ...args){
        return String.format(context.getString(resId),args);
    }



    public static byte[] getBytes(int i) {
        byte[] bInt = new byte[4];
        bInt[3] = (byte)(i % 256);
        int value = i >> 8;
        bInt[2] = (byte)(value % 256);
        value >>= 8;
        bInt[1] = (byte)(value % 256);
        value >>= 8;
        bInt[0] = (byte)(value % 256);
        return bInt;
    }




    public static boolean isEmpty(String str) {
        if (null == str)
            return true;
        if (str.trim().length() == 0)
            return true;
        return str.trim().equalsIgnoreCase("null");
    }


    /**
     * 字符替换
     *
     * @param source
     * @param regex
     * @param replacement
     * @return
     */
    public static String replace(String source, String regex, String replacement) {
        int index = -1;
        StringBuffer buffer = new StringBuffer();
        while ((index = source.indexOf(regex)) >= 0) {
            buffer.append(source.substring(0, index));
            buffer.append(replacement);
            source = source.substring(index + regex.length());
        }
        buffer.append(source);
        return buffer.toString();
    }

    /**
     * 工程默认编码
     *
     * @param obj
     * @return
     */
    public static String urlEncode(String obj) {
        return urlEncode(obj, "GBK");
    }

    /**
     * 工程默认解码
     *
     * @param obj
     * @return
     */
    public static String urlDecode(String obj) {
        return urlDecode(obj, "GBK");
    }

    public static String urlEncode(String obj, String charset) {
        String result = null;
        if (obj != null) {
            try {
                result = URLEncoder.encode(obj, charset);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                return result;
            }
        }
        return result;
    }

    public static String urlDecode(String obj, String charset) {
        String result = null;
        if (obj != null) {
            try {
                result = URLDecoder.decode(obj, charset);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                return result;
            }
        }
        return result;
    }


    /**
     * md5加密
     *
     * @param str
     * @return
     */
    public static String md5(String str) {
        StringBuffer buf = new StringBuffer("");
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();
            int i;
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }

    /**
     * 获取http请求的host
     *
     * @param url
     * @return
     */
    public static String getDomain(String url) {
        try {
            URI uri = new URI(url);
            return uri.getHost();
        } catch (URISyntaxException e) {
            return null;
        }
    }



    /*
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
    */
    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }


    /**
     * 根据生日获取星座
     *
     * @param birthday
     * @return
     */
    public static String getConstellation(String birthday) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(birthday);
            int month = date.getMonth() + 1;
            int day = date.getDate();
            switch (month) {
                case 1:
                    if (day <= 19) {
                        str = "魔蝎座";
                    } else {
                        str = "水瓶座";
                    }
                    break;
                case 2:
                    if (day <= 18) {
                        str = "水瓶座";
                    } else {
                        str = "双鱼座";
                    }
                    break;
                case 3:
                    if (day <= 20) {
                        str = "双鱼座";
                    } else {
                        str = "白羊座";
                    }
                    break;
                case 4:
                    if (day <= 19) {
                        str = "白羊座";
                    } else {
                        str = "金牛座";
                    }
                    break;
                case 5:
                    if (day <= 20) {
                        str = "金牛座";
                    } else {
                        str = "双子座";
                    }
                    break;
                case 6:
                    if (day <= 21) {
                        str = "双子座";
                    } else {
                        str = "巨蟹座";
                    }
                    break;
                case 7:
                    if (day <= 22) {
                        str = "巨蟹座";
                    } else {
                        str = "狮子座";
                    }
                    break;
                case 8:
                    if (day <= 22) {
                        str = "狮子座";
                    } else {
                        str = "处女座";
                    }
                    break;
                case 9:
                    if (day <= 22) {
                        str = "处女座";
                    } else {
                        str = "天秤座";
                    }
                    break;
                case 10:
                    if (day <= 23) {
                        str = "天秤座";
                    } else {
                        str = "天蝎座";
                    }
                    break;
                case 11:
                    if (day <= 22) {
                        str = "天蝎座";
                    } else {
                        str = "射手座";
                    }
                    break;
                case 12:
                    if (day <= 21) {
                        str = "射手座";
                    } else {
                        str = "魔蝎座";
                    }
                    break;
                default:
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;

    }



    public static double round(double v, int scale) {
        String temp = "####.";
        for (int i = 0; i < scale; i++) {
            temp += "0";
        }
        return Double.valueOf(new java.text.DecimalFormat(temp).format(v));
    }

    public static byte[] decode(final byte[] bytes) {
        return Base64.decode(bytes, 2);
    }

    public static String encode(final byte[] bytes) {
        return new String(Base64.encode(bytes, 2));
    }

    public static String formatDate(int hour) {
        String src = "";
        if (hour >= 23 || hour < 1) {
            src = "子时";
        } else if (hour >= 1 && hour < 3) {
            src = "丑时";
        } else if (hour >= 3 && hour < 5) {
            src = "寅时";
        } else if (hour >= 5 && hour < 7) {
            src = "卯时";
        } else if (hour >= 7 && hour < 9) {
            src = "辰时";
        } else if (hour >= 9 && hour < 11) {
            src = "巳时";
        } else if (hour >= 11 && hour < 13) {
            src = "午时";
        } else if (hour >= 13 && hour < 15) {
            src = "未时";
        } else if (hour >= 15 && hour < 17) {
            src = "申时";
        } else if (hour >= 17 && hour < 19) {
            src = "酉时";
        } else if (hour >= 19 && hour < 21) {
            src = "戌时";
        } else if (hour >= 21 && hour < 23) {
            src = "亥时";
        }
        return src;
    }

    public static String formatDate1(int position) {
        String src = "";
        switch (position) {
            case 0:
                src = "子时";
                break;
            case 1:
                src = "丑时";
                break;
            case 2:
                src = "寅时";
                break;
            case 3:
                src = "卯时";
                break;
            case 4:
                src = "辰时";
                break;
            case 5:
                src = "巳时";
                break;
            case 6:
                src = "午时";
                break;
            case 7:
                src = "未时";
                break;
            case 8:
                src = "申时";
                break;
            case 9:
                src = "酉时";
                break;
            case 10:
                src = "戌时";
                break;
            case 11:
                src = "亥时";
                break;
        }
        return src;
    }

    public static int formatDateIndex(int hour) {
        int position = -1;
        if (hour >= 23 && hour < 1) {
            position = 0;
        } else if (hour >= 1 && hour < 3) {
            position = 1;
        } else if (hour >= 3 && hour < 5) {
            position = 2;
        } else if (hour >= 5 && hour < 7) {
            position = 3;
        } else if (hour >= 7 && hour < 9) {
            position = 4;
        } else if (hour >= 9 && hour < 11) {
            position = 5;
        } else if (hour >= 11 && hour < 13) {
            position = 6;
        } else if (hour >= 13 && hour < 15) {
            position = 7;
        } else if (hour >= 15 && hour < 17) {
            position = 8;
        } else if (hour >= 17 && hour < 19) {
            position = 9;
        } else if (hour >= 19 && hour < 21) {
            position = 10;
        } else if (hour >= 21 && hour < 23) {
            position = 11;
        }
        return position;
    }

    /**
     *  去掉影响居中的标签
     * @param source
     * @return
     */
    public static String moveCenterTag (String source) {
        if (source == null)
            return null;
//        source = replace(source, "<p>", "");
        source = moveTag(source, "p");
        source = replace(source, "</p>", "");
//        source = replace(source, "<div>", "");
        source = moveTag(source, "div");
        source = replace(source, "</div>", "");
        source = moveTag(source, "span");
        source = replace(source, "</span>", "");
        source = replace(source, "</br>", "");
        source = replace(source, "<br>", "");
        source = replace(source, "<o:p>", "");
        source = replace(source, "</o:p>", "");
        source = replace(source,"\n","");
        return source;
    }

    /**
     * 替换指定标签标签
     * @param source
     * @param tag
     * @return
     */
    public static String moveTag (String source, String tag) {
        if (tag == null)
            return source;
        if (source == null)
            return null;
        String reg = "<" + tag + "[^>]*>";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(source);
        while(m.find()){
            String  find = m.group() ;
            source = source.replace(find, "") ;
        }
        source = replace(source, "</" + tag + ">", "");
        return source;
    }


    /**
     * 将字符串按照","或者"，"分隔为数组
     * @param source 将要进行解析的字符串
     * @return 解析后的数组,如果source为空则返回空数组
     */
    public static String[] convertStringToArray(String source){
        if(source == null){
            return new String[]{};
        }
        source = source.replace(" ","");
        if(source.contains(",") || source.contains("，")){
            String[] firstSource = source.split(",");
            List<String> resultSource = new ArrayList<String>();
            final int firstSourceLength = firstSource.length;
            for (int i=0;i< firstSourceLength;i++){
                String tmpSource = firstSource[i];//xxx，xxx
                if(tmpSource.contains("，")){
                    String[] secondSource = tmpSource.split("，");
                    for (String s :secondSource){
                        resultSource.add(s.trim());
                    }
                }else{
                    resultSource.add(tmpSource);
                }
            }
            return resultSource.toArray(new String[resultSource.size()]);
        }
        return new String[]{source};
    }


    public static <T>  String ListToString(List<T> list){
        if (list == null) return null;
        StringBuilder stringBuilder = new StringBuilder();
        for (T t: list){
            stringBuilder.append(t.toString());
        }
        return stringBuilder.toString();
    }

    public static boolean isBlank(String s) {
        return s == null || s.isEmpty() || s.trim().isEmpty();
    }

    public static boolean isBlank(List list){
        return list == null || list.isEmpty();
    }


    public static List<String> arrayToList(String[] array){
        if (array == null) return Collections.emptyList();
        List<String> a = new ArrayList<>();
        for (String s : array){
            a.add(s);
        }
        return a;
    }

}
