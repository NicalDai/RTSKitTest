package com.netease.nical.rtskittest.RTSInitial;

import android.util.Log;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.Arrays;
import java.util.List;

/**
 * 用户资料提供类，主要是为了判断本地是否有这个用户的资料
 */
public class RTSUserinfoProvider {

    private NimUserInfo nimUserInfo;
    private String TAG = "RTSUserinfoProvider";

    /**
     * 白板用户资料提供者初始化方法
     * @return
     */
    public static RTSUserinfoProvider getInstance(){
        RTSUserinfoProvider rtsUserinfoProvider = new RTSUserinfoProvider();
        return rtsUserinfoProvider;
    }

    /**
     * 获取指定的用户资料，如果本地拿不到，就去拿云端的。
     * @param account
     * @return
     */
    public NimUserInfo getUserinfo(String account){
        //先拿本地数据库的用户信息
        nimUserInfo = NIMClient.getService(UserService.class).getUserInfo(account);
        //拿不到去拿云端的
        if (nimUserInfo == null){
            //示例仅处理单个账号的场景。
            String[] array = {account};
            List<String> accounts = Arrays.asList(array);
            NIMClient.getService(UserService.class).fetchUserInfo(accounts)
                    .setCallback(new RequestCallback<List<NimUserInfo>>() {
                        @Override
                        public void onSuccess(List<NimUserInfo> nimUserInfos) {
                            nimUserInfo = nimUserInfos.get(0);
                            Log.d(TAG, "onSuccess: 获取云端好友资料成功");
                        }

                        @Override
                        public void onFailed(int i) {
                            Log.e(TAG, "onFailed: 获取云端好友资料失败,错误码："+i );
                        }

                        @Override
                        public void onException(Throwable throwable) {

                        }
                    });
        }
        return nimUserInfo;
    }

}
