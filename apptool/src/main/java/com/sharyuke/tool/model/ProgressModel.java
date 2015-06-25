package com.sharyuke.tool.model;

/**
 * ProgressModel
 * Created by sharyuke on 15-6-25.
 */
public class ProgressModel {
    public long progress = 0;
    public long totalLength = 1;

    public boolean isComplete() {
        return progress == totalLength;
    }

    public long getProgress() {
        return progress;
    }

    public ProgressModel setProgress(long progress) {
        this.progress = progress;
        return this;
    }

    public long getTotalLength() {
        return totalLength;
    }

    public ProgressModel setTotalLength(long totalLength) {
        this.totalLength = totalLength;
        return this;
    }

    public ProgressModel reset() {
        this.progress = 0;
        this.totalLength = 1;
        return this;
    }
}
