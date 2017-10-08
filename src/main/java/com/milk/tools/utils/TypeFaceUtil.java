package com.milk.tools.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Hashtable;

/**
 * 用来进行TypeFace的缓存功能.
 * 使每个对应的assetPath只有一个实例存在
 * 可避免因为TypeFace导致的OOM
 */
public class TypeFaceUtil {

    private static final String TAG = "TypeFaceUtil";

    //用来保存TypeFace路径以及实例的集合
    private static final Hashtable<String, Typeface> typeFaceCache = new Hashtable<String, Typeface>();

    public static Typeface createTypeface(Context context, String assetPath) {
        if (assetPath != null) {
            synchronized (typeFaceCache) {
                if (!typeFaceCache.contains(assetPath)) {
                    try {
                        Typeface typeface = Typeface.createFromAsset(context.getAssets(), assetPath);
                        if (typeface != null) {
                            typeFaceCache.put(assetPath, typeface);
                        }
                        return typeface;
                    } catch (Exception e) {
                        Log.e(TAG, "Could not get typeface '" + assetPath + "' because " + e.getMessage());
                        return null;
                    }
                }
                return typeFaceCache.get(assetPath);
            }
        }
        return null;
    }

    /**
     * 释放资源
     */
    public static void release() {
        typeFaceCache.clear();
    }


    /**
     * <p>Replace the font of specified view and it's children</p>
     *
     * @param root     The root view.
     * @param fontPath font file path relative to 'assets' directory.
     */
    public static void replaceFont( View root, String fontPath) {
        if (root == null || TextUtils.isEmpty(fontPath)) {
            return;
        }
        if (root instanceof TextView) { // If view is TextView or it's subclass, replace it's font
            TextView textView = (TextView) root;
            int style = Typeface.NORMAL;
            if (textView.getTypeface() != null) {
                style = textView.getTypeface().getStyle();
            }
            textView.setTypeface(createTypeface(root.getContext(), fontPath), style);
        } else if (root instanceof ViewGroup) { // If view is ViewGroup, apply this method on it's child views
            ViewGroup viewGroup = (ViewGroup) root;
            for (int i = 0; i < viewGroup.getChildCount(); ++i) {
                replaceFont(viewGroup.getChildAt(i), fontPath);
            }
        }
    }

    /**
     * <p>Replace the font of specified view and it's children</p>
     * 通过递归批量替换某个View及其子View的字体改变Activity内部控件的字体(TextView,Button,EditText,CheckBox,RadioButton等)
     *
     * @param context  The view corresponding to the activity.
     * @param fontPath font file path relative to 'assets' directory.
     */
    public static void replaceFont(Activity context, String fontPath) {
        replaceFont(getRootView(context), fontPath);
    }

    /**
     * 从Activity 获取 rootView 根节点
     *
     * @param context
     * @return 当前activity布局的根节点
     */
    public static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    /**
     * 通过改变App的系统字体替换App内部所有控件的字体(TextView,Button,EditText,CheckBox,RadioButton等)
     * <p/>
     * <style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
     * <item name="android:typeface">monospace</item>
     * </style>
     *
     * @param context
     * @param fontPath 需要修改style样式为monospace：
     */
    public static void replaceSystemDefaultFont(Context context, String fontPath) {
        replaceTypefaceField("MONOSPACE", createTypeface(context, fontPath));
    }

    /**
     * <p>Replace field in class Typeface with reflection.</p>
     */
    private static void replaceTypefaceField(String fieldName, Object value) {
        try {
            Field defaultField = Typeface.class.getDeclaredField(fieldName);
            defaultField.setAccessible(true);
            defaultField.set(null, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}
