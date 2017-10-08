package com.milk.tools.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.view.ContextThemeWrapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/14.
 */

public class SharedPreferenceUtil<T> {

    public static int MODE_PUT = 1;
    public static int MODE_GET = 2;

    private Context mContext;
    private int mType;
    private String mName;
    private String mKey;
    private T mValue;
    private Class<T> mTClass;

    public static class Builder<T> {

        private Context mContext;
        private int mType;
        private String mName;
        private String mKey;
        private T mValue;
        private Class<T> mTClass;

        public Builder mode (int mode) {
            this.mType = mode;
            return this;
        }

        public Builder with (Context context) {
            this.mContext = context;
            return this;
        }

        public Builder name (String name) {
            this.mName = name;
            return this;
        }

        public Builder key (String key) {
            this.mKey = key;
            return this;
        }

        public Builder value (T t) {
            this.mValue = t;
            return this;
        }

        public Builder type (Class<T> type) {
            this.mTClass = type;
            return this;
        }

        public SharedPreferenceUtil build () {
            SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil();
            sharedPreferenceUtil.mName = mName;
            sharedPreferenceUtil.mKey = mKey;
            sharedPreferenceUtil.mValue = mValue;
            sharedPreferenceUtil.mType = mType;
            sharedPreferenceUtil.mContext = mContext;
            sharedPreferenceUtil.mTClass = mTClass;
            return sharedPreferenceUtil;
        }
    }


    public Object process () {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mName, Context.MODE_APPEND |
                Context.MODE_MULTI_PROCESS);

