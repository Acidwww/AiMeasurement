package com.example.aibodysizemeasurement.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.aibodysizemeasurement.R;
import com.example.aibodysizemeasurement.application.MyApplication;
import com.example.aibodysizemeasurement.bean.AgeBean;
import com.example.aibodysizemeasurement.bean.HeightBean;
import com.example.aibodysizemeasurement.bean.UserVO;
import com.example.aibodysizemeasurement.utils.ModelConstant;
import com.example.aibodysizemeasurement.constant.NetConstant;
import com.example.aibodysizemeasurement.utils.ToastUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;

import java.util.ArrayList;

import static com.xuexiang.xhttp2.XHttp.getContext;

public class MyinfoActivity extends AppCompatActivity implements  View.OnClickListener{

    private ImageButton mIbNavigationBack;
    private boolean isLogin;
    private String telephone;
    private TextView tvtelephone,tvgenger,tvstyle,tvage,sui;
    private EditText etusername;
    private RelativeLayout rlusername,rlage,rlgender,rlstyle;
    private int gender,style,curgender;
    private String curage,curusername;
    private String curstyle;
    private OptionsPickerView pvCustomOptions;
    private ArrayList<AgeBean> ages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        sui=findViewById(R.id.sui);
        etusername=findViewById(R.id.data_username);
        tvage =findViewById(R.id.data_age);
        tvgenger=findViewById(R.id.data_gender);
        tvtelephone=findViewById(R.id.data_telephone);
        tvstyle=findViewById(R.id.data_style);
        mIbNavigationBack = findViewById(R.id.ib_navigation_back_info);
        rlage=findViewById(R.id.info_age);
        rlgender=findViewById(R.id.info_gender);
        rlusername=findViewById(R.id.info_username);
        rlstyle=findViewById(R.id.info_style);
        rlstyle.setOnClickListener(this);
        rlage.setOnClickListener(this);
        rlgender.setOnClickListener(this);
        rlusername.setOnClickListener(this);
        mIbNavigationBack.setOnClickListener(this);
        isLogin= MyApplication.getInstance().isLogin();

