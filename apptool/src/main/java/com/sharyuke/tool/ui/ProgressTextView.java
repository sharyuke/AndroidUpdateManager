package com.sharyuke.tool.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sharyuke.tool.R;

/**
 * ProgressTextView
 * Created by sharyuke on 15-6-11.
 */
public class ProgressTextView extends TextView {

    private long total;
    private long progress;

    private Paint paint = new Paint();

    public ProgressTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(context.getResources().getColor(R.color.debug_progress_bg_color));
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        double p = (double) progress / (double) total;
        float border = getResources().getDimension(R.dimen.progress_bar_border);
        canvas.drawRect(border, border, (float) (p * getWidth()) - 2 * border, getHeight() - 2 * border, paint);
    }
}
