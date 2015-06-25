package com.sharyuke.util;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.sharyuke.tool.update.UpdateManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.RestAdapter;
import retrofit.http.GET;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends Activity implements UpdateManager.OnUpdateStatus {

    public static final String UPDATE_DOWN_LOAD_URL = "http://test.yuke.me:9000/public/file/Passionlife.apk";
    public final static String SERVER_URL = "http://api.yuke.me:3000";
    UpdateManager updateManager;

    @InjectView(R.id.progress)
    TextView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.plant(new Timber.DebugTree());
        ButterKnife.inject(this);
        UpdateInterface api = getAdapter().create(UpdateInterface.class);
        updateManager = UpdateManager.getInstance()
                .initUpdateManager(api.checkVersion()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()),
                        UPDATE_DOWN_LOAD_URL,
                        BuildConfig.VERSION_CODE);
        updateManager.setOnStatusUpdateListener(this);
        updateManager.setDialogTheme(R.style.dialogTheme);
        updateManager.setOnProgressUpdateListener(downLoadProgress ->
                progressView.setText(String.format("%.2f",
                        ((float) downLoadProgress.getProgress()) * 100
                                / downLoadProgress.getTotalLength())));
        updateManager.removeStatusListener(this);
    }

    @OnClick(R.id.update)
    public void update() {
        updateManager.checkUpdate(this);
    }

    @OnClick(R.id.debug_delete)
    public void delete() {
        updateManager.deleteCacheFiles();
    }

    RestAdapter getAdapter() {
        return new RestAdapter.Builder()
                .setEndpoint(SERVER_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(Timber::i)
                .build();
    }

    @Override
    public void onStatusChanged(UpdateManager.Status status) {

    }

    interface UpdateInterface {

        @GET("/update")
        Observable<ResUpdateCheckVersion> checkVersion();
    }

}
