package com.milk.tools.function.media;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.milk.tools.utils.ExternalStorage;
import com.milk.tools.utils.FileUtil;
import com.milk.tools.utils.Logger;
import com.milk.tools.utils.PackageUtil;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wiki on 16/4/28.
 */
public class MediaRecord implements MediaActions {

    public static final int VOLUME_LEVEL_ZERO = 0;

    public static final int VOLUME_LEVEL_ONE = 1;

    public static final int VOLUME_LEVEL_TWO = 2;

    public static final int VOLUME_LEVEL_THREE = 3;

    public static final int VOLUME_LEVEL_FOUR = 4;

    public static final int VOLUME_LEVEL_FIVE = 5;

    public static final int VOLUME_LEVEL_SIX = 6;

    public static final int VOLUME_LEVEL_SEVEN = 7;

    private String mFileSavePath = null;

    private MediaRecorder mMediaRecord;
    
    private MediaRecordListener mMediaRecordListener;

    //录音开始时间
    private long mStartRecordTime = 0l;
    //录音实时时间
    private long mCurrentRecordTime = 0l;

    private Timer mTimer;

    private final Handler mVoiceHandler = new Handler();

    private boolean mStartRecord = false;


    private MediaRecord(Context context,String fileSavePath){
        if(ExternalStorage.isAvailable()){
            //if sd card can use
            mFileSavePath = fileSavePath;
        }else{
            mFileSavePath = "/data/data/" + context.getPackageName() + "/cache/"+
            File.separator+System.currentTimeMillis()+".amr";
        }

        FileUtil.createFile(mFileSavePath);
        Logger.v("MediaRecord File Save Path : %s",mFileSavePath);
    }

    public static MediaRecord getMediaRecord(Context context){
        return new MediaRecord(context,configSaveString(context));
    }

    public void setMediaRecordListener(MediaRecordListener mediaRecordListener){
        if(mediaRecordListener != null){
            mMediaRecordListener = mediaRecordListener;
        }
    }
    
    private static String configSaveString(Context context){
        StringBuilder stringBuilder = new StringBuilder();
        final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        stringBuilder.append(rootPath);
        stringBuilder.append(File.separator);
        stringBuilder.append(PackageUtil.getPackageName(context));
        stringBuilder.append(File.separator);
        stringBuilder.append("media");
        stringBuilder.append(File.separator);
        stringBuilder.append(System.currentTimeMillis()+".amr");

        return stringBuilder.toString();
    }

    @Override
    public void start() {
        try {
            mMediaRecord = new MediaRecorder();
            mMediaRecord.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecord.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mMediaRecord.setOutputFile(mFileSavePath);
            mMediaRecord.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecord.prepare();
            mMediaRecord.start();
            mStartRecordTime = System.currentTimeMillis();
            mCurrentRecordTime = mStartRecordTime;
            
            mTimer = new Timer();
            mTimer.schedule(mTimerTask,TASK_INTERVAL_TIME,TASK_INTERVAL_TIME);

            updateMicStatus();

            if(mMediaRecordListener!=null){
                mMediaRecordListener.startRecord();
            }

            mStartRecord = true;
            
        }catch (IOException e){
            Logger.v("start record failed[%s]",e.getMessage());
            stop();
            if(mMediaRecordListener!=null){
                mMediaRecordListener.recordFailed();
            }
        }
    }

    public String getFileSavePath(){
        return mFileSavePath;
    }

    @Override
    public void stop() {
        if(mMediaRecord !=null && mStartRecord){
            mMediaRecord.stop();
        }
        
        if(mTimer != null){
            mTimer.cancel();
            mTimerTask.cancel();
        }

        mStartRecord = false;
    }

    @Override
    public void release() {
        if(mMediaRecord!=null){
            mMediaRecord.release();
            mMediaRecord = null;
        }
        
        if(mTimer!=null){
            mTimer = null;
        }
    }
    
    
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            updateRecordTime();
        }
    };
    
    
    public void updateRecordTime(){
        mCurrentRecordTime = System.currentTimeMillis();
        mHandler.sendMessage(mHandler.obtainMessage());
    }
    
    
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(mMediaRecordListener != null){
                mMediaRecordListener.recordTotalTime(mCurrentRecordTime,
                        mCurrentRecordTime-mStartRecordTime);
            }
        }
    };


    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };


    private void updateMicStatus() {
        if (mMediaRecord != null) {
            int ratio = mMediaRecord.getMaxAmplitude() / 600;
            int db = 0;// 分贝
            if (ratio > 1)
                db = (int) (20 * Math.log10(ratio));
            System.out.println("分贝值：" + db / 4 + "     " + Math.log10(ratio));
            switch (db / 4) {
                case 0:
                    if (null != mMediaRecordListener) {
                        mMediaRecordListener.volumeLevel(VOLUME_LEVEL_ZERO);
                    }
                    break;
                case 1:
                    if (null != mMediaRecordListener) {
                        mMediaRecordListener.volumeLevel(VOLUME_LEVEL_ONE);
                    }
                    break;
                case 2:
                    if (null != mMediaRecordListener) {
                        mMediaRecordListener.volumeLevel(VOLUME_LEVEL_TWO);
                    }
                    break;
                case 3:
                    if (null != mMediaRecordListener) {
                        mMediaRecordListener.volumeLevel(VOLUME_LEVEL_THREE);
                    }
                    break;
                case 4:
                    if (null != mMediaRecordListener) {
                        mMediaRecordListener.volumeLevel(VOLUME_LEVEL_FOUR);
                    }
                    break;
                case 5:
                    if (null != mMediaRecordListener) {
                        mMediaRecordListener.volumeLevel(VOLUME_LEVEL_FIVE);
                    }
                    break;
                case 6:
                    if (null != mMediaRecordListener) {
                        mMediaRecordListener.volumeLevel(VOLUME_LEVEL_SIX);
                    }
                    break;
                default:
                    if (null != mMediaRecordListener) {
                        mMediaRecordListener.volumeLevel(VOLUME_LEVEL_SEVEN);
                    }
                    break;
            }
            mVoiceHandler.postDelayed(mUpdateMicStatusTimer, 200);
        }
    }
}
