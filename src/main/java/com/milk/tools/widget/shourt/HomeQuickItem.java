package com.milk.tools.widget.shourt;

import java.io.Serializable;

/**
 * 首页快捷方式的实体类
 * Created by Administrator on 2015/12/2.
 */
public class HomeQuickItem implements Serializable {

    /**
     * ico : http://app.jingu58.com/app/ico/tzcl.png
     * name : 投资策略
     * action : location:tzcl
     */
    private String ico;
    private String name;
    private String action;
    private String ios2x;
    private String android;

    public String getAndroid() {
        return android;
    }

    public void setAndroid(String android) {
        this.android = android;
    }

    public String getIos2x() {
        return ios2x;
    }

    public void setIos2x(String ios2x) {
        this.ios2x = ios2x;
    }

    public void setIco(String ico) {
        this.ico = ico;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getIco() {
        return ico;
    }

    public String getName() {
        return name;
    }

    public String getAction() {
        return action;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof HomeQuickItem){
            HomeQuickItem homeQuickItem = (HomeQuickItem) o;
            final boolean isIconEqual = isStringEqual(ico,homeQuickItem.getIco());
            final boolean isNameEqual = isStringEqual(name,homeQuickItem.getName());
            final boolean isActionEqual = isStringEqual(action,homeQuickItem.getAction());
            return isIconEqual && isNameEqual && isActionEqual;
        }
        return false;
    }

    private boolean isStringEqual(String s,String s1) {
        if (s == null && s1 == null) {
            return true;
        }
        return !(s == null) && !(s1 == null) && s.equals(s1);
    }

    @Override
    public String toString() {
        return "HomeQuickItem{" +
                "ico='" + ico + '\'' +
                ", name='" + name + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
