# RTSKitTest
基于网易云信RTSKit实现的白板互通demo
## 概 述
目前云信将IM、音视频、互动白板的功能集成在一个即时通讯的demo中，整体功能耦合度较高，对于部分开发者可能比较难以解耦。为了便于我们开发者快速集成云信的功能，这里我收藏了一份热心开发者提供的独立白板demo以供参考。
## 下载编译 Demo
demo下载地址：https://github.com/NicalDai/RTSKitTest

总体环境需求：

该工程使用的build Tools版本为26.0.2，最低版本不要低于19.
JDK版本为10，建议最低不低于7.

如果你使用的 IDE 是 Android Studio，可直接在 IDE 中打开 Demo 工程，然后将工程目录下 gradle.properties 文件按照注释修改，就可以直接编译运行。

如果你是第一次使用 Android Studio，导入时会从 gradle 网站下载 gradle 发布包，在国内下载可能会比较慢。这时你也可以通过 Android Studio 设置，不使用 gradle wrapper，改为使用 local gradle distribution。

## 源码结构
由于 Demo 依赖于 RTSKit 进行开发。分为 Demo 工程和 RTSKit 工程。分别介绍这两个工程的源码结构。
### demo主工程：

- Application 入口：MainApplication, 包含 SDK 的初始化，UIKit的初始化以及配置示例。
- 登录相关：login 包，LoginActivity是登陆主界面，EditTextClearTools是提供了点击按钮一键清除EditText内容的方法。DataSaveToLocal提供了将数据本地处理的能力，CustomBoolean是用于判断登陆时是否需要MD5.
- 登陆加密：MD5包，提供了对String类型数据MD5加密的能力
- 权限管理：permission包，提供了摄像头，麦克风，网路，本地存储等权限
- 白板能力初始化：RTSInitial包，提供了初始化RTSKit的能力，包括用户资料的提供者，RTS事件监听器接口。
- Activity包：提供了一个发起白板通话的界面。

### RTSKit
- 可以参考这份文档：https://github.com/netease-im/NIM_Android_RtsKit/blob/master/README.md

## 修改Demo为己用
开发者可直接以 这份Demo 为基础开发自己的 白板业务软件，也可以稍作修改，用于前期流程验证，也可以作为 SDK 开发的参考和指南。


- 如果你已经在网易云通信官网上注册了 APP，你需要修改 AndroidManifest 中的 “com.netease.nim.appKey” 为你自己的 appkey，否则登录会失败。



## 业务逻辑说明
下面简单描述一下该demo的业务流程
### 初始化

其实整个初始化分两步
- 初始化SDK
- 初始化RTSKit的provider
- 初始化白板的observeIncomingSession的监听。

需要注意，一个是RTSKit的初始化逻辑必须判断在NIMUtil.isMainProcess(this)之后去判断。RTSHelper实现的是提供给RTSKit用户资料的提供者，以及白板事件的触发逻辑。RTSOptions这部分逻辑这个demo没有实现，可以在这里调用登出的方法。
```
public class MainApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        NIMClient.init(this,null,NimSDKOptionConfig.getSDKOptions(this));

        if(NIMUtil.isMainProcess(this)){ //判断是否为主进程
            initRTSKit();
        }
    }

    private void initRTSKit() {
        RTSOptions rtsOptions = new RTSOptions() {
            @Override
            public void logout(Context context) {
                // 登出操作，开发者可以自行实现
                // MainActivity.logout(context, true);
            }
        };
        RTSKit.init(rtsOptions);
        RTSHelper.init();
        RTSKit.setContext(this);
    }
}
```

### 登陆界面以及登陆登出逻辑

登陆登出界面的逻辑，主要附带的功能有记住密码、按钮清空输入框的功能。

这里有个需要开发者注意的点，如果应用的appkey是demo的appkey，需要在执行登陆之前将输入的密码进行MD5加密。

- 以下是点击按钮清理输入框的逻辑。

```
public class EditTextClearTools {
    
    public static void addClearListener(final EditText et , final ImageView iv ,final CustomBoolean b){
        
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(before == 0 && count == 32){
                    b.setB(false);  //场景是从文件中直接获取到了MD5的密码结果，这时密码不需要MD5
                }else {
                    b.setB(true);   //其他的场景，可以判断为手动输入的密码，如果Appkey是demo的需要MD5
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                //如果有输入内容长度大于0那么显示clear按钮
                String str = s + "" ;
                if (s.length() > 0){
                    iv.setVisibility(View.VISIBLE);
                }else{
                    iv.setVisibility(View.INVISIBLE);
                }
            }
        });


        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et.setText("");
            }
        });
    }
}
```

### 发起白板会话

输入被叫的账号，执行呼叫操作，跳转到RTSKit，后续逻辑移交给RTSKit实现。
```
        StartRTSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!toAccountText.getText().toString().isEmpty()){
                    RTSKit.startRTSSession(getApplicationContext(), toAccountText.getText().toString());
                }
            }
        });
```

## 集成过程中遇到的坑

1. 本地没有用户资料的缓存，发起时导致崩溃的问题。

```
Caused by: java.lang.NullPointerException:
Attempt to invoke interface method 'java.lang.String com.netease.nimlib.sdk.uinfo.model.NimUserInfo.getName()' on a null object reference
```
这个是由于执行RTSKit.setUserInfoProvider(new com.netease.nim.rtskit.api.IUserInfoProvider()的方法报的异常，方法内获取不到用户资料对象的Name，一般情况是由于本地数据库没有这个人的用户资料导致的。

解决：如果本地没有被叫的用户资料，那就在RTSActivity中向云端异步获取对方的资料，并更新UI。

```
private void initAccountInfoView() {
        displayName = RTSKit.getUserInfoProvider().getUserDisplayName(account);
        isNeedFinish = displayName.isNeedFresh();
        nameText.setText(displayName.getAccount());
        headImage.loadBuddyAvatar(account);
        if(isNeedFinish){
            //示例仅处理单个账号的场景。
            String[] array = {account};
            List<String> accounts = Arrays.asList(array);
            NIMClient.getService(UserService.class).fetchUserInfo(accounts)
                    .setCallback(new RequestCallback<List<NimUserInfo>>() {
                        @Override
                        public void onSuccess(List<NimUserInfo> nimUserInfos) {
                            if(!nimUserInfos.isEmpty()){
                                nimUserInfo = nimUserInfos.get(0);
                                nameText.setText(nimUserInfo.getName());
                                headImage.loadBuddyAvatar(account);
                                isNeedFinish = false;
                                Log.d("RTSActivity", "onSuccess: 获取到"+nimUserInfo.getAccount()+"的云端资料");
                                Toast.makeText(RTSActivity.this, "获取" + nimUserInfo.getName()+"的云端资料成功", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(RTSActivity.this, "请检查对方账号是否存在！", Toast.LENGTH_SHORT).show();
                                onFinish();
                            }

                        }
                        @Override
                        public void onFailed(int i) {
                            Log.e("RTSActivity", "onFailed: 获取云端好友资料失败,错误码："+i );
                        }

                        @Override
                        public void onException(Throwable throwable) {

                        }
                    });
        }
```


# Version 1.1
</br>1、修复了发起呼叫时，被叫账号本地不存在，导致必现的崩溃问题。
</br>2、新增登陆时记住密码的功能。
