package com.example.aibodysizemeasurement.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aibodysizemeasurement.R;
import com.example.aibodysizemeasurement.application.MyApplication;
import com.example.aibodysizemeasurement.bean.OtpCode;
import com.example.aibodysizemeasurement.utils.ModelConstant;
import com.example.aibodysizemeasurement.constant.NetConstant;
import com.example.aibodysizemeasurement.utils.ValidUtils;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;

public class MyRegisterActivity extends AppCompatActivity implements View.OnClickListener , TextWatcher {

    private final String TAG = "MyRegisterActivity";
    private Toast mToast;
    private LinearLayout mLlRegisterUsername,mLlRegisterPwd;
    private EditText mEtRegisterUsername,mEtRegisterPwd;
    private ImageView mIvRegisterUsernameDel,mIvRegisterPwdDel;
    private LinearLayout mLlOtpCode;
    private EditText mEtOtpCode;
    private TextView mTvgetOtp;
    private Button mBtRegister;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String otpCode;




    private String telephone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_register_step_one);
        findViewById(R.id.ib_navigation_back).setOnClickListener(this);
        //telephone
        mLlRegisterUsername = findViewById(R.id.ll_register_phone);
        mEtRegisterUsername = findViewById(R.id.et_register_username);
        mIvRegisterUsernameDel = findViewById(R.id.iv_register_username_del);
        mEtRegisterUsername.setOnClickListener(this);
        mIvRegisterUsernameDel.setOnClickListener(this);

        //otp
        mLlOtpCode = findViewById(R.id.ll_register_sms_code);
        mEtOtpCode = findViewById(R.id.et_register_auth_code);
        mTvgetOtp = findViewById(R.id.tv_register_sms_call);
        mEtOtpCode.setOnClickListener(this);
        mTvgetOtp.setOnClickListener(this);

        //password
        mLlRegisterPwd = findViewById(R.id.ll_register_pwd);
        mEtRegisterPwd = findViewById(R.id.et_register_pwd);
        mIvRegisterPwdDel = findViewById(R.id.iv_register_pwd_del);
        mEtRegisterPwd.setOnClickListener(this);
        mIvRegisterPwdDel.setOnClickListener(this);

        //register
        mBtRegister = findViewById(R.id.bt_register_submit);
        mBtRegister.setOnClickListener(this);
        setOnFocusChangeErrMsg(mEtRegisterUsername, "????????????????????????","telephone");
        setOnFocusChangeErrMsg(mEtRegisterPwd, "??????????????????6???","password");

        //text
        mEtRegisterUsername.addTextChangedListener(this);
        mEtRegisterPwd.addTextChangedListener(this);
        mEtOtpCode.addTextChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        telephone = mEtRegisterUsername.getText().toString();
        String otpCode = mEtOtpCode.getText().toString();
        String password = mEtRegisterPwd.getText().toString();
        switch (view.getId()) {
            case R.id.ib_navigation_back:
                finish();
                break;
            case R.id.et_register_username:
                mEtOtpCode.clearFocus();
                mEtRegisterPwd.clearFocus();
                mEtRegisterUsername.setFocusableInTouchMode(true);
                mEtRegisterUsername.requestFocus();
                break;
            case R.id.et_register_auth_code:
                mEtRegisterPwd.clearFocus();
                mEtRegisterUsername.clearFocus();
                mEtOtpCode.setFocusableInTouchMode(true);
                mEtOtpCode.requestFocus();
                break;
            case R.id.et_register_pwd:
                mEtRegisterUsername.clearFocus();
                mEtOtpCode.clearFocus();
                mEtRegisterPwd.setFocusableInTouchMode(true);
                mEtRegisterPwd.requestFocus();
                break;
            case R.id.iv_register_pwd_del:
                //????????????
                mEtRegisterPwd.setText(null);
            case R.id.iv_register_username_del:
                //????????????
                mEtRegisterUsername.setText(null);
            case R.id.tv_register_sms_call:
                // ???????????????????????????????????????
                if (TextUtils.isEmpty(telephone)) {
                    showToast( "?????????????????????");
                } else {
                    if (ValidUtils.isPhoneValid(telephone)) {
//                      asyncGetOtpCode(telephone);
                        asyncGetOtpCodeWithXHttp2(telephone);
                        mTvgetOtp.setBackgroundResource(R.drawable.bg_login_submit_lock);
                        mTvgetOtp.setTextColor(getResources().getColor(R.color.account_lock_font_color));
                    } else {
                        showToast( "???????????????????????????");
                    }
                }
                break;
            case R.id.bt_register_submit:
                asyncRegisterWithXHttp2(telephone, otpCode,password);
                break;
        }
    }

    //???????????????
    private void asyncGetOtpCodeWithXHttp2(String telephone) {
        XHttp.post(NetConstant.getGetOtpCodeURL())
                .params("telephone", telephone)
                .syncRequest(false)
                .execute(new SimpleCallBack<OtpCode>() {
                    @Override
                    public void onSuccess(OtpCode data) throws Throwable {
                        Log.d(TAG, "??????URL????????? " + data);
                        if (data != null) {
                            otpCode = data.getOtpCode();
                            // ?????????????????????
                            setTextInThread(mEtOtpCode, otpCode);
                            // ?????????????????????Toast
                            showToast( "????????????" + otpCode);
                            Log.d(TAG, "telephone: " + telephone + " otpCode: " + otpCode);
                            mTvgetOtp.setBackgroundResource(R.drawable.bg_login_submit);
                            mTvgetOtp.setTextColor(getResources().getColor(R.color.white));
                        }
                        Log.d(TAG, "????????????????????????????????????");
                    }

                    @Override
                    public void onError(ApiException e) {
                        Log.d(TAG, "??????URL????????? " + e.toString());
                        mTvgetOtp.setBackgroundResource(R.drawable.bg_login_submit);
                        mTvgetOtp.setTextColor(getResources().getColor(R.color.white));
                        showToast( e.getMessage());
                    }
                });
    }

    //??????
    private void asyncRegisterWithXHttp2(final String telephone, final String otpCode,final String password) {
        // ????????????
        if (TextUtils.isEmpty(telephone) || TextUtils.isEmpty(otpCode)|| TextUtils.isEmpty(password)) {
            Toast.makeText(MyRegisterActivity.this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }




        // ??????
        XHttp.post(NetConstant.getRegisterURL())
                .params("telephone", telephone)
                .params("otpCode", otpCode)
                .params("name", "ZL_1")
                .params("gender", 0)
                .params("age", 0)
                .params("password", password)
                .syncRequest(false)
                .execute(new SimpleCallBack<Object>() {
                    @Override
                    public void onSuccess(Object data) throws Throwable {
                        Log.d(TAG, "success??? " );
                        // ?????????????????????????????????
                        sp = getSharedPreferences(ModelConstant.LOGIN_INFO, MODE_PRIVATE);
                        editor = sp.edit();
                        editor.clear();
                        editor.putString("telephone", telephone);
                        String encryptedPassword = ValidUtils.encodeByMd5(password);
                        editor.putString("encryptedPassword", encryptedPassword);
                        editor.apply();

                        MyApplication.getInstance().setLogin(true);
                        MyApplication.getInstance().userVO.setTelephone(telephone);
                        MyApplication.getInstance().userVO.setName("ZL_1");

                        Intent intent=new Intent(MyRegisterActivity.this, StartActivity.class);
                        startActivity(intent);
                        //?????????????????????????????????????????????????????????
                        finish();





                    }

                    @Override
                    public void onError(ApiException e) {
                        Log.d(TAG, "??????URL????????? " + e.toString());
                        showToast( e.getMessage());
                    }
                });
    }



    private void setOnFocusChangeErrMsg(EditText editText, String errMsg,String inputType) {
        editText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        String inputStr = editText.getText().toString();
                        // ????????????
                        if (!hasFocus) {
                            if(inputType.equals("telephone")){
                                if (!ValidUtils.isPhoneValid(inputStr)) {
                                    editText.setError(errMsg);
                                }
                            }else {
                                if (!ValidUtils.isPasswordValid(inputStr)) {
                                    editText.setError(errMsg);
                                }
                            }


                        }
                    }
                }
        );
    }



    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String username = mEtRegisterUsername.getText().toString().trim();
        String otpCode = mEtOtpCode.getText().toString().trim();

        //????????????????????????
        if (username.length() > 0) {
            mIvRegisterUsernameDel.setVisibility(View.VISIBLE);
        } else {
            mIvRegisterUsernameDel.setVisibility(View.INVISIBLE);
        }


        //????????????????????????
        if (!TextUtils.isEmpty(otpCode) && !TextUtils.isEmpty(username)) {
            mBtRegister.setBackgroundResource(R.drawable.bg_login_submit);
            mBtRegister.setTextColor(getResources().getColor(R.color.white));
        } else {
            mBtRegister.setBackgroundResource(R.drawable.bg_login_submit_lock);
            mBtRegister.setTextColor(getResources().getColor(R.color.account_lock_font_color));
        }
    }
    private void showToast(String msg) {
        if (null != mToast) {
            mToast.setText(msg);
        } else {
            mToast = Toast.makeText(MyRegisterActivity.this, msg, Toast.LENGTH_SHORT);
        }

        mToast.show();
    }
    /* ?????????????????????UI ?????????????????????????????? */
    private void setTextInThread(EditText editText, String otpCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                editText.setText(otpCode);
            }
        });
    }
}