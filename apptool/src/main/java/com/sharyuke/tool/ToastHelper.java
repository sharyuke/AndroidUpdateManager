package com.sharyuke.tool;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class ToastHelper {

    private static ToastHelper instance;

    private final Handler handler = new Handler();
    private final Context context;
    private Toast toast;
    private WindowManager.LayoutParams mParams;

    class CancelRunnable implements Runnable {
        final Toast toast;

        CancelRunnable(Toast toast) {
            this.toast = toast;
        }

        @Override
        public void run() {
            toast.cancel();
        }
    }

    public static ToastHelper get(Context context) {
        if (instance == null) {
            synchronized (ToastHelper.class) {
                instance = new ToastHelper(context);
            }
        }
        return instance;
    }

    public void showShort(int resId) {
        show(context.getText(resId), Toast.LENGTH_SHORT);
    }

    public void showShort(CharSequence text) {
        show(text, Toast.LENGTH_SHORT);
    }

    public void showLong(int resId) {
        show(context.getText(resId), Toast.LENGTH_LONG);
    }

    public void showLong(CharSequence text) {
        show(text, Toast.LENGTH_LONG);
    }

    public void show(int resId, int duration) {
        show(context.getText(resId), duration);
    }

    public void show(CharSequence text, int duration) {
        if (toast != null) {
            handler.post(new CancelRunnable(toast));
        }
        toast = new Toast(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.toast_bg, null);
        toast.setView(layout);
        TextView textView = (TextView) layout.findViewById(R.id.toast_text);
        textView.setText(text);
        toast.show();
    }

    private ToastHelper(Context context) {
        this.context = context;
    }

}
