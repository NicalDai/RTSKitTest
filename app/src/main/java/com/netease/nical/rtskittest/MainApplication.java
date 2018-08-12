package com.netease.nical.rtskittest;

import android.app.Application;
import android.content.Context;

import com.netease.nical.rtskittest.RTSInitial.RTSHelper;
import com.netease.nim.rtskit.RTSKit;
import com.netease.nim.rtskit.api.config.RTSOptions;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.util.NIMUtil;

public class MainApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        NIMClient.init(this,null,NimSDKOptionConfig.getSDKOptions(this));

        if(NIMUtil.isMainProcess(this)){
            initRTSKit();
        }
    }

    private void initRTSKit() {
        RTSOptions rtsOptions = new RTSOptions() {
            @Override
            public void logout(Context context) {
                // MainActivity.logout(context, true);
            }
        };
        RTSKit.init(rtsOptions);
        RTSHelper.init();
        RTSKit.setContext(this);
    }

}
