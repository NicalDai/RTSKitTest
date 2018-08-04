package com.netease.nical.rtskittest.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.netease.nical.rtskittest.Login.EditTextClearTools;
import com.netease.nical.rtskittest.MD5.MD5;
import com.netease.nical.rtskittest.R;
import com.netease.nical.rtskittest.permission.MPermission;
import com.netease.nical.rtskittest.permission.annotation.OnMPermissionDenied;
import com.netease.nical.rtskittest.permission.annotation.OnMPermissionGranted;
import com.netease.nical.rtskittest.permission.annotation.OnMPermissionNeverAskAgain;
import com.netease.nim.rtskit.RTSKit;
import com.netease.nim.rtskit.activity.RTSActivity;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import static com.netease.nim.rtskit.RTSKit.getAccount;

public class LoginActivity extends AppCompatActivity {

    private Button Login_OK;    //登陆按钮
    private EditText account;   //账号输入框
    private EditText password;  //密码输入框
    private static final int BASIC_PERMISSION_REQUEST_CODE = 100;
    private String token;
    private String Appkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Appkey = getAppKey();
        //权限信息
        authorityManage();
        //初始化界面
        initView();
        //获取基本权限（相机，麦克风等）
        requestBasicPermission();

        Login_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String accid = account.getText().toString();
                //判断是否为demo的appkey，是就加MD5，不是就直接透传
                if(Appkey.equals("45c6af3c98409b18a84451215d0bdd6e")){
                    token = MD5.getStringMD5(password.getText().toString());
                }else {
                    token = password.getText().toString();
                }
                //实例化logininfo，执行登陆
                LoginInfo loginInfo = new LoginInfo(accid,token);
                doLogin(loginInfo);
            }
        });

    }

    /**
     * 界面初始化
     */
    private void initView(){
        Login_OK = findViewById(R.id.Login_OK);
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);

        ImageView unameClear = (ImageView) findViewById(R.id.iv_unameClear);
        ImageView pwdClear = (ImageView) findViewById(R.id.iv_pwdClear);

        //点击按钮清除EditText的内容
        EditTextClearTools.addClearListener(account,unameClear);
        EditTextClearTools.addClearListener(password,pwdClear);

    }

    /**
     * 权限申请
     */
    private void authorityManage(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }else {
                Toast.makeText(this, "权限已申请", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 基本权限管理
     */
    private final String[] BASIC_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private void requestBasicPermission() {
        MPermission.printMPermissionResult(true, this, BASIC_PERMISSIONS);
        MPermission.with(LoginActivity.this)
                .setRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(BASIC_PERMISSIONS)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
        try {
            Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        MPermission.printMPermissionResult(false, this, BASIC_PERMISSIONS);
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    @OnMPermissionNeverAskAgain(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
        try {
            Toast.makeText(this, "未全部授权，部分功能可能无法正常运行！", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        MPermission.printMPermissionResult(false, this, BASIC_PERMISSIONS);
    }


    /**
     * 执行登陆
     * @param loginInfo
     */
    private void doLogin(LoginInfo loginInfo){
        RequestCallback<LoginInfo> callback = new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo loginInfo) {
                Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();
                //跳转到拨打界面

                Intent intent = new Intent(LoginActivity.this,RTSCallActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailed(int i) {
                Toast.makeText(LoginActivity.this, "登陆失败，错误码："+i, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable throwable) {
                Toast.makeText(LoginActivity.this, "登陆异常，" + throwable.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        NIMClient.getService(AuthService.class).login(loginInfo).setCallback(callback);
    }

    /**
     * 点击空白位置 隐藏软键盘
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(null != this.getCurrentFocus()){
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }


    /**
     * 取出清单文件的appkey，用作MD5比较
     * @return
     */
    public String getAppKey() {
        String keyString = "";
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
            keyString = appInfo.metaData.getString("com.netease.nim.appKey");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return keyString;
    }
}
