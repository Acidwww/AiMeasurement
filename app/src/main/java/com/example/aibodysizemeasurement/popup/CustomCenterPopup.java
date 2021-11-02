package com.example.aibodysizemeasurement.popup;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.aibodysizemeasurement.R;
import com.example.aibodysizemeasurement.application.MyApplication;
import com.example.aibodysizemeasurement.constant.NetConstant;
import com.example.aibodysizemeasurement.bean.HeightBean;
import com.example.aibodysizemeasurement.utils.ModelConstant;
import com.example.aibodysizemeasurement.utils.ToastUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class CustomCenterPopup extends CenterPopupView implements View.OnClickListener{

    private RelativeLayout rlgender,rlheight;
    private TextView tvgender,tvheight;
    private LinearLayout llconfirm;
    // 声明SharedPreferences对象
    SharedPreferences sp;
    // 声明SharedPreferences编辑器对象
    SharedPreferences.Editor editor;
    private int gender=-1;
    private OptionsPickerView pvCustomOptions;
    private ArrayList<HeightBean> heights = new ArrayList<>();
    private double height=0;

    public CustomCenterPopup(@NonNull Context context) {
        super(context);
    }


    @Override
    protected int getImplLayoutId() {
        return R.layout.custom_center_popup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        rlgender=findViewById(R.id.pop_gender);
        rlheight=findViewById(R.id.pop_height);
        tvgender=findViewById(R.id.data_gender);
        tvheight=findViewById(R.id.data_height);
        llconfirm=findViewById(R.id.pop_confirm);
        rlheight.setOnClickListener(this);
        rlgender.setOnClickListener(this);
        llconfirm.setOnClickListener(this);
        initCustomOptionPicker();

    }

    @Override
    protected void onShow() {
        super.onShow();


    }

    @Override
    protected void onDismiss() {
        super.onDismiss();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.pop_gender:
                new XPopup.Builder(getContext())
                        .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                        .asAttachList(new String[]{ "女","男"},
                                new int[]{ R.mipmap.famale,R.mipmap.male},
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, String text) {

                                        gender=position;
                                        tvgender.setText(text);
                                    }
                                })
                        .show();
                break;
            case R.id.pop_height:
                pvCustomOptions.show();
                break;
            case R.id.pop_confirm:
                if(height==0||gender==-1){
                    ToastUtil.showToast(getContext(),"请填写完整！");
                }else{
                    sp = getContext().getSharedPreferences(ModelConstant.SIZE_INFO, MODE_PRIVATE);
                    editor = sp.edit();
                    editor.putInt("gender", gender);
                    editor.putFloat("height", (float) height);
                    editor.apply();
                    MyApplication.getInstance().userVO.setHeight(height);
                    MyApplication.getInstance().userVO.setGender(Byte.parseByte(gender+""));
                    asyncUpdateWithXHttp2(MyApplication.getInstance().userVO.getTelephone(),MyApplication.getInstance().userVO.getName(),MyApplication.getInstance().userVO.getAge()+"",gender+"",MyApplication.getInstance().userVO.getStyle()+"");
                    asyncinsertWithXHttp2(MyApplication.getInstance().userVO.getTelephone(),height,0,
                            0,0,0, 0);
                    dismiss();
                }

                break;
        }
    }

    private void asyncinsertWithXHttp2(final String telephone,
                                       final double height,
                                       final double crotch,
                                       final double waist,
                                       final double shoulder,
                                       final double chest,
                                       final double butto){
        //
        XHttp.post(NetConstant.getInsertSizeURL())
                .params("telephone", telephone)
                .params("height", height)
                .params("crotch", crotch)
                .params("waist", waist)
                .params("shoulder", shoulder)
                .params("chest", chest)
                .params("butto", butto)
                .syncRequest(false)
                .execute(new SimpleCallBack<Object>() {
                    @Override
                    public void onSuccess(Object data) throws Throwable {



                    }

                    @Override
                    public void onError(ApiException e) {

                        ToastUtil.showToast(getContext(),"保存失败！");
                    }
                });
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

                    }

                    @Override
                    public void onError(ApiException e) {

                    }
                });

    }

    private void initCustomOptionPicker() {//条件选择器初始化，自定义布局
        /**
         * @description
         *
         * 注意事项：
         * 自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针。
         * 具体可参考demo 里面的两个自定义layout布局。
         */
        pvCustomOptions = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = heights.get(options1).getPickerViewText().split("cm")[0];
                height= Integer.valueOf(tx);
                tvheight.setText(height+"");



                //mMyHandler.sendEmptyMessage(TOAST_MSG);
            }
        })
                .setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final LinearLayout confirm =  v.findViewById(R.id.ll_confirm);
                        final LinearLayout cancel = v.findViewById(R.id.ll_cancel);
                        TextView title=v.findViewById(R.id.tv_title);
                        title.setText("身高");

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
                .setSelectOptions(110)
                .build();
        getHeightData();
        pvCustomOptions.setPicker(heights);//添加数据


    }
    private void getHeightData() {
        for (int i = 60; i <= 250; i++) {
            heights.add(new HeightBean(i,  i+"cm"));
        }


    }
}
