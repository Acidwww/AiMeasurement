package com.example.aibodysizemeasurement.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aibodysizemeasurement.R;
import com.example.aibodysizemeasurement.application.MyApplication;
import com.example.aibodysizemeasurement.bean.DemoBean;
import com.example.aibodysizemeasurement.bean.UserVO;
import com.example.aibodysizemeasurement.callback.ImageUtilCallBack;
import com.example.aibodysizemeasurement.utils.ExcelUtil;
import com.example.aibodysizemeasurement.utils.ImageUtils;
import com.example.aibodysizemeasurement.utils.ModelConstant;
import com.example.aibodysizemeasurement.constant.NetConstant;
import com.example.aibodysizemeasurement.utils.ToastUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MyDataActivity extends AppCompatActivity implements  View.OnClickListener{

    private ImageButton mIbNavigationBack,more;
    private boolean isLogin;
    private LinearLayout lldata;
    private TextView tvheight,tvchest,tvbu,tvshoulder,tvwaist,tvcrotch;

    private String height;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_data);
        lldata=findViewById(R.id.ll_data);
        tvheight=findViewById(R.id.data_height);
        tvbu=findViewById(R.id.data_bu);
        tvshoulder=findViewById(R.id.data_shoulder);
        tvchest=findViewById(R.id.data_chest);
        tvwaist=findViewById(R.id.data_waist);
        tvcrotch=findViewById(R.id.data_leg);
        //?????????+????????????
        more = findViewById(R.id.ib_navigation_more_data);
        mIbNavigationBack = findViewById(R.id.ib_navigation_back_data);
        mIbNavigationBack.setOnClickListener(this);
        more.setOnClickListener(this);
        isLogin= MyApplication.getInstance().isLogin();
        getSizeData(isLogin);
    }

    private void getSizeData(boolean isLogin) {
        if(isLogin){
            setInfo();
//            SharedPreferences sp = getSharedPreferences(ModelConstant.LOGIN_INFO, MODE_PRIVATE);
//            String telephone = sp.getString("telephone", "");
//            getSizeLogin(telephone);
        }else{
            getSizeNotLogin();
        }
    }

    private void setInfo() {
        tvheight.setText(MyApplication.getInstance().userVO.getHeight()+"");
        tvbu.setText(MyApplication.getInstance().userVO.getButto()+"");
        tvchest.setText(MyApplication.getInstance().userVO.getChest()+"");
        tvcrotch.setText(MyApplication.getInstance().userVO.getCrotch()+"");
        tvshoulder.setText(MyApplication.getInstance().userVO.getShoulder()+"");
        tvwaist.setText(MyApplication.getInstance().userVO.getWaist()+"");
    }

    /**
     * ??????????????????
     */
    private void getSizeNotLogin() {
        SharedPreferences sp = getSharedPreferences(ModelConstant.SIZE_INFO, MODE_PRIVATE);
        tvheight.setText(sp.getFloat("height",0f)+"");
        tvbu.setText(sp.getFloat("butto",0f)+"");
        tvchest.setText(sp.getFloat("chest",0f)+"");
        tvcrotch.setText(sp.getFloat("crotch",0f)+"");
        tvshoulder.setText(sp.getFloat("shoulder",0f)+"");
        tvwaist.setText(sp.getFloat("waist",0f)+"");
    }


    /**??????????????????
     * @param telephone
     */
    private void getSizeLogin(final String telephone){
        // ??????
        XHttp.post(NetConstant.getGetSizeURL())
                .params("telephone", telephone)
                .syncRequest(false)
                .execute(new SimpleCallBack<UserVO>() {
                    @Override
                    public void onSuccess(UserVO data) throws Throwable {
                        height=data.getHeight()+"";
                        tvheight.setText(data.getHeight()+"");
                        tvbu.setText(data.getButto()+"");
                        tvchest.setText(data.getChest()+"");
                        tvcrotch.setText(data.getCrotch()+"");
                        tvshoulder.setText(data.getShoulder()+"");
                        tvwaist.setText(data.getWaist()+"");
                    }

                    @Override
                    public void onError(ApiException e) {
                        //?????????????????????????????????
                        getSizeNotLogin();
                    }
                });
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_navigation_back_data:
                //??????
                finish();
                break;
            case R.id.ib_navigation_more_data:
                new XPopup.Builder(this)
                        .atView(view)  // ?????????????????????View???????????????????????????????????????????????????
                        .asAttachList(new String[]{ "??????????????????"},
                                new int[]{},
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, String text) {

                                            Bitmap bitmap=getViewBitmapNoBg(lldata);
                                            saveImg(bitmap);
                                            ToastUtil.showToast(getApplication(),"???????????????");


                                    }
                                })
                        .show();
                break;
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param view view
     * @return Bitmap
     */
    public static Bitmap getViewBitmapNoBg(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        // clear drawing cache
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    /**
     * @param view ?????????????????????view??????????????????
     * @return Bitmap
     */
    public static Bitmap getViewBitmap(Activity activity, View view) {
        View screenView = activity.getWindow().getDecorView();
        screenView.setDrawingCacheEnabled(true);
        screenView.buildDrawingCache();

        //????????????????????????
        Bitmap bitmap = screenView.getDrawingCache();

        if (bitmap != null) {
            //????????????????????????
            int outWidth = view.getWidth();
            int outHeight = view.getHeight();

            //????????????????????????????????????????????????(view?????????????????????
            int[] viewLocationArray = new int[2];
            view.getLocationOnScreen(viewLocationArray);

            //??????????????????????????????????????????
            bitmap = Bitmap.createBitmap(bitmap, viewLocationArray[0], viewLocationArray[1], outWidth, outHeight);
        }
        return bitmap;
    }
    private void saveImg(Bitmap bitmap) {
        ImageUtils imageUtils = new ImageUtils(MyDataActivity.this.getApplicationContext());

        imageUtils.saveToAlbum(bitmap);
    }




}
