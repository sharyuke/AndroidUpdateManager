package com.sharyuke.design;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

/**
 * ProgressDialog
 * Created by sharyuke on 15-6-25.
 */
public class ProgressDialog extends Dialog {

    private static final int UNIT = 1024;
    private static final int SIZE_K = 1024;
    private static final int SIZE_M = SIZE_K * UNIT;
    private static final int SIZE_G = SIZE_M * UNIT;

    private TextView mTitleView;
    private TextView mPercentView;
    private TextView mProgressSizeView;
    private ProgressTextView mProgressBarView;

    private CharSequence title;
    private int progressLength = 0;
    private int totalLength = 1;

    public CharSequence getTitle() {
        return title;
    }

    public ProgressDialog setProgressTitle(CharSequence title) {
        this.title = title;
        return this;
    }

    public int getProgressLength() {
        return progressLength;
    }

    public ProgressDialog setProgressLength(int progressLength) {
        this.progressLength = progressLength;
        updateView();
        return this;
    }

    public int getTotalLength() {
        return totalLength;
    }

    public ProgressDialog setTotalLength(int totalLength) {
        this.totalLength = totalLength;
        return this;
    }

    public ProgressDialog(Context context) {
        this(context, 0);
    }

    public ProgressDialog(Context context, int theme) {
        super(context, theme);
        setContentView(R.layout.layout_progress_dialog);
        mTitleView = (TextView) findViewById(R.id.progress_title);
        mPercentView = (TextView) findViewById(R.id.progress_percent);
        mProgressSizeView = (TextView) findViewById(R.id.progress_length);
        mProgressBarView = (ProgressTextView) findViewById(R.id.progress_bar);
        updateView();
    }

    private void updateView() {
        mTitleView.setText(title);
        mPercentView.setText(getPercent());
        mProgressSizeView.setText(getProgressTips());
        mProgressBarView.setTotal(totalLength);
        mProgressBarView.setProgress(progressLength);
    }

    private String getPercent() {
        return String.format("%.2f%%", ((float) progressLength * 100) / totalLength);
    }

    private String getProgressTips() {
        return String.format("%s/%s", getSize(progressLength), getSize(totalLength));
    }

    private String getSize(long length) {
        if (length > SIZE_G) {
            return String.format("%.2fGb", ((float) length) / SIZE_G);
        } else if (length > SIZE_M) {
            return String.format("%.2fMb", ((float) length) / SIZE_M);
        } else if (length > SIZE_K) {
            return String.format("%.2fKb", ((float) length) / SIZE_K);
        } else {
            return String.valueOf(length) + "byte";
        }
    }
}
