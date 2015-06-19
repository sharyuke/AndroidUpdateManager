package com.sharyuke.tool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.otto.Bus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit.RetrofitError;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class UpdateManager {
    private static final String UPDATE_SAVE_NAME = "Passionlife.apk";

    private static final String RES_404 = "404";

    private static UpdateManager mUpdateManager;

    protected CompositeSubscription subscription = new CompositeSubscription();

    private Observable<? extends ResUpdateModel> updateApi;

    private String downloadUrl;

    private int versionCode;

    private Activity activity;

    private ProgressDialog updateProgressDialog;

    private OkHttpClient client;

    private Bus bus;

    private UpdateStatus updateStatus;

    private Dialog askDialog;

    public static UpdateManager getInstance() {
        synchronized (UpdateManager.class) {
            if (mUpdateManager == null) {
                mUpdateManager = new UpdateManager();
            }
        }
        return mUpdateManager;
    }

    private UpdateManager() {
        client = new OkHttpClient();
        bus = new Bus();
    }

    private Status status = Status.NORMAL;

    public enum Status {
        NORMAL,
        CHECKING,
        DOWNLOADING,
        ASKING,
    }

    public Bus getBus() {
        return bus;
    }

    public Status getStatus() {
        return status;
    }

    public UpdateManager initUpdateManager(Observable<? extends ResUpdateModel> updateApi, String downloadUrl, int versionCode) {
        setUpdateApi(updateApi).setDownloadUrl(downloadUrl).setVersionCode(versionCode);
        return this;
    }

    public UpdateManager setUpdateApi(Observable<? extends ResUpdateModel> updateApi) {
        this.updateApi = updateApi;
        return this;
    }

    public UpdateManager setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
        return this;
    }

    public UpdateManager setVersionCode(int versionCode) {
        this.versionCode = versionCode;
        return this;
    }

    private void initProgressDialog() {
        updateProgressDialog = new ProgressDialog(
                activity);
        updateProgressDialog
                .setMessage(activity.getText(R.string.dialog_downloading_msg));
        updateProgressDialog.setIndeterminate(false);
        updateProgressDialog
                .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        updateProgressDialog.setMax(100);
        updateProgressDialog.setProgress(0);
        updateProgressDialog.show();
        status = Status.DOWNLOADING;
        updateStatus();
    }

    private void updateStatus() {
        if (updateStatus != null) {
            updateStatus.onStatusChanged(status);
        }

    }

    public void checkUpdate(Activity activity) {
        checkUpdate(activity, false);
    }

    public void checkUpdate(Activity activity, boolean isSaliently) {
        synchronized (this) {
            this.activity = activity;
            switch (status) {
                case NORMAL:
                    status = Status.CHECKING;
                    updateStatus();
                    subscription.add(updateApi
                            .doOnError(throwable -> status = Status.NORMAL)
                            .doOnError(throwable1 -> updateStatus())
                            .doOnError((throwable2) -> handleError(throwable2, isSaliently))
                            .onErrorResumeNext(Observable.empty())
                            .subscribe((updateResModel) -> versionInfo(updateResModel, isSaliently)));
                    break;
                case CHECKING:
                    break;
                case DOWNLOADING:
                    updateProgressDialog.show();
                    break;
                case ASKING:
                    break;
            }
        }
    }

    private void handleError(Throwable throwable, boolean isSaliently) {
        if (isSaliently) return;
        if (throwable instanceof RetrofitError) {
            RetrofitError cause = (RetrofitError) throwable;
            switch (cause.getKind()) {
                case NETWORK:
                    ToastHelper.get(activity).showShort(R.string.toast_connect_error);
                    break;
                case CONVERSION:
                    break;
                case HTTP:
                    break;
                case UNEXPECTED:
                    break;
            }
        }
    }

    private void download(Activity activity, String url) {
        this.activity = activity;
        switch (status) {
            case NORMAL:
                break;
            case CHECKING:
                return;
            case DOWNLOADING:
                updateProgressDialog.show();
                return;
            case ASKING:
                break;
            default:
                return;
        }
        initProgressDialog();
        subscription.add(get(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(downLoadProgress -> {
                    updateProgressDialog.setMax((int) downLoadProgress.getTotalLength());
                    updateProgressDialog.setProgress((int) downLoadProgress.getProgress());
                    bus.post(downLoadProgress);
                })
                .doOnError(throwable -> {
                    updateProgressDialog.dismiss();
                    String tips = RES_404.equals(throwable.getMessage()) ? activity.getString(R.string.toast_file_not_exist) : activity.getString(R.string.toast_download_connect_error);
                    ToastHelper.get(activity).showShort(tips);
                    Timber.e(throwable, "---->>");
                    status = Status.NORMAL;
                    updateStatus();
                })
                .onErrorResumeNext(Observable.empty())
                .subscribe(downLoadProgress -> {
                    if (downLoadProgress.isComplete()) {
                        updateProgressDialog.dismiss();
                        update();
                    }
                }));
    }

    private void versionInfo(ResUpdateModel resCheckVersion, boolean isSaliently) {
        status = Status.ASKING;
        updateStatus();
        if (resCheckVersion.getVersionCode() > versionCode) {
            askDialog = new AlertDialog.Builder(activity)
                    .setTitle(R.string.dialog_update_title)
                    .setMessage(activity.getString(R.string.dialog_update_msg, resCheckVersion.getVersionName()))
                    .setPositiveButton(R.string.dialog_update_btnupdate, (dialog, which) -> {
                        download(activity, downloadUrl);
                    })
                    .setNegativeButton(R.string.dialog_update_btnnext, (dialog, which) -> {
                        activity.finish();
                        status = Status.NORMAL;
                    })
                    .setOnCancelListener(dialog -> status = Status.NORMAL)
                    .show();
        } else if (!isSaliently) {
            ToastHelper.get(activity).showShort(R.string.toast_last_version);
        }
    }

    public void downLoadDebug(Activity activity, String url) {
        download(activity, url);
    }

    public void cancelDownLoad() {
        reset();
        subscription.clear();
    }

    private void reset() {
        status = Status.NORMAL;
        updateStatus();
        bus.post(new DownLoadProgress());
    }

    private void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(
                Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/" + UPDATE_SAVE_NAME)),
                "application/vnd.android.package-archive");
        activity.startActivity(intent);
        reset();
    }

    public static class DownLoadProgress {
        public long progress = 0;
        public long totalLength = 1;

        public boolean isComplete() {
            return progress == totalLength;
        }

        public long getProgress() {
            return progress;
        }

        public DownLoadProgress setProgress(long progress) {
            this.progress = progress;
            return this;
        }

        public long getTotalLength() {
            return totalLength;
        }

        public DownLoadProgress setTotalLength(long totalLength) {
            this.totalLength = totalLength;
            return this;
        }

        public DownLoadProgress reset() {
            this.progress = 0;
            this.totalLength = 1;
            return this;
        }
    }


    public Observable<DownLoadProgress> get(String url) {
        return Observable.create(subscriber -> {
            Request request = new Request.Builder().url(url).build();
            Response response = null;
            File apkFile = new File(Environment.getExternalStorageDirectory() + "/" + UPDATE_SAVE_NAME);
            if (apkFile.exists() && apkFile.delete()) {
                Timber.d("delete exist apk file...");
            }
            FileOutputStream fos = null;
            InputStream inputStream = null;
            UpdateManager.DownLoadProgress downLoadProgress = new UpdateManager.DownLoadProgress();
            try {
                fos = new FileOutputStream(apkFile);
                response = client.newCall(request).execute();

                if (response.code() == 200) {
                    inputStream = response.body().byteStream();
                    byte[] buff = new byte[1024 * 4];
                    long downloaded = 0;
                    long target = response.body().contentLength();
                    downLoadProgress.setTotalLength(target);
                    subscriber.onStart();
                    subscriber.onNext(downLoadProgress.setProgress(0));
                    int hasRead = 0;
                    long lastUpdate = System.currentTimeMillis();
                    while ((hasRead = inputStream.read(buff)) != -1) {
                        downloaded += hasRead;
                        fos.write(buff, 0, hasRead);

                        if (System.currentTimeMillis() - lastUpdate > 100) {
                            lastUpdate = System.currentTimeMillis();
                            subscriber.onNext(downLoadProgress.setProgress(downloaded));
                        }
                    }
                    subscriber.onNext(downLoadProgress.setProgress(target));
                    subscriber.onCompleted();
                } else if (response.code() == 404) {
                    subscriber.onError(new IOException(RES_404));
                } else {
                    subscriber.onError(new IOException());
                }
            } catch (IOException e) {
                e.printStackTrace();
                subscriber.onError(e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public UpdateManager setUpdateStatus(UpdateStatus updateStatus) {
        this.updateStatus = updateStatus;
        return this;
    }

    public interface UpdateStatus {
        void onStatusChanged(Status status);
    }

}
