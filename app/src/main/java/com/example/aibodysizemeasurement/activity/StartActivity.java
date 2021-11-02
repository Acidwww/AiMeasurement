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

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aibodysizemeasurement.R;
import com.example.aibodysizemeasurement.adapter.MyViewFragmentAdapter;
import com.example.aibodysizemeasurement.application.MyApplication;
import com.example.aibodysizemeasurement.fragment.HomeFragment;
import com.example.aibodysizemeasurement.fragment.MyFragment;

import com.example.aibodysizemeasurement.popup.CustomCenterPopup;
import com.example.aibodysizemeasurement.utils.ModelConstant;
import com.example.aibodysizemeasurement.utils.ToastUtil;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.SimpleCallback;


import java.util.ArrayList;
import java.util.List;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public final class StartActivity extends BaseActivity
        implements OnRequestPermissionsResultCallback, View.OnClickListener {
    private static final String TAG = "StartActivity";
    public static final String API_KEY = "client/api_key";
    private static final int PERMISSION_REQUESTS = 1;


    // Template (including the quantity provided by the SDK and that manually generated)
    private static int mCount = 0;
    private boolean isLogin;
    private ViewPager2 viewpage;

    private LinearLayout llhome,llmy,llcr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_start);
        isLogin= MyApplication.getInstance().isLogin();
        
        initView();

        // Set the ApiKey of the application for accessing cloud services.
        setApiKey();
        if (!this.allPermissionsGranted()) {
            this.getRuntimePermissions();
        }
        checkInfo();

    }

    private void checkInfo() {
        int gender;
        double height;
        if(isLogin){
            gender=MyApplication.getInstance().userVO.getGender();
            height=MyApplication.getInstance().userVO.getHeight();
        }else{
            SharedPreferences sp = getSharedPreferences(ModelConstant.SIZE_INFO, MODE_PRIVATE);
            gender = sp.getInt("gender", -1);
            height = sp.getFloat("height", 0f);
        }

        if(gender==-1||height==0f){
            new XPopup.Builder(StartActivity.this).dismissOnTouchOutside(false)
                    .autoOpenSoftInput(false).setPopupCallback(new SimpleCallback() { //设置显示和隐藏的回调
                @Override
                public void onCreated(BasePopupView v) {
                    // 弹窗内部onCreate执行完调用
                }

                @Override
                public void beforeShow(BasePopupView v) {
                    super.beforeShow(v);
                    Log.e("tag", "beforeShow，在每次show之前都会执行，可以用来进行多次的数据更新。");
                }

                @Override
                public void onShow(BasePopupView v) {
                    // 完全显示的时候执行
                }

                @Override
                public void onDismiss(BasePopupView v) {
                    // 完全隐藏的时候执行

                }

                //如果你自己想拦截返回按键事件，则重写这个方法，返回true即可
                @Override
                public boolean onBackPressed(BasePopupView v) {


                    return true; //默认返回false
                }
            })
//                        .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                    .asCustom(new CustomCenterPopup(this))
                    .show();
        }

    }


    private void initView() {
        llhome=findViewById(R.id.ll_home);
        llmy=findViewById(R.id.ll_my);
        llhome.setOnClickListener(this);
        llmy.setOnClickListener(this);
        llhome.setSelected(true);
        llcr=llhome;
        viewpage = findViewById(R.id.view_page);

        List<Fragment> fragmentList=new ArrayList<>();
        fragmentList.add(HomeFragment.newInstance());
        fragmentList.add(MyFragment.newInstance());
        MyViewFragmentAdapter myadapter = new MyViewFragmentAdapter(getSupportFragmentManager(),getLifecycle(),fragmentList);
        viewpage.setAdapter(myadapter);
        viewpage.setUserInputEnabled(false);//设置不可滑动
        viewpage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                changeTable(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

        });
    }

    private void changeTable(int position) {
        switch (position){
            case R.id.ll_home:
                viewpage.setCurrentItem(0);
                //case 0:
                llcr.setSelected(false);
                llhome.setSelected(true);

                llcr=llhome;

                break;
            case R.id.ll_my:
                viewpage.setCurrentItem(1);
                //case 1:
                llcr.setSelected(false);
                llmy.setSelected(true);
                llcr=llmy;
                break;
        }
    }



    /**
     * Read the ApiKey field in the sample-agconnect-services.json to obtain the API key of the application and set it.
     * For details about how to apply for the agconnect-services.json, see section https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-add-agc.
     */
    private void setApiKey(){
        AGConnectServicesConfig config = AGConnectServicesConfig.fromContext(getApplication());
        MLApplication.getInstance().setApiKey(config.getString(API_KEY));
    }



    @Override
    public void onClick(View view) {
        changeTable(view.getId());
    }



    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : this.getRequiredPermissions()) {
            if (!StartActivity.isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : this.getRequiredPermissions()) {
            if (!StartActivity.isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), StartActivity.PERMISSION_REQUESTS);
        }
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != StartActivity.PERMISSION_REQUESTS) {
            return;
        }
        boolean isNeedShowDiag = false;
        for (int i = 0; i < permissions.length; i++) {
            if ((permissions[i].equals(Manifest.permission.CAMERA) && grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    || (permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[i] != PackageManager.PERMISSION_GRANTED)) {
                // If the camera or storage permissions are not authorized, need to pop up an authorization prompt box.
                isNeedShowDiag = true;
            }
        }
        if (isNeedShowDiag && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(this.getString(R.string.camera_permission_rationale))
                    .setPositiveButton(this.getString(R.string.settings), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            // Open the corresponding setting interface according to the package name.
                            intent.setData(Uri.parse("package:" + StartActivity.this.getPackageName()));
                            StartActivity.this.startActivityForResult(intent, 200);
                            StartActivity.this.startActivity(intent);
                        }
                    })
                    .setNegativeButton(this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StartActivity.this.finish();
                        }
                    }).create();
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (!this.allPermissionsGranted()) {
                this.getRuntimePermissions();
            }
        }
    }



    public static int getCount() {
        return mCount;
    }

    public static void setCount() {
        StartActivity.mCount = StartActivity.mCount + 1;
    }






}
