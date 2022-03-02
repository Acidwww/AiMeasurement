/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.example.aibodysizemeasurement.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.example.aibodysizemeasurement.others.CameraConfiguration;
import com.example.aibodysizemeasurement.others.GraphicOverlay;
import com.example.aibodysizemeasurement.others.LensEngine;
import com.example.aibodysizemeasurement.others.LensEnginePreview;
import com.example.aibodysizemeasurement.transtor.ImageSegmentationTransactor;
import com.example.aibodysizemeasurement.transtor.LocalSketlonTranstor;
import com.example.aibodysizemeasurement.R;
import com.example.aibodysizemeasurement.callback.CameraCallback;
import com.example.aibodysizemeasurement.callback.ImageUtilCallBack;
import com.example.aibodysizemeasurement.utils.DataCleanManager;
import com.example.aibodysizemeasurement.utils.ImageUtils;
import com.example.aibodysizemeasurement.utils.ToastUtil;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationScene;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationSetting;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzer;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerSetting;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;


import java.io.IOException;


/**
 *  HumanSkeleton
 *
 * @since  2020-12-10
 */
public final class HumanSkeletonActivity extends BaseActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, CompoundButton.OnCheckedChangeListener, View.OnClickListener , CameraCallback {
    private static final String TAG = "HumanSkeletonActivity";
    /**
     * Refresh the interface
     */
    public static final int FOOT = 102;

    public static final int CAPTURE_FRONT = 103;

    public static final int CAPTURE_SIDE = 104;

    public static final String SIMILARITY = "similarity";

    private static boolean isSimilarityChecked = true;



    private Bitmap CameraBimap;

    private GraphicOverlay graphicOverlay;

    private Button templateSelect;

    private ImageView people;

    private RelativeLayout templateSimilarityImageLayout;

    private LensEngine lensEngine = null;

    private LensEnginePreview preview;

    private CameraConfiguration cameraConfiguration = null;

    private int facing = CameraConfiguration.CAMERA_FACING_FRONT;

    private Camera mCamera;

    private LocalSketlonTranstor localSketlonTranstor;
    private ImageSegmentationTransactor imageSegmentationTransactor;

    private Handler mHandler = new MsgHandler();

    //  Whether the camera preview interface drawing is asynchronous, if synchronized, frame by frame will be very stuttered
    private static boolean isAsynchronous = true;

    private String FrontImgPath,SideImgPath;

    private Boolean isTokenFront=false;

    private Boolean isTokenSide=false;

    private Bitmap processImage;

    private TextView tvCountDown;

    private LinearLayout llCount,llmention1,llmention2;

    private double footF,footS;

    private boolean isLogin;
    private MLImageSegmentationSetting setting1;

    @Override
    public void callCameraBitmap(Bitmap bitmap) {
        this.processImage=bitmap;
    }

