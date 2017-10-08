package com.milk.tools.function.media;

/**
 * Created by wiki on 16/4/28.
 */
public interface MediaRecordListener {

    // 开始录音
    abstract void startRecord();

    // 录音失败
    abstract void recordFailed();

    abstract void recordTotalTime(long mCurrentRecordTime, long recordTime);

    abstract void stopRecord(long mTitleRecordTime, long recordTime);

    abstract void volumeLevel(int level);
}
