package com.milk.tools.utils;

import java.util.regex.Matcher;

/**
 * Created by wiki on 17/4/13.
 */

public class Pattern {

    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        try {
            java.util.regex.Pattern p = null;
            Matcher m = null;
            boolean b = false;
            p = java.util.regex.Pattern.compile("^[1][1235689]\\d{9}$"); // 验证手机号11位
            m = p.matcher(str);
            b = m.matches();
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 密码是否合格
     * 字母或数子 或字母和组合组成 大于6位
     * @param password
     * @return
     */
    public static boolean isPassWord(String password){
        if (password == null)
            return false;
        String str= "[a-zA-Z0-9]{6,16}";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(str);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    /**
     * 电话号码验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isTelephone(String str) {
        try {
            java.util.regex.Pattern p1 = null, p2 = null;
            Matcher m = null;
            boolean b = false;
            p1 = java.util.regex.Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
            p2 = java.util.regex.Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
            if (str.length() > 9) {
                m = p1.matcher(str);
                b = m.matches();
            } else {
                m = p2.matcher(str);
                b = m.matches();
            }
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 邮箱验证
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        try {
            String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(str);
            Matcher m = p.matcher(email);
            return m.matches();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isHttp(String url) {
        return url!=null && (url.startsWith("http") || url.startsWith("https"));
    }
}
