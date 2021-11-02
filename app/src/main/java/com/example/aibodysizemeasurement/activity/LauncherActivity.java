package com.example.aibodysizemeasurement.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.example.aibodysizemeasurement.R;
import com.example.aibodysizemeasurement.application.MyApplication;
import com.example.aibodysizemeasurement.bean.UserVO;
import com.example.aibodysizemeasurement.utils.ModelConstant;
import com.example.aibodysizemeasurement.constant.NetConstant;
import com.example.aibodysizemeasurement.utils.ShareUtils;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;

/*
 *  描述： 启动页
 */
public class LauncherActivity extends BaseActivity {

    /**
     * 1.延时2000ms
     * 2.判断程序是否第一次运行
     * 3.Activity全屏主题
     */

    //闪屏业延时
    private static final int HANDLER_SPLASH = 1001;
    private static final int START = 1002;
    //判断程序是否是第一次运行
    private static final String SHARE_IS_FIRST = "isFirst";
    private boolean havein=false;
    private boolean isLogin = false;
    private TextView tvlogo,tvlogo1 ;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_SPLASH:
                    if(!havein){
                        havein=true;
                        //判断程序是否是第一次运行
//                        Log.d("123456", "isfirst： "+isFirst() );
                        if (isFirst()) {
                            Log.d("123456", "success：1" );
                            startActivity(new Intent(LauncherActivity.this, GuideActivity.class));
                        } else {
                            Log.d("123456", "isLogin:"+isLogin );
                            if (isLogin) {
                                getSizeLogin(telephoneInSP);
                            } else {
                                startActivity(new Intent(LauncherActivity.this, MyLoginActivity.class));
                                finish();
                            }

                        }
                        finish();
                    }

                    break;
                case START:
                    Log.e("eee",123123+"");
                    Intent intent=new Intent(LauncherActivity.this, StartActivity.class);
                    startActivity(intent);
                    finish();


                    break;
                default:
                    break;
            }
            return false;
        }

    });
    private String telephoneInSP;
    private String passwordInSP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_launcher);
        tvlogo=findViewById(R.id.tv_splash);
        tvlogo1=findViewById(R.id.tv_splash1);
        Typeface typeface=Typeface.createFromAsset(getAssets(),"LOGO.otf");
        Typeface typeface1=Typeface.createFromAsset(getAssets(),"yp.TTF");
        tvlogo.setTypeface(typeface);
        tvlogo1.setTypeface(typeface1);
        SharedPreferences sp = getSharedPreferences(ModelConstant.LOGIN_INFO, MODE_PRIVATE);
        telephoneInSP = sp.getString("telephone", "");
        passwordInSP = sp.getString("encryptedPassword", "");
        // 异步登录
        // asyncValidate(telephoneInSP, passwordInSP);
        asyncValidateWithXHttp2(telephoneInSP, passwordInSP);
        initView();


    }

    private void getSizeLogin(final String telephone){

        XHttp.post(NetConstant.getGetSizeURL())
                .params("telephone", telephone)
                .syncRequest(false)
                .execute(new SimpleCallBack<UserVO>() {
                    @Override
                    public void onSuccess(UserVO data) throws Throwable {
                        MyApplication.getInstance().userVO.setHeight(data.getHeight());
                        MyApplication.getInstance().userVO.setButto(data.getButto());
                        MyApplication.getInstance().userVO.setChest(data.getChest());
                        MyApplication.getInstance().userVO.setCrotch(data.getCrotch());
                        MyApplication.getInstance().userVO.setShoulder(data.getShoulder());
                        MyApplication.getInstance().userVO.setWaist(data.getWaist());
                        asyncGetUserWithXHttp2(telephoneInSP);
                    }

                    @Override
                    public void onError(ApiException e) {
                        //在线获取失败则本地获取

                        asyncGetUserWithXHttp2(telephoneInSP);
                    }
                });
    }

    private void asyncGetUserWithXHttp2(String telephone) {
        XHttp.post(NetConstant.getGetUserURL())
                .params("telephone", telephone)
                .syncRequest(false)
                .execute(new SimpleCallBack<UserVO>() {
                    @Override
                    public void onSuccess(UserVO data) throws Throwable {
                        Log.d("MyFragment", "请求URL成功： " + data);
                        MyApplication.getInstance().userVO.setName(data.getName());
                        MyApplication.getInstance().userVO.setAge(data.getAge());
                        MyApplication.getInstance().userVO.setGender(data.getGender());
                        MyApplication.getInstance().userVO.setStyle(data.getStyle());
                        MyApplication.getInstance().userVO.setTelephone(data.getTelephone());

                        handler.sendEmptyMessage(START);
                    }

                    @Override
                    public void onError(ApiException e) {

                    }
                });
    }

    //初始化View
    private void initView() {
        //延时2000ms

        handler.sendEmptyMessageDelayed(HANDLER_SPLASH, 2000);
    }

    //判断程序是否第一次运行
    private boolean isFirst() {
        boolean isFirst = ShareUtils.getBoolean(this, SHARE_IS_FIRST, true);
        if (isFirst) {
            ShareUtils.putBoolean(this, SHARE_IS_FIRST, false);
            //是第一次运行
            return true;
        } else {
            return false;
        }

    }






    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    // 异步登录
    private void asyncValidateWithXHttp2(String account, String password) {
        XHttp.post(NetConstant.getLoginURL())
                .params("telephone", account)
                .params("password", password)
                .params("type", "autoLogin")
                .syncRequest(false)
                .execute(new SimpleCallBack<Object>() {
                    @Override
                    public void onSuccess(Object data) throws Throwable {
                        isLogin=true;
                        MyApplication.getInstance().setLogin(true);
                        Log.d("LauncherActivity", "请求URL成功,自动登录成功");
//                        ToastUtil.showToast(getApplicationContext(),"自动登录成功！");
                    }

                    @Override
                    public void onError(ApiException e) {
                        Log.d("LauncherActivity", "请求URL异常,自动登录失败" + e.toString());
                        //ToastUtil.showToast(getApplicationContext(),"自动登录失败！");
//                        showToastInThread(CountDownActivity.this, e.getMessage());
                    }
                });
    }

}
