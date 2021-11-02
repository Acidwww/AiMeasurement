package com.example.aibodysizemeasurement.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aibodysizemeasurement.R;
import com.example.aibodysizemeasurement.application.MyApplication;
import com.example.aibodysizemeasurement.bean.UserVO;
import com.example.aibodysizemeasurement.constant.NetConstant;
import com.example.aibodysizemeasurement.utils.ToastUtil;
import com.example.aibodysizemeasurement.utils.ValidUtils;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;

public class MyLoginActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener, ViewTreeObserver.OnGlobalLayoutListener, TextWatcher{

    private String TAG = "ifu25";
    // 声明SharedPreferences对象
    SharedPreferences sp;
    // 声明SharedPreferences编辑器对象
    SharedPreferences.Editor editor;
    private ImageButton mIbNavigationBack;
    private LinearLayout mLlLoginPull;
    private View mLlLoginLayer;
    private LinearLayout mLlLoginOptions;
    private EditText mEtLoginUsername;
    private EditText mEtLoginPwd;
    private LinearLayout mLlLoginUsername;
    private ImageView mIvLoginUsernameDel;
    private Button mBtLoginSubmit;
    private LinearLayout mLlLoginPwd;
    private ImageView mIvLoginPwdDel;
    private ImageView mIvLoginLogo;
    private LinearLayout mLayBackBar;
    private TextView mTvLoginForgetPwd,tvnologin;
    private Button mBtLoginRegister;
    private String craccount ;
    private String crpassword ;
    //全局Toast
    private Toast mToast;

    private int mLogoHeight;
    private int mLogoWidth;
    private static final int START = 1002;
    private boolean ifSize=false,ifUser=false,isLogin=false;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case START:
                    Log.e("eee",123+"");


