package com.example.aibodysizemeasurement.popup;

import android.content.Context;


import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.aibodysizemeasurement.R;
import com.example.aibodysizemeasurement.activity.HumanSkeletonActivity;
import com.example.aibodysizemeasurement.bean.HeightBean;
import com.example.aibodysizemeasurement.utils.ModelConstant;
import com.lxj.xpopup.core.BottomPopupView;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class CustomButtomPopup extends BottomPopupView {


    private LinearLayout llcamera;
    // 声明SharedPreferences对象
    SharedPreferences sp;
    // 声明SharedPreferences编辑器对象
    SharedPreferences.Editor editor;

    private OptionsPickerView pvCustomOptions;
    private ArrayList<HeightBean> heights = new ArrayList<>();
    private double height=0;

    public CustomButtomPopup(@NonNull Context context) {
        super(context);
    }


    @Override
    protected int getImplLayoutId() {
        return R.layout.custom_buttom_popup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        llcamera=findViewById(R.id.pop_camera);
        llcamera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(), HumanSkeletonActivity.class);
                //startActivityForResult(intent,REQUEST_CAPTURE);
                getContext().startActivity(intent);
                dismiss();            }
        });

        sp = getContext().getSharedPreferences(ModelConstant.SIZE_INFO, MODE_PRIVATE);
        editor = sp.edit();
    }

    @Override
    protected void onShow() {
        super.onShow();


    }

    @Override
    protected void onDismiss() {
        super.onDismiss();

    }




}
