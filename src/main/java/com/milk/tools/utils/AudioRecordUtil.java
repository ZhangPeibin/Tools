package com.milk.tools.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/3/15.
 */
public class AudioRecordUtil {

    private static final String TAG = AudioRecordUtil.class.getSimpleName();

    public static final int VOLUME_LEVEL_ZERO = 0;

    public static final int VOLUME_LEVEL_ONE = 1;

    public static final int VOLUME_LEVEL_TWO = 2;

    public static final int VOLUME_LEVEL_THREE = 3;

    public static final int VOLUME_LEVEL_FOUR = 4;

    public static final int VOLUME_LEVEL_FIVE = 5;

    public static final int VOLUME_LEVEL_SIX = 6;

    public static final int VOLUME_LEVEL_SEVEN = 7;

    private static final int TASK_INTERVAL_TIME = 1000;

    private static final int BASE = 600;

    private static final int SPACE = 200;// 间隔取样时间

    public static final int START_RECORD = 0, STOP_RECORD = 1, PLAY_START = 2, PLAY_STOP = 3;

    public static final int MAX_RECORD_TIME_LEN = 60;

    public static final String RECORD_INIT_TIME = "0'/60'";

    private static final String RECORD_TIME_FORMAT = "%02d:%02d";// 格式化时间

    public static final String STATUS_RECORD_VOICE_FILE_NAME = "FileUtils.BASE_VOICE_DISK_DIR"
            + "iworker_status_record_voice.amr";// 帖子任务录音临时文件名

    public static final String MESSAGE_RECORD_VOICE_FILE_NAME = "FileUtils.BASE_VOICE_DISK_DIR";

    public static final String MESSAGE_RECORD_VOICE = "iworker_msg_record_voice.amr";// 帖子任务录音临时文件名

    private MediaPlayer mPlayer = null;

    private MediaRecorder mRecorder = null;

    private MediaPlayListner mAudioPlayListner;

    private int state;// 0：录音 2：播放

    private int mCurrentTime;

    private int mMddiaTotalTime;

    private Timer timer;

    private Context mContext;

    private boolean isMessageRecord;

    private final Handler mVoiceHandler = new Handler();
    private String voiceFile;

    public AudioRecordUtil(Context mContext, MediaPlayListner mAudioPlayListner) {
        this.mAudioPlayListner = mAudioPlayListner;
        this.mContext = mContext;
    }

    public AudioRecordUtil(Context mContext, MediaPlayListner mAudioPlayListner, boolean isMsgRecord) {
        this(mContext, mAudioPlayListner);
        this.isMessageRecord = isMsgRecord;
    }

