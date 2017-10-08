package com.milk.tools.utils;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by Administrator on 2016/3/13.
 */
public class InputMethodUtils {
    public static void open(Context context, EditText editText){
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 接受软键盘输入的编辑文本或其它视图
        inputMethodManager.showSoftInput(editText,InputMethodManager.SHOW_FORCED);
    }


    public static void close(AppCompatActivity appCompatActivity, EditText editText){
        InputMethodManager inputMethodManager = (InputMethodManager)appCompatActivity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(appCompatActivity.getCurrentFocus().getWindowToken()
                ,InputMethodManager.HIDE_NOT_ALWAYS);

    }

    public static boolean isOpen(Context context){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }
}