    // Handler Message, to display similarity.
    private class MsgHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //拍摄正面
                case CAPTURE_FRONT:
                    if(!isTokenFront){
                        llmention1.setVisibility(View.GONE);
                        startAnimation(1);
                    }
                    break;
                    //拍摄侧面
                case CAPTURE_SIDE:
                    if(!isTokenSide){
                        llmention2.setVisibility(View.GONE);
                        startAnimation(2);
                    }
                    break;
                case FOOT:
                    if(!isTokenFront&&!isTokenSide){
                        footF=msg.getData().getDouble("foot");
                    }else if(isTokenFront&&!isTokenSide){
                        footS=msg.getData().getDouble("foot");
                    }
            }

        }
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, R.color.black);
        setContentView(R.layout.activity_human_skeleton);
        setIsAsynchronous(true);
        llmention1=findViewById(R.id.llmention_1);
        llmention2=findViewById(R.id.llmention_2);
        llmention2.setVisibility(View.GONE);
        preview = findViewById(R.id.firePreview);
        people=findViewById(R.id.people);
        people.setImageResource(R.drawable.people);
        templateSimilarityImageLayout = findViewById(R.id.similarity_layout);
        templateSimilarityImageLayout.setVisibility(View.VISIBLE);
        tvCountDown=findViewById(R.id.tvCountDown);
        findViewById(R.id.back).setOnClickListener(this);
        graphicOverlay = findViewById(R.id.fireOverlay);
        llCount=findViewById(R.id.llCount);
        ToggleButton facingSwitch = findViewById(R.id.facingSwitch);
        facingSwitch.setOnCheckedChangeListener(this);
        if (Camera.getNumberOfCameras() == 1) {
            facingSwitch.setVisibility(View.GONE);
        }

        this.cameraConfiguration = new CameraConfiguration();
        this.cameraConfiguration.setCameraFacing(CameraConfiguration.CAMERA_FACING_FRONT);
        createLensEngineAndAnalyzer();
    }



    private void createLensEngineAndAnalyzer() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth  = display.getWidth();
        int screenHeight = display.getHeight();
        Log.e("Screen size","wight:"+screenWidth+"hight"+screenHeight);
        if (this.lensEngine == null) {
            this.lensEngine = new LensEngine(this, this.cameraConfiguration, this.graphicOverlay,screenWidth,screenHeight);
        }
        MLSkeletonAnalyzerSetting setting;

        setting = new MLSkeletonAnalyzerSetting.Factory().create();
        Log.i(TAG, "skeletonmode");
        setting1 = new MLImageSegmentationSetting.Factory()
                .setAnalyzerType(MLImageSegmentationSetting.BODY_SEG)
                .setExact(true)
                .setScene(MLImageSegmentationScene.GRAYSCALE_ONLY)
                .create();

        this.localSketlonTranstor = new LocalSketlonTranstor(setting, this, mHandler);
        this.imageSegmentationTransactor = new ImageSegmentationTransactor(this.getApplicationContext(), setting1,localSketlonTranstor);
        this.localSketlonTranstor.setmCameraCallback(this);
        this.lensEngine.setMachineLearningFrameTransactor(localSketlonTranstor,imageSegmentationTransactor);
    }

    private void startLensEngine() {
        if (this.lensEngine != null) {
            try {
                this.preview.start(this.lensEngine, true);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start lensEngine." + e.getMessage());
                this.lensEngine.release();
                this.lensEngine = null;
            }
        }
    }

    public static boolean isAsynchronous() {
        return isAsynchronous;
    }

    public static void setIsAsynchronous(boolean isAsynchronous) {
        HumanSkeletonActivity.isAsynchronous = isAsynchronous;
    }

    @Override
    public void onClick(@NonNull View view) {

        if (view.getId() == R.id.back) {
            DataCleanManager.clearAllCache(getApplicationContext());
            this.lensEngine.release();
            finish();
        }
    }

    //拦截返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){

            this.lensEngine.release();
            DataCleanManager.clearAllCache(getApplicationContext());
            finish();
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.i(TAG, "Set facing");
        if (this.lensEngine != null) {
            if (!isChecked) {
                this.facing = CameraConfiguration.CAMERA_FACING_FRONT;
                this.cameraConfiguration.setCameraFacing(this.facing);
            } else {
                this.facing = CameraConfiguration.CAMERA_FACING_BACK;
                this.cameraConfiguration.setCameraFacing(this.facing);
            }
        }

        this.preview.stop();
        reStartLensEngine();
    }

    private void reStartLensEngine() {
        startLensEngine();
        if (null != this.lensEngine) {
            this.mCamera = this.lensEngine.getCamera();
            try {
                this.mCamera.setPreviewTexture(this.preview.getSurfaceTexture());
            } catch (IOException e) {
                Log.i(TAG, "initViews IOException, " + e.getMessage());
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        preview.stop();
        createLensEngineAndAnalyzer();
        startLensEngine();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        reStartLensEngine();
        isTokenFront=false;
        isTokenSide=false;
        // After returning to the main page, if there is a value selected in the template, it is displayed, otherwise the value of the default template key0 is displayed

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (lensEngine != null) {
            lensEngine.release();
        }


        //setIsAsynchronous(false);
    }

    public static boolean isOpenStatus() {
        return isSimilarityChecked;
    }
    /**
     * 拍完照片后做
     */
    private void goNext() {
        Log.e("Attention berore save file","success");
        //保存图片到相册
        saveImg(2);


        Log.e("Attention after save file","success");
        Intent intent=new Intent(HumanSkeletonActivity.this,SegActivity.class);

        intent.putExtra("front",FrontImgPath);
        intent.putExtra("side",SideImgPath);
        intent.putExtra("foots",footS);
        intent.putExtra("footf",footF);
        startActivity(intent);

    }

    private void saveImg(int tag) {
        ImageUtils imageUtils = new ImageUtils(HumanSkeletonActivity.this.getApplicationContext());
        imageUtils.setImageUtilCallBack(new ImageUtilCallBack() {
            @Override
            public void callSavePath(String path) {
                if(tag==1){
                    HumanSkeletonActivity.this.FrontImgPath = path;
                    Log.i("Humansekeleton", "PATH:" + path);
                }else{
                    HumanSkeletonActivity.this.SideImgPath = path;
                }

            }
        });
        imageUtils.saveToAlbum(HumanSkeletonActivity.this.processImage);
    }

    private void startAnimation(int which) {
        if(which==1){
            isTokenFront=true;
        }else{
            isTokenSide=true;
        }


        final AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(910);//动画持续时间(定义900~1000,也就是1秒左右)
        alphaAnimation.setRepeatMode(Animation.RESTART);
        alphaAnimation.setRepeatCount(2);
        alphaAnimation.setInterpolator(new LinearInterpolator());
        // 设置缩放渐变动画
        final ScaleAnimation scaleAnimation =new ScaleAnimation(0.5f, 1f, 0.5f,1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(910);//动画持续时间(定义900~1000,也就是1秒左右)
        scaleAnimation.setRepeatMode(Animation.RESTART);
        scaleAnimation.setRepeatCount(2);
        scaleAnimation.setInterpolator(new LinearInterpolator());

        AnimationSet animationSet=new AnimationSet(false);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);


        llCount.startAnimation(animationSet);
        //这里 alphAnimation 设置监听，不能用 animationSet 做监听
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            int count=2+1;// 加1为第一次要显示的数字 5
            @Override
            public void onAnimationStart(Animation animation) {// 此方法执行1次
                llCount.setVisibility(View.VISIBLE);
                tvCountDown.setText(""+count);//设置显示的数字
                count--;
            }

            @Override
            public void onAnimationEnd(Animation animation) {// 此方法执行1次

                // 动画结束 隐藏控件
                llCount.setVisibility(View.GONE);
                if(which==1){
                    llmention2.setVisibility(View.VISIBLE);
                    people.setImageResource(R.drawable.cepeople);
                    saveImg(1);
                }else{
                    goNext();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {// 此方法执行4次（repeatCount值）
                tvCountDown.setText(""+count);
                count--;
            }
        });

    }

}
