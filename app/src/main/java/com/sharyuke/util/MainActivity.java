package com.sharyuke.util;

import android.app.Activity;
import android.os.Bundle;

import com.sharyuke.tool.update.UpdateManager;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.RestAdapter;
import retrofit.http.GET;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends Activity {

    public static final String UPDATE_DOWN_LOAD_URL = "http://test.yuke.me:9000/public/file/Passionlife.apk";
    public final static String SERVER_URL = "http://api.yuke.me:3000";
    UpdateManager updateManager;

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
        updateManager.setOnUpdateStatus(status -> {

        });
        updateManager.setDialogTheme(R.style.Base_Theme_AppCompat_Dialog_Alert);
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

    interface UpdateInterface {

        @GET("/update")
        Observable<ResUpdateCheckVersion> checkVersion();
    }

}
