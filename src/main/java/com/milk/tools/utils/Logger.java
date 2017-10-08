package com.milk.tools.utils;

import android.os.Environment;
import android.util.Log;

import org.apache.log4j.Level;

import java.io.File;
import java.util.Locale;

import de.mindpipe.android.logging.log4j.LogConfigurator;


/**
 * Created by wiki on 15-5-5.
 */
public class Logger {

    private static org.apache.log4j.Logger mLogger;

    private static final int MAX_FILE_SIZE = 1024 * 1024 * 50;


    static {

        final LogConfigurator logConfigurator = new LogConfigurator();

        logConfigurator.setFileName(Environment.getExternalStorageDirectory() + File.separator + "mogu.log");
        // Set the root log level
        logConfigurator.setRootLevel(Level.INFO);
        // Set log level of a specific logger
        logConfigurator.setImmediateFlush(true);
        logConfigurator.setUseFileAppender(true);
        logConfigurator.setUseLogCatAppender(true);
        logConfigurator.setMaxBackupSize(10);
        logConfigurator.setMaxFileSize(MAX_FILE_SIZE);
        logConfigurator.configure();

        mLogger = org.apache.log4j.Logger.getLogger(Logger.class);
    }

    public static void setLoggerFileName(String name){
        LogConfigurator logConfigurator = new LogConfigurator(name); // Set the root log level
        logConfigurator.setRootLevel(Level.INFO);
        // Set log level of a specific logger
        logConfigurator.setImmediateFlush(true);
        logConfigurator.setUseFileAppender(true);
        logConfigurator.setUseLogCatAppender(true);
        logConfigurator.configure();
        mLogger = org.apache.log4j.Logger.getLogger(Logger.class);
    }

    /**
     * charge is logging to local
     */
    private static boolean LOGGING_TO_LOCAL = false;

    /**
     * the Tag for Log
     */
    private static String TAG = "Logger";

    /**
     * charge is logging while debug.
     */
    public static boolean DEBUG = Log.isLoggable(TAG, Log.INFO);

    /**
     * customize the log's tag for your Application
     *
     * @param tag <br/>
     *            Enable your log level before you start your Application
     *            <br/>
     *            {@code Adb shell setprop  log.tag.&lt;tag&gt;}
     */
    public static void setTag(String tag) {
        if (tag == null) {
            return;
        }

        if (tag.length() == 0) {
            return;
        }

        d("change tag to %s", tag);

        TAG = tag;

        DEBUG = Log.isLoggable(TAG, Log.INFO);
    }


    public static void toggle(boolean loggingToLocal){
        LOGGING_TO_LOCAL = loggingToLocal;
    }

    public static void v(String format, Object... args) {
        Log.v(TAG, buildMessage(format, args));
    }

    public static void d(String format, Object... args) {
        try {
            if (DEBUG)
                Log.d(TAG, buildMessage(format, args));
        }catch (Exception e){
        }
    }

    public static void d(String format){
        if ( DEBUG ){
            if ( format != null ){
                logToLocal(format);
                Log.d(TAG,format);
            }
        }
    }

    public static void i(String format, Object... args) {
        Log.i(TAG, buildMessage(format, args));
    }

    public static void e(String format, Object... args) {
        Log.e(TAG, buildMessage(format, args));
    }

    public static void e(String message){
        if ( message!=null ) {
            Log.d(TAG, message);
        }else{
            Log.e(TAG, "println needs a message");
        }
    }

    /**
     * build your own message with this format
     *
     * @param format
     * @param args
     * @return format String
     */
    private static String buildMessage(String format, Object... args) {
        try {
            String msg = (args == null) ? format : String.format(Locale.CHINA, format, args);

            //get current invoking method and class stack
            //it looks like a stack
            StackTraceElement[] stackTraceElements = new Throwable().fillInStackTrace().getStackTrace();

            String caller = "<unknown>";

            for (int i = 2; i < stackTraceElements.length; i++) {
                Class clz = stackTraceElements[i].getClass();
                if (!clz.equals(Logger.class)) {//if current level class not equal Logger.getClass
                    String callingClass = stackTraceElements[i].getClassName();
                    callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                    callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);

                    caller = callingClass + "." + stackTraceElements[i].getMethodName() +
                            "<" + stackTraceElements[i].getLineNumber() + ">";
                    break;
                }
            }

            String s = String.format(Locale.CHINA, "[%d]%s:%s",
                    Thread.currentThread().getId(), caller, msg);
            return s;
        }catch (Exception e){
            return format;
        }
    }


    public static void logToLocal(String s){
        if ( LOGGING_TO_LOCAL ){
            if ( mLogger == null )
                mLogger = org.apache.log4j.Logger.getLogger(Logger.class);

            if ( s.length()  < 1024 * 1024 * 2 ){
                mLogger.info(s);
            }
        }
    }

}