                    break;
                default:
                    break;
            }
            return false;
        }

    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_login);

        initView();
    }

    //初始化视图
    private void initView() {
        //登录层、下拉层、其它登录方式层


        mLlLoginOptions = findViewById(R.id.ll_login_options);

        //导航栏+返回按钮
        mLayBackBar = findViewById(R.id.ly_retrieve_bar);


        //logo
        mIvLoginLogo = findViewById(R.id.iv_login_logo);

        //username
        mLlLoginUsername = findViewById(R.id.ll_login_username);
        mEtLoginUsername = findViewById(R.id.et_login_username);
        mIvLoginUsernameDel = findViewById(R.id.iv_login_username_del);

        //passwd
        mLlLoginPwd = findViewById(R.id.ll_login_pwd);
        mEtLoginPwd = findViewById(R.id.et_login_pwd);
        mIvLoginPwdDel = findViewById(R.id.iv_login_pwd_del);

        //提交、注册
        mBtLoginSubmit = findViewById(R.id.bt_login_submit);
        mBtLoginRegister = findViewById(R.id.bt_login_register);

        //忘记密码
        mTvLoginForgetPwd = findViewById(R.id.tv_login_forget_pwd);
        mTvLoginForgetPwd.setOnClickListener(this);

        //暂不登录
        tvnologin=findViewById(R.id.nologin);
        tvnologin.setOnClickListener(this);

        //注册点击事件


        mEtLoginUsername.setOnClickListener(this);
        mIvLoginUsernameDel.setOnClickListener(this);
        mBtLoginSubmit.setOnClickListener(this);
        mBtLoginRegister.setOnClickListener(this);
        mEtLoginPwd.setOnClickListener(this);
        mIvLoginPwdDel.setOnClickListener(this);
        findViewById(R.id.ib_login_weibo).setOnClickListener(this);
        findViewById(R.id.ib_login_qq).setOnClickListener(this);
        findViewById(R.id.ib_login_wx).setOnClickListener(this);

        //注册其它事件
        mLayBackBar.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mEtLoginUsername.setOnFocusChangeListener(this);
        mEtLoginUsername.addTextChangedListener(this);
        mEtLoginPwd.setOnFocusChangeListener(this);
        mEtLoginPwd.addTextChangedListener(this);

        setOnFocusChangeErrMsg(mEtLoginUsername, "phone", "手机号格式不正确");
        setOnFocusChangeErrMsg(mEtLoginPwd, "password", "密码必须不少于6位");
    }
    /*
        当账号输入框失去焦点时，校验账号是否是中国大陆手机号
        当密码输入框失去焦点时，校验密码是否不少于6位
        如有错误，提示错误信息
         */
    private void setOnFocusChangeErrMsg(EditText editText, String inputType, String errMsg) {
        editText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        String inputStr = editText.getText().toString();
                        if (!hasFocus) {
                            switch (inputType) {
                                case "phone":
                                    if (!ValidUtils.isPhoneValid(inputStr)) {
                                        editText.setError(errMsg);
                                    }
                                    break;
                                case "password":
                                    if (!ValidUtils.isPasswordValid(inputStr)) {
                                        editText.setError(errMsg);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
        );
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_login_username:
                mEtLoginPwd.clearFocus();
                mEtLoginUsername.setFocusableInTouchMode(true);
                mEtLoginUsername.requestFocus();
                break;
            case R.id.et_login_pwd:
                mEtLoginUsername.clearFocus();
                mEtLoginPwd.setFocusableInTouchMode(true);
                mEtLoginPwd.requestFocus();
                break;
            case R.id.iv_login_username_del:
                //清空用户名
                mEtLoginUsername.setText(null);
                break;
            case R.id.iv_login_pwd_del:
                //清空密码
                mEtLoginPwd.setText(null);
                break;
            case R.id.bt_login_submit:
                craccount = mEtLoginUsername.getText().toString();
                crpassword = mEtLoginPwd.getText().toString();
                //登录
                loginRequest();
                break;
            case R.id.bt_login_register:
                //注册
                startActivity(new Intent(MyLoginActivity.this, MyRegisterActivity.class));
                break;
            case R.id.tv_login_forget_pwd:
                //忘记密码
                startActivity(new Intent(MyLoginActivity.this, ForgetPwdActivity.class));
                break;
//            case R.id.ll_login_layer:
//            case R.id.ll_login_pull:
//                mLlLoginPull.animate().cancel();
//                mLlLoginLayer.animate().cancel();
//
//                int height = mLlLoginOptions.getHeight();
//                float progress = (mLlLoginLayer.getTag() != null && mLlLoginLayer.getTag() instanceof Float) ? (float) mLlLoginLayer.getTag() : 1;
//                int time = (int) (360 * progress);
//
//                if (mLlLoginPull.getTag() != null) {
//                    mLlLoginPull.setTag(null);
//                    glide(height, progress, time);
//                } else {
//                    mLlLoginPull.setTag(true);
//                    upGlide(height, progress, time);
//                }
//                break;
            case R.id.ib_login_weibo:
                weiboLogin();
                break;
            case R.id.ib_login_qq:
                qqLogin();
                break;
            case R.id.ib_login_wx:
                weixinLogin();
                break;
            case R.id.nologin:
                //暂不登录
                Intent intent=new Intent(this,StartActivity.class);
                MyApplication.getInstance().setLogin(false);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    //用户名密码焦点改变
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();

        if (id == R.id.et_login_username) {
            if (hasFocus) {
                mLlLoginUsername.setActivated(true);
                mLlLoginPwd.setActivated(false);
            }
        } else {
            if (hasFocus) {
                mLlLoginPwd.setActivated(true);
                mLlLoginUsername.setActivated(false);
            }
        }
    }




    //显示或隐藏logo
    @Override
    public void onGlobalLayout() {
        final ImageView ivLogo = this.mIvLoginLogo;
        Rect KeypadRect = new Rect();

        mLayBackBar.getWindowVisibleDisplayFrame(KeypadRect);

        int screenHeight = mLayBackBar.getRootView().getHeight();
        int keypadHeight = screenHeight - KeypadRect.bottom;

        //隐藏logo
        if (keypadHeight > 300 && ivLogo.getTag() == null) {
            final int height = ivLogo.getHeight();
            final int width = ivLogo.getWidth();
            this.mLogoHeight = height;
            this.mLogoWidth = width;

            ivLogo.setTag(true);

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0);
            valueAnimator.setDuration(400).setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = ivLogo.getLayoutParams();
                    layoutParams.height = (int) (height * animatedValue);
                    layoutParams.width = (int) (width * animatedValue);
                    ivLogo.requestLayout();
                    ivLogo.setAlpha(animatedValue);
                }
            });

            if (valueAnimator.isRunning()) {
                valueAnimator.cancel();
            }
            valueAnimator.start();
        }
        //显示logo
        else if (keypadHeight < 300 && ivLogo.getTag() != null) {
            final int height = mLogoHeight;
            final int width = mLogoWidth;

            ivLogo.setTag(null);

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(400).setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = ivLogo.getLayoutParams();
                    layoutParams.height = (int) (height * animatedValue);
                    layoutParams.width = (int) (width * animatedValue);
                    ivLogo.requestLayout();
                    ivLogo.setAlpha(animatedValue);
                }
            });

            if (valueAnimator.isRunning()) {
                valueAnimator.cancel();
            }
            valueAnimator.start();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    //用户名密码输入事件
    @Override
    public void afterTextChanged(Editable s) {
        String username = mEtLoginUsername.getText().toString().trim();
        String pwd = mEtLoginPwd.getText().toString().trim();

        //是否显示清除按钮
        if (username.length() > 0) {
            mIvLoginUsernameDel.setVisibility(View.VISIBLE);
        } else {
            mIvLoginUsernameDel.setVisibility(View.INVISIBLE);
        }
        if (pwd.length() > 0) {
            mIvLoginPwdDel.setVisibility(View.VISIBLE);
        } else {
            mIvLoginPwdDel.setVisibility(View.INVISIBLE);
        }

        //登录按钮是否可用
        if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(username)) {
            mBtLoginSubmit.setBackgroundResource(R.drawable.bg_login_submit);
            mBtLoginSubmit.setTextColor(getResources().getColor(R.color.white));
        } else {
            mBtLoginSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
            mBtLoginSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
        }
    }

    //登录
    private void loginRequest() {
        if (!(ValidUtils.isPhoneValid(craccount) && ValidUtils.isPasswordValid(crpassword))) {
            Toast.makeText(this, "账号或密码错误", Toast.LENGTH_SHORT).show();
            return;
        }
                /*
                   因为验证是耗时操作，所以独立成方法
                   在方法中开辟子线程，避免在当前UI线程进行耗时操作
                   否则会造成 ANR（application not responding）
                */
//                asyncLogin(account, password);
        getSizeLogin(craccount);


    }

    //微博登录
    private void weiboLogin() {

    }

    //QQ登录
    private void qqLogin() {

    }

    //微信登录
    private void weixinLogin() {

    }
    private void asyncLoginWithXHttp2(String telephone, String password) {
        XHttp.post(NetConstant.getLoginURL())
                .params("telephone", telephone)
                .params("password", password)
                .params("type", "login")
                .syncRequest(false)
                .execute(new SimpleCallBack<UserVO>() {
                    @Override
                    public void onSuccess(UserVO data) throws Throwable {
                        Log.d(TAG, "请求URL成功,登录成功");
                        String encryptedPassword = ValidUtils.encodeByMd5(password);
                        sp = getSharedPreferences("login_info", MODE_PRIVATE);
                        editor = sp.edit();
                        editor.putString("telephone", telephone);
                        editor.putString("encryptedPassword", encryptedPassword);

                        if (editor.commit()) {

                            MyApplication.getInstance().setLogin(true);
//
                            Intent intent=new Intent(MyLoginActivity.this, StartActivity.class);
                            startActivity(intent);
                            finish();
                            // 登录成功后，登录界面就没必要占据资源了

                        } else {
                            showToast("账号密码保存失败，请重新登录");
                        }
                    }

                    @Override
                    public void onError(ApiException e) {
                        Log.d(TAG, "请求URL失败： " + e.getMessage());
                        showToast( e.getMessage());
                    }
                });
    }
    /**
     * 显示Toast
     *
     * @param msg 提示信息内容
     */
    private void showToast(String msg) {
        if (null != mToast) {
            mToast.setText(msg);
        } else {
            mToast = Toast.makeText(MyLoginActivity.this, msg, Toast.LENGTH_SHORT);
        }

        mToast.show();
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
                        asyncGetUserWithXHttp2(craccount);


                    }

                    @Override
                    public void onError(ApiException e) {


                        asyncGetUserWithXHttp2(craccount);
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
                        asyncLoginWithXHttp2(craccount, crpassword);
                    }

                    @Override
                    public void onError(ApiException e) {

                    }
                });
    }
}
