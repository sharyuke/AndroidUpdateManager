package com.sharyuke.util;

import com.google.gson.Gson;

/**
 * 基类模型
 * Created by sharyuke on 5/9/15 at 4:21 PM.
 */
public class BaseReqModel {

    public static final String MODULE_INFORMATION = "Information";
    public static final String METHOD_CHECK_VERSION = "checkVersion";

    public String module;
    public String method;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
