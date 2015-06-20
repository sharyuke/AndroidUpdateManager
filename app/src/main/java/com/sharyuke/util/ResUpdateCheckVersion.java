package com.sharyuke.util;

import com.sharyuke.tool.update.ResUpdateModel;

/**
 * ResUpdateCheckVersion
 * Created by sharyuke on 15-6-8.
 */
public class ResUpdateCheckVersion implements ResUpdateModel {
    public String versionName;
    public String method;
    public int versionCode;

    @Override
    public int getVersionCode() {
        return versionCode;
    }

    @Override
    public String getVersionName() {
        return versionName;
    }
}
