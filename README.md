#安卓自定义更新组件的介绍
Android UpdateManager Introduce

6/20/2015 10:19:08 PM 

![view](view.gif)

---
######1.2.5 release note listeners should not more than 5 in case memory leak
监听器不能超过5个，防止内存泄漏 , 修改监听器方法错误

######1.2.4 release note
当处于下载状态时，不能删除文件
    
######1.2.1 release note
修改下载状态变化和下载进度变化的监听器的设置集合。增加remove监听器方法

    updateManager.removeOnUpdateStatus(this);
    updateManager.removeUpdateDownloadProgress(this);

######1.2.0 release note
fix progress bug

######1.1.9 release note
增加自定义dialog的style

             updateManager.setDialogTheme(R.style.Base_Theme_AppCompat_Dialog_Alert);

######1.1.8 release note
增加回调的方式来实现下载进度实时更新

             updateManager.setOnUpdateStatus(new UpdateManager.OnUpdateStatus() {
                    @Override
                    public void onStatusChanged(UpdateManager.Status status) {

                }
            });

####实例化Update


	updateManager = UpdateManager.getInstance()
                .initUpdateManager(api.checkVersion()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()),
                        UPDATE_DOWN_LOAD_URL,
                        BuildConfig.VERSION_CODE);

UpdateManager有多种实例化方法，不过有3个参数是必须的，

1、一个Observable<? extends ResUpdateModel>对象，用来进行检查更新的网络请求。ResUpdateModel接口需要实现2个方法，
分别得到版本名字versionName String,和版本码versionCode int。

2、一个下载app的地址链接，用来更新App之用 String。可以在UpdateManager实例化之后，任何地方重新设置下载链接

3、当前app的版本码 int。

4、其他参数

| bus |appName|
|:---:|:----:|
|基于otto，用来获取下载进度|用来缓存apk的文件名字，默认app|

####检查更新

	  updateManager.checkUpdate(getActivity());

如果想要静默检查更新（即，不弹出，“当前App是最新版本”和“检查更新连接服务器失败”），用于app启动时候的检查更新，添加一个参数即可

	  updateManager.checkUpdate(getActivity()，false);

自定义下载链接

	  updateManager.checkUpdate(getActivity()，downloadUrl);

####实时获取下载进度

实例化一个Bus（com.squareup.otto）对象，并且传递给updateManager。
在当前类，注册public void Registe(Object object);

	mBus.register(this);

然后写一个公开的方法，并且注解@Subscribe （注意选择com.squareup.otto包内的），并且接受一个DownLoadProgress对象

    @Subscribe
    public void updateDownloadProgress(DownLoadProgress progress) {
        
    }

DownLoadProgress 接口，有2个方法，获取最大文件长度，另外一个是获取当前的进度，每次数据更新（2次更新时间不会低于100毫秒），就会发送携带最新信息的DownLoadProgress对象。在这里进行下载进度的更新。发送的信息是在主线程完成的，可以直接操作UI。


####其他方法

---
*删除缓存文件

updateManager带有下载文件缓存功能，第一次下载成功了，第二次相同的版本直接跳转安装，如果有相同的名字，但是版本不一样，
就需要删除缓存文件然后再更新

    public void deleteCacheFiles() {
        FileHelper.deleteFile(files);
    }

*直接下载app

    public void downLoadDebug(Activity activity, String url, String versionName) {
        download(activity, url, versionName);
    }

####使用 

UpdateManager 已经上传到Jcenter 在android studio 的 build.gradle 里面添加Jcenter的依赖

	  compile 'com.sharyuke:apptool:1.2.1@aar'

[或者直接下载aar](https://bintray.com/artifact/download/sharyuke/maven/com/sharyuke/apptool/1.2.1/apptool-1.2.1.aar)