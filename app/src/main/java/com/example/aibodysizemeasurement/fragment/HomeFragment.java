package com.example.aibodysizemeasurement.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.aibodysizemeasurement.R;
import com.example.aibodysizemeasurement.activity.HumanSkeletonActivity;
import com.example.aibodysizemeasurement.activity.MyDataActivity;
import com.example.aibodysizemeasurement.activity.MyLoginActivity;
import com.example.aibodysizemeasurement.activity.RecommendActivity;
import com.example.aibodysizemeasurement.activity.SettingActivity;
import com.example.aibodysizemeasurement.activity.StartActivity;
import com.example.aibodysizemeasurement.adapter.ImageAdapter;
import com.example.aibodysizemeasurement.application.MyApplication;
import com.example.aibodysizemeasurement.bean.DataBean;

import com.example.aibodysizemeasurement.popup.CustomButtomPopup;
import com.example.aibodysizemeasurement.utils.ToastUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.SimpleCallback;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.transformer.ZoomOutPageTransformer;


public class HomeFragment extends Fragment implements View.OnClickListener{

    private RelativeLayout capture,recommend,mydata;
    private OnFragmentInteractionListener mListener;
    private View rootview;
    private Banner banner;
    private boolean isLogin;
    private Context mcontext;


    public HomeFragment() {
        // Required empty public constructor
    }



    public static HomeFragment newInstance(){
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isLogin = MyApplication.getInstance().isLogin();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootview == null) {

            rootview = inflater.inflate(R.layout.fragment_home, container, false);
        }

        capture=rootview.findViewById(R.id.capture_body);
        mydata=rootview.findViewById(R.id.home_mydata);
        recommend=rootview.findViewById(R.id.recommend_size);
        banner=rootview.findViewById(R.id.banner);
        capture.setOnClickListener(this);
        recommend.setOnClickListener(this);
        mydata.setOnClickListener(this);
        initBanner();
        // Inflate the layout for this fragment
        return rootview;
    }

    private void initBanner() {
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(banner.getLayoutParams());
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        layoutParams.width=screenWidth-60;
        layoutParams.height=(int)((screenWidth)*6f/13);
        layoutParams.leftMargin=30;
        layoutParams.rightMargin=30;
        layoutParams.gravity= Gravity.CENTER_HORIZONTAL;
        banner.setLayoutParams(layoutParams);
        banner.addBannerLifecycleObserver(this)//???????????????????????????
                .setAdapter(new ImageAdapter(DataBean.getTestData()))
                .setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(Object data, int position) {
                        switch(position){
                            case 0:
                                showTips();

                                break;
                            case 1:
                                Intent intent=new Intent(mcontext, RecommendActivity.class);
                                intent.putExtra("isLogin",RecommendActivity.class);
                                startActivity(intent);
                                break;
                            case 2:
                                ToastUtil.showToast(getContext(),"????????????");
                                break;
                        }
                    }
                })
                .setIndicator(new CircleIndicator(mcontext)).addPageTransformer(new ZoomOutPageTransformer());
    }

    //??????????????????
    private void showTips() {
        new XPopup.Builder(getActivity())
                .autoOpenSoftInput(false).setPopupCallback(new SimpleCallback() { //??????????????????????????????
            @Override
            public void onCreated(BasePopupView v) {
                // ????????????onCreate???????????????
            }

            @Override
            public void beforeShow(BasePopupView v) {
                super.beforeShow(v);
                Log.e("tag", "beforeShow????????????show???????????????????????????????????????????????????????????????");
            }

            @Override
            public void onShow(BasePopupView v) {
                // ???????????????????????????
            }

            @Override
            public void onDismiss(BasePopupView v) {
                // ???????????????????????????

            }

            //???????????????????????????????????????????????????????????????????????????true??????
            @Override
            public boolean onBackPressed(BasePopupView v) {


                return true; //????????????false
            }
        })
//                        .isDestroyOnDismiss(true) //???????????????????????????????????????????????????
                .asCustom(new CustomButtomPopup(getContext()))
                .show();
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mcontext = context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.capture_body:
                showTips();
                break;
            case R.id.recommend_size:
                Intent intent=new Intent(mcontext, RecommendActivity.class);
                startActivity(intent);
                break;
            case R.id.home_mydata:
                intent=new Intent(mcontext, MyDataActivity.class);
                startActivity(intent);

                break;
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
