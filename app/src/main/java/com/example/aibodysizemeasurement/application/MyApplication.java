package com.example.aibodysizemeasurement.application;

import android.app.Application;

import com.example.aibodysizemeasurement.bean.UserVO;
import com.example.aibodysizemeasurement.constant.NetConstant;
import com.xuexiang.xhttp2.XHttpSDK;

public class MyApplication extends Application {
    private static MyApplication instance;
    public UserVO userVO;

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    private boolean isLogin;



    @Override
    public void onCreate() {
        super.onCreate();
        XHttpSDK.init(this);   //初始化网络请求框架，必须首先执行
        XHttpSDK.setBaseUrl(NetConstant.baseService);  //设置网络请求的基础地址
        instance = this;
        userVO=new UserVO("",Byte.parseByte("-1"),0,"",0,0,0,0,0,0,0);
    }

    public static MyApplication getInstance(){
        // 因为我们程序运行后，Application是首先初始化的，如果在这里不用判断instance是否为空
        return instance;
    }

    public void reload(){
        userVO=new UserVO("",Byte.parseByte("-1"),0,"",0,0,0,0,0,0,0);
    }

}
