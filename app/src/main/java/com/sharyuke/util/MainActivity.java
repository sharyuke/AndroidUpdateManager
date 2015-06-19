package com.sharyuke.util;

import android.app.Activity;
import android.os.Bundle;

import com.sharyuke.tool.update.UpdateManager;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.RestAdapter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends Activity {

    public static final String UPDATE_DOWN_LOAD_URL = "http://test.yuke.me:9000/public/file/Passionlife.apk";
    public final static String SERVER_URL = "http://test.yuke.me:9000";
    UpdateManager updateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.plant(new Timber.DebugTree());
        ButterKnife.inject(this);
        UpdateInterface api = getAdapter().create(UpdateInterface.class);
        BaseReqModel baseReqModel = new BaseReqModel();
        baseReqModel.module = BaseReqModel.MODULE_INFORMATION;
        baseReqModel.method = BaseReqModel.METHOD_CHECK_VERSION;
        updateManager = UpdateManager.getInstance()
                .initUpdateManager(api.checkVersion(baseReqModel)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()),
                        UPDATE_DOWN_LOAD_URL,
                        BuildConfig.VERSION_CODE);
    }

    @OnClick(R.id.update)
    public void update() {
        updateManager.checkUpdate(this);
    }

    RestAdapter getAdapter() {
        return new RestAdapter.Builder()
                .setEndpoint(SERVER_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(Timber::i)
                .build();
    }

    interface UpdateInterface {

        @POST("/app/terminalapi/call")
        @FormUrlEncoded
        Observable<ResUpdateCheckVersion> checkVersion(@Field("requestValue") BaseReqModel reqModel);
    }

}
