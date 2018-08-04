package com.netease.nical.rtskittest.RTSInitial;


import com.netease.nim.rtskit.RTSKit;
import com.netease.nim.rtskit.api.listener.RTSEventListener;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

/**
 * Created by winnie on 2018/3/26.
 */

public class RTSHelper {

    /**
     * 初始化方法
     */
    public static void init() {
        setRtsEventListener();
        setUserInfoProvider();
    }

    /**
     * 设置用户相关资料提供者
     */
    private static void setUserInfoProvider() {
        RTSKit.setUserInfoProvider(new com.netease.nim.rtskit.api.IUserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String account) {
                return RTSUserinfoProvider.getInstance().getUserinfo(account);
            }

            @Override
            public String getUserDisplayName(String account) {
                return RTSUserinfoProvider.getInstance().getUserinfo(account).getName();
            }
        });
    }


    /**
     * 设置rts事件监听器
     */
    private static void setRtsEventListener() {
        RTSKit.setRTSEventListener(new RTSEventListener() {
            @Override
            public void onRTSStartSuccess(String account) {
                //当白板发起成功时，处理业务层逻辑，如在消息列表插入一条消息
            }

            @Override
            public void onRTSFinish(String account, boolean selfFinish) {
                //当白板结束成功时，处理业务层逻辑，如在消息列表插入一条消息
            }
        });
    }


}