        telephone = MyApplication.getInstance().userVO.getTelephone();
        if(isLogin){
            sui.setVisibility(View.VISIBLE);
            setInfo();
            initCustomOptionPicker();
            //asyncGetUserWithXHttp2(telephone);
        }else{
            etusername.setFocusable(false);
            etusername.setFocusableInTouchMode(false);
            tvage.setFocusable(false);
            tvage.setFocusableInTouchMode(false);
            etusername.setText("未登录");
            SharedPreferences sp1 = getSharedPreferences(ModelConstant.SIZE_INFO, MODE_PRIVATE);
            gender=sp1.getInt("gender", 0);
            if(gender==0){
                tvgenger.setText("女");
            }else{
                tvgenger.setText("男");
            }
        }

    }

    private void setInfo() {
        String u=MyApplication.getInstance().userVO.getName();
        etusername.setText(u);
        curusername=u;
        String a=MyApplication.getInstance().userVO.getAge()+"";
        tvage.setText(a);
        curage=a;
        if(MyApplication.getInstance().userVO.getGender()==1){
            tvgenger.setText("男");
            curgender=1;
            gender=1;
        }else if(MyApplication.getInstance().userVO.getGender()==0){
            tvgenger.setText("女");
            curgender=0;
            gender=0;
        }
        switch (MyApplication.getInstance().userVO.getStyle()){
            case 0:
                tvstyle.setText("贴身");
                curstyle="贴身";
                style=0;
                break;
            case 1:
                tvstyle.setText("较贴身");
                curstyle="较贴身";
                style=1;
                break;
            case 2:
                tvstyle.setText("较宽松");
                curstyle="较宽松";
                style=2;
                break;
            case 3:
                tvstyle.setText("宽松");
                curstyle="宽松";
                style=3;
                break;
        }

        tvtelephone.setText(MyApplication.getInstance().userVO.getTelephone());
    }

    //拦截返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            //未登录直接退出
            if(!isLogin){
                finish();
                return true;
            }//登录则判断是否改动信息
            else if(curage.equals(tvage.getText().toString())&&curgender==gender&&curusername.equals(etusername.getText().toString())&&curstyle.equals(tvstyle.getText().toString())){
                finish();
                return true;
            }else{
                new XPopup.Builder(this).asConfirm("", "是否保存修改？",
                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                                if(isLogin){
                                    asyncUpdateWithXHttp2(telephone,etusername.getText().toString(), tvage.getText().toString(),gender+"",style+"");
                                    saveLocal();
                                }
                                else {
                                   finish();
                                }

                            }
                        }, new OnCancelListener() {
                            @Override
                            public void onCancel() {
                                finish();
                            }
                        })
                        .show();
                return true;
            }
        }else{
            return super.onKeyDown(keyCode, event);
        }

    }

    private void saveLocal() {
        MyApplication.getInstance().userVO.setTelephone(telephone);
        MyApplication.getInstance().userVO.setName(etusername.getText().toString());
        MyApplication.getInstance().userVO.setAge(Integer.valueOf(tvage.getText().toString()));
        MyApplication.getInstance().userVO.setGender(Byte.parseByte(gender+""));
        MyApplication.getInstance().userVO.setStyle(style);
    }

    private void asyncUpdateWithXHttp2(String telephone, String username, String age, String gender,String style) {
        XHttp.post(NetConstant.getUpdateUserURL())
                .params("telephone", telephone)
                .params("name", username)
                .params("age", age)
                .params("gender", gender)
                .params("style",style)
                .syncRequest(false)
                .execute(new SimpleCallBack<Object>() {
                    @Override
                    public void onSuccess(Object data) throws Throwable {
                        Log.d("Myinfo", "请求URL成功： " + data);
                        ToastUtil.showToast(getApplicationContext(),"保存成功");
                        finish();
                    }

                    @Override
                    public void onError(ApiException e) {
                        ToastUtil.showToast(getApplicationContext(),"保存失败:"+e.getMessage());
                    }
                });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_navigation_back_info:
                //bug
                if(!isLogin){
                    finish();
                }
                else if(curage.equals(tvage.getText().toString())&&curgender==gender&&curusername.equals(etusername.getText().toString())&&curstyle.equals(tvstyle.getText().toString())){
                    finish();
                }else{
                    new XPopup.Builder(this).asConfirm("", "是否保存修改？",
                            new OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    if(isLogin){
                                        asyncUpdateWithXHttp2(telephone,etusername.getText().toString(), tvage.getText().toString(),gender+"",style+"");
                                        saveLocal();
                                    }
                                    else {
                                        finish();
                                    }

                                }
                            }, new OnCancelListener() {
                                @Override
                                public void onCancel() {
                                    finish();
                                }
                            })
                            .show();
                }
                break;
            case R.id.info_age:
                if(isLogin){
                    etusername.clearFocus();
                    pvCustomOptions.show();
                }
                break;
            case R.id.info_gender:
                new XPopup.Builder(this)
                        .atView(view)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                        .asAttachList(new String[]{ "女","男"},
                                new int[]{ R.mipmap.famale,R.mipmap.male},
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, String text) {

                                        gender=position;
                                        tvgenger.setText(text);
                                    }
                                })
                        .show();

                break;
            case R.id.info_style:
                new XPopup.Builder(this)
                    .atView(view)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                    .asAttachList(new String[]{ "贴身","较贴身","较宽松","宽松"},
                            new int[]{},
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {

                                    style=position;
                                    tvstyle.setText(text);
                                }
                            })
                    .show();

                break;
            case R.id.info_username:
                if(isLogin){
                    tvage.clearFocus();
                    etusername.setFocusableInTouchMode(true);
                    etusername.requestFocus();
                    InputMethodManager inputManager =
                            (InputMethodManager)etusername.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.showSoftInput(etusername, 0);
                }
                break;
        }
    }

    private void initCustomOptionPicker() {//条件选择器初始化，自定义布局
        /**
         * @description
         *
         * 注意事项：
         * 自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针。
         * 具体可参考demo 里面的两个自定义layout布局。
         */
        pvCustomOptions = new OptionsPickerBuilder(MyinfoActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = ages.get(options1).getPickerViewText().split("岁")[0];
                tvage.setText(tx);



                //mMyHandler.sendEmptyMessage(TOAST_MSG);
            }
        })
                .setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final LinearLayout confirm =  v.findViewById(R.id.ll_confirm);
                        final LinearLayout cancel = v.findViewById(R.id.ll_cancel);
                        TextView title=v.findViewById(R.id.tv_title);
                        title.setText("年龄");

                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.returnData();
                                pvCustomOptions.dismiss();
                            }
                        });

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.dismiss();
                            }
                        });

                    }
                })
                .isDialog(true)
                .setOutSideCancelable(false)
                .setItemVisibleCount(6)
                .setSelectOptions(18)
                .build();
        getAgeData();
        pvCustomOptions.setPicker(ages);//添加数据


    }
    private void getAgeData() {
        for (int i = 0; i <= 120; i++) {
            ages.add(new AgeBean(i,  i+"岁"));
        }


    }

}
