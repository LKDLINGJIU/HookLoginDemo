package com.android.lj.hooklogindemo;

import android.app.Application;
import android.content.Context;

/**
 * Created by lingjiu on 2018/11/13.
 */
public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        HookUtils.getInstance().init(base);
        HookUtils.getInstance().hookLogin();
    }
}