    // 开始录音
    public String startRecord() {
        try {
            File mFile = new File("FileUtils.BASE_VOICE_DISK_DIR");
            if (!mFile.exists()) {
                mFile.mkdirs();
            }
            voiceFile = MESSAGE_RECORD_VOICE_FILE_NAME + (System.currentTimeMillis() + "") + MESSAGE_RECORD_VOICE;
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置音频的来源，来着麦克风
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);// 设置输出音频格式
            mRecorder.setOutputFile(isMessageRecord ? voiceFile : STATUS_RECORD_VOICE_FILE_NAME);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.prepare();
            mRecorder.start();
            updateMicStatus();
            if (null != mAudioPlayListner) {
                mAudioPlayListner.StartRecord();
            }
            timer = new Timer();
            state = START_RECORD;
            mCurrentTime = 0;// 记录录音时间
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    taskPlay();
                }
            }, TASK_INTERVAL_TIME, TASK_INTERVAL_TIME);
        } catch (Exception e) {
            if (null != mAudioPlayListner) {
                mAudioPlayListner.RecordFailed();
            }
            //VoiceUtils
            //        .deleteVoiceFile(isMessageRecord ? voiceFile : STATUS_RECORD_VOICE_FILE_NAME);// 删除录音
            stopRecord();
            Log.e(TAG, e.toString(), e);
        }
        return voiceFile;
    }

    // 停止录音
    public void stopRecord() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (mRecorder != null) {
            if (null != mAudioPlayListner) {
                mMddiaTotalTime = mCurrentTime;
                // 停止录音 时间格式：00/40
                mAudioPlayListner.StopRecord(mMddiaTotalTime, getFormatTime(0) + "/" + getFormatTime(mMddiaTotalTime));
            }

            try {
                mRecorder.stop();
            } catch (IllegalStateException e) {
                mRecorder.release();
                mRecorder = null;
            }
        }

    }

    // 播放录音
    public void startPlay(String fileName) {
        if (StringUtil.empty(fileName)) {
            Logger.v(TAG,"voice file's name is empty");
            return;
        }
        try {
            mPlayer = new MediaPlayer();
            File file = new File(fileName);
            FileInputStream fis = new FileInputStream(file);
            mPlayer.setDataSource(fis.getFD());
            mPlayer.prepare();
            mPlayer.start();
            if (null != mAudioPlayListner) {
                mAudioPlayListner.StartPlay();
            }
            mPlayer.setOnCompletionListener(mCompletionListener);
            // 实时获得当前播放时间
            timer = new Timer();
            state = PLAY_START;
            mCurrentTime = 0;// 重置记录播放时间
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    taskPlay();
                }
            }, TASK_INTERVAL_TIME, TASK_INTERVAL_TIME);
        } catch (Exception e) {
            if (null != mAudioPlayListner) {
                mAudioPlayListner.PlayFailed();
            }
            //VoiceUtils
            //        .deleteVoiceFile(isMessageRecord ? MESSAGE_RECORD_VOICE_FILE_NAME : STATUS_RECORD_VOICE_FILE_NAME);// 删除录音
            stopPlay();
            Log.e(TAG, e.toString(), e);
        }
    }

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (null != mAudioPlayListner) {
                mAudioPlayListner.stopPlay();
                mAudioPlayListner
                        .PlayCurrentTime(mCurrentTime, getFormatTime(0) + "/" + getFormatTime(mMddiaTotalTime));
            }
            if (null != mp) {
                mp.release();
                mp = null;
            }
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }
    };

    private void taskPlay() {
        ++mCurrentTime;
        Log.d(TAG, "----" + mCurrentTime);
        mHandler.sendEmptyMessage(state);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (state == PLAY_START) {// 播放的处理
                if (null != mAudioPlayListner) {
                    mAudioPlayListner.PlayCurrentTime(mCurrentTime, getFormatTime(mCurrentTime) + "/"
                            + getFormatTime(mMddiaTotalTime));
                }
            } else {// 录音的处理
                if (null != mAudioPlayListner) {
                    mAudioPlayListner.RecordTotalTime(mCurrentTime, mCurrentTime + "'/" + "60'");
                }
                if (MAX_RECORD_TIME_LEN == mCurrentTime) {
                    stopRecord();
                }
            }

        }
    };

    public void stopPlay() {
        if (null != mPlayer) {
            if (null != mAudioPlayListner) {
                mAudioPlayListner.stopPlay();
            }
            mPlayer.release();
            mPlayer = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

    }

    // 获取录音的长度
    public int getTotalTime() {
        return mMddiaTotalTime;
    }

    // 设置录音的长度
    public void setTotalTime(int tatalTime) {
        this.mMddiaTotalTime = tatalTime;
    }

    // 格式化时间
    public static String getFormatTime(int time) {
        Object[] sendData = new Object[2];
        sendData[0] = Integer.valueOf(0);
        sendData[1] = Integer.valueOf(time);
        return String.format(RECORD_TIME_FORMAT, sendData);
    }

    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    private void updateMicStatus() {
        if (mRecorder != null) {
            int ratio = mRecorder.getMaxAmplitude() / BASE;
            int db = 0;// 分贝
            if (ratio > 1)
                db = (int) (20 * Math.log10(ratio));
            System.out.println("分贝值：" + db / 4 + "     " + Math.log10(ratio));
            switch (db / 4) {
                case 0:
                    if (null != mAudioPlayListner) {
                        mAudioPlayListner.volumeLevel(VOLUME_LEVEL_ZERO);
                    }
                    break;
                case 1:
                    if (null != mAudioPlayListner) {
                        mAudioPlayListner.volumeLevel(VOLUME_LEVEL_ONE);
                    }
                    break;
                case 2:
                    if (null != mAudioPlayListner) {
                        mAudioPlayListner.volumeLevel(VOLUME_LEVEL_TWO);
                    }
                    break;
                case 3:
                    if (null != mAudioPlayListner) {
                        mAudioPlayListner.volumeLevel(VOLUME_LEVEL_THREE);
                    }
                    break;
                case 4:
                    if (null != mAudioPlayListner) {
                        mAudioPlayListner.volumeLevel(VOLUME_LEVEL_FOUR);
                    }
                    break;
                case 5:
                    if (null != mAudioPlayListner) {
                        mAudioPlayListner.volumeLevel(VOLUME_LEVEL_FIVE);
                    }
                    break;
                case 6:
                    if (null != mAudioPlayListner) {
                        mAudioPlayListner.volumeLevel(VOLUME_LEVEL_SIX);
                    }
                    break;
                default:
                    if (null != mAudioPlayListner) {
                        mAudioPlayListner.volumeLevel(VOLUME_LEVEL_SEVEN);
                    }
                    break;
            }
            mVoiceHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    public interface MediaPlayListner {

        // 开始录音
        abstract void StartRecord();

        // 录音失败
        abstract void RecordFailed();

        // 当前录制的时间
        abstract void RecordTotalTime(int mCurrentRecordTime, String recordTime);

        // 私信录音时用到
        abstract void StopRecord(int mTitleRecordTime, String recordTime);

        // 开始播放
        abstract void StartPlay();

        // 播放失败
        abstract void PlayFailed();

        // 当前播放的时间
        abstract void PlayCurrentTime(int mPlayPosTime, String currentPosTime);

        // 当完成录音播放后！按钮状态的改变

        abstract void stopPlay();

        abstract void volumeLevel(int level);

    }

    public void release() {
        mPlayer = null;
        mRecorder = null;
        timer = null;
        mContext = null;
    }
}
