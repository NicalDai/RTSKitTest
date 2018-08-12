package com.netease.nical.rtskittest.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.netease.nical.rtskittest.R;
import com.netease.nim.rtskit.RTSKit;

import static com.netease.nim.rtskit.RTSKit.getAccount;

public class RTSCallActivity extends AppCompatActivity {

    private Button StartRTSButton;  //发起白板会话的按钮
    private EditText toAccountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtscall);
        //初始化界面
        initView();

        StartRTSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!toAccountText.getText().toString().isEmpty()){
                    RTSKit.startRTSSession(getApplicationContext(), toAccountText.getText().toString());
                }
            }
        });
    }

    /**
     * 初始化界面
     */
    private void initView(){
        StartRTSButton = findViewById(R.id.startrts);
        toAccountText = findViewById(R.id.toaccountid);
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

}
