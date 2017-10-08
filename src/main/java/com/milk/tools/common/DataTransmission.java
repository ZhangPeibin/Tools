package com.milk.tools.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据传输工具
 * Created by wiki on 16/1/19.
 */
public class DataTransmission<T> {

    private volatile static DataTransmission dataTransmission;

    private Map<String,Object> mDataMap;

    //private constructor method to make single class
    private DataTransmission(){
        mDataMap = new ConcurrentHashMap<>();
    }


    public static DataTransmission getInstance(){
        if(dataTransmission == null){//just come at first time
            synchronized (DataTransmission.class){
                if(dataTransmission == null){
                    dataTransmission = new DataTransmission();
                }
            }
        }

        return dataTransmission;
    }


    /**
     * put a key/value to collection
     * @param key
     * @param data
     */
    public void put(String key,T data) {
        if(key == null){
            return ;
        }

        if(data == null){
            return ;
        }

        mDataMap.put(key,data);
    }

    public boolean hasKey(String key){
        if(key == null){
            return false;
        }

        return  mDataMap.containsKey(key);
    }


    /**
     * get a value with a special key
     * @param key
     * @return
     */
    public T get(String key){
        if(key == null){
            return null;
        }

        Object data = mDataMap.get(key);

        if(data != null){
            return (T)data;
        }

        return null;
    }


}