        if ( mType == MODE_GET ) {
            if ( String.class.getName().equals(mTClass.getName()) ) {
                return sharedPreferences.getString(mKey, null);
            } else if ( int.class.getName().equals(mTClass.getName()) ) {
                return sharedPreferences.getInt(mKey, 0);
            } else if ( boolean.class.getName().equals(mTClass.getName()) ) {
                return sharedPreferences.getBoolean(mKey, false);
            } else if ( long.class.getName().equals(mTClass.getName()) ) {
                return sharedPreferences.getLong(mKey, 0L);
            }
        } else if ( mType == MODE_PUT ) {
            if ( mValue instanceof String ) {
                Logger.v("要保存的值的类型为[%s],值为[%s]", "String", mValue);
                sharedPreferences.edit().putString(mKey, (String) mValue).commit();
                return mValue;
            } else if ( mValue instanceof Integer ) {
                Logger.v("要保存的值的类型为[%s],值为[%d]", "int", mValue);
                sharedPreferences.edit().putInt(mKey, (Integer) mValue).commit();
                return mValue;
            } else if ( mValue instanceof Boolean ) {
                Logger.v("要保存的值的类型为[%s],值为[%s]", "boolean", mValue + "");
                sharedPreferences.edit().putBoolean(mKey, (Boolean) mValue).commit();
                return mValue;
            } else if ( mValue instanceof Long ) {
                sharedPreferences.edit().putLong(mKey, (Long) mValue).commit();
                return mValue;
            }
        }
        return "unknow Type";
    }


    /**
     * 注册监听
     *
     * @param context
     * @param name
     * @param onSharedPreferenceChangeListener
     * @return
     */
    public static SharedPreferences register (Context context, String name,
                                              SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name,
                Context.MODE_APPEND | Context.MODE_MULTI_PROCESS);
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        return sharedPreferences;
    }


    public static String getString (Context context, String name, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_APPEND | Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getString(key, null);
    }

    public static void setString (Context context, String name, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_APPEND | Context.MODE_MULTI_PROCESS);
        sharedPreferences.edit().putString(key, value).commit();
    }


    public static int getInt (Context context, String name, String key, int defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_APPEND | Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static void setInt (Context context, String name, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_APPEND | Context.MODE_MULTI_PROCESS);
        sharedPreferences.edit().putInt(key, value).commit();
    }

    public static long getLong (Context context, String name, String key, long defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_APPEND | Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getLong(key, defaultValue);
    }

    public static void setLong (Context context, String name, String key, long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_APPEND | Context.MODE_MULTI_PROCESS);
        sharedPreferences.edit().putLong(key, value).commit();
    }

    public static boolean getBoolean (Context context, String name, String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_APPEND | Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static void setBoolean (Context context, String name, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_APPEND | Context.MODE_MULTI_PROCESS);
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    /**
     * 根据对应的key获取本地的缓存数据,如果缓存不存在
     * 则返回空
     *
     * @param context 上下文对象,用来获取缓存目录
     * @param key     文件缓存后的文件名称
     * @throws Exception 缓存过程中的异常
     */
    public static String readCache (Context context, String key) {
        FileInputStream ins = null;
        try {
            File file = new File(context.getExternalFilesDir(null), key);
            ins = new FileInputStream(file);
            int countLen = ins.available();
            byte[] bytes = new byte[countLen];
            int readBytes = ins.read(bytes);
            if ( readBytes == countLen ) {
            }
            return new String(bytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if ( ins != null ) {
                try {
                    ins.close();
                } catch (IOException e) {
                }
            }
        }
        return "";
    }


    /**
     * 根据对应的key获取本地的缓存数据,如果缓存不存在
     * 则返回空
     *
     * @param context 上下文对象,用来获取缓存目录
     * @param key     文件缓存后的文件名称
     * @throws Exception 缓存过程中的异常
     */
    public static void writeCache (Context context, String key,String text) {
        FileOutputStream fos = null;
        try {
            File file = new File(context.getExternalFilesDir(null), key);
            if ( !file.exists() ) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            byte[] buffer = text.getBytes("UTF-8");
            fos = new FileOutputStream(file);
            fos.write(buffer);
            fos.flush();
            fos.close();
        } catch (Exception e) {
        } finally {
            if ( fos != null ) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 缓存数据到本地文件中
     *
     * @param key  文件缓存后的文件名称
     * @param text 所需要缓存的内容
     * @throws Exception 缓存过程中的异常
     */
    public static void writeCache (String dirName, String key, String text) {
        FileOutputStream fos = null;
        try {
            String externalDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            externalDir = externalDir + "/" + dirName;
            File file = new File(externalDir, key);
            if ( !file.exists() ) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            byte[] buffer = text.getBytes("UTF-8");
            fos = new FileOutputStream(file);
            fos.write(buffer);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if ( fos != null ) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据对应的key获取本地的缓存数据,如果缓存不存在
     * 则返回空
     *
     * @param key 文件缓存后的文件名称
     * @throws Exception 缓存过程中的异常
     */
    public static String readCache (String dirName, String key) {
        FileInputStream ins = null;
        try {
            String externalDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            externalDir = externalDir + "/" + dirName;
            File file = new File(externalDir, key);
            ins = new FileInputStream(file);
            int countLen = ins.available();
            byte[] bytes = new byte[countLen];
            int readBytes = ins.read(bytes);
            if ( readBytes == countLen ) {
                Log.v("readCache", "readCache Success");
            }
            return new String(bytes, "UTF-8");
        } catch (Exception e) {
        } finally {
            if ( ins != null ) {
                try {
                    ins.close();
                } catch (IOException e) {
                }
            }
        }
        return "";
    }


    /**
     * 删除指定的文件
     *
     * @param dirName
     * @param key
     * @return
     */
    public static boolean removeFile (String dirName, String key) {
        String externalDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        externalDir = externalDir + "/" + dirName;
        File f = new File(externalDir, key);
        return f.exists();
    }


    /**
     * 缓存数据到本地文件中
     *
     * @param key  文件缓存后的文件名称
     * @param text 所需要缓存的内容
     * @throws Exception 缓存过程中的异常
     */
    public static void writeMultiCache (String dirName, String key, String text) {
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter fos = null;
        try {
            String externalDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            externalDir = externalDir + "/" + dirName;
            File file = new File(externalDir, key);
            if ( !file.exists() ) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file, true);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            fos = new BufferedWriter(outputStreamWriter);
            fos.newLine();
            fos.write(text);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if ( fos != null )
                    fos.close();

                if ( outputStreamWriter != null )
                    outputStreamWriter.close();

                if ( fileOutputStream != null )
                    fileOutputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 根据对应的key获取本地的缓存数据,如果缓存不存在
     * 则返回空
     *
     * @param key 文件缓存后的文件名称
     * @throws Exception 缓存过程中的异常
     */
    public static List<String> readMultiCache (String dirName, String key) {
        BufferedReader bufferedReader = null;
        FileInputStream ins = null;
        InputStreamReader inputStreamReader = null;
        try {
            String externalDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            externalDir = externalDir + "/" + dirName;
            File file = new File(externalDir, key);
            if ( !file.exists() ) {
                return null;
            }
            ins = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(ins);
            bufferedReader = new BufferedReader(inputStreamReader);
            List<String> strings = new ArrayList<>();
            String line = null;
            while ( (line = bufferedReader.readLine()) != null ) {
                strings.add(line);
            }
            return strings;
        } catch (Exception e) {
        } finally {
            try {
                if ( bufferedReader != null ){
                    bufferedReader.close();
                }
                if ( inputStreamReader != null ){
                    inputStreamReader.close();
                }

                if ( ins != null ) {
                    ins.close();
                }
            } catch (IOException e) {
            }
        }
        return null;
    }
}
