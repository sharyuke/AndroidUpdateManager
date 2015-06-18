package com.sharyuke.util;

import com.sharyuke.tool.UpdateResModel;

/**
 * ResCheckVersion
 * Created by sharyuke on 15-6-8.
 */
public class ResCheckVersion implements UpdateResModel {
    public String version_name;
    public String method;
    public int version_code;

    @Override
    public int getVersionCode() {
        return version_code;
    }

    @Override
    public String getVersionName() {
        return version_name;
    }
}
