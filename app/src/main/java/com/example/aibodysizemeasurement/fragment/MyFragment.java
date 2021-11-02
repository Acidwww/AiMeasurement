package com.example.aibodysizemeasurement.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.aibodysizemeasurement.R;
import com.example.aibodysizemeasurement.activity.MyDataActivity;
import com.example.aibodysizemeasurement.activity.MyLoginActivity;
import com.example.aibodysizemeasurement.activity.MyinfoActivity;
import com.example.aibodysizemeasurement.activity.SettingActivity;
import com.example.aibodysizemeasurement.application.MyApplication;
import com.example.aibodysizemeasurement.bean.UserVO;
import com.example.aibodysizemeasurement.constant.NetConstant;
import com.example.aibodysizemeasurement.utils.ModelConstant;
import com.example.aibodysizemeasurement.utils.ToastUtil;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;

import static android.content.Context.MODE_PRIVATE;


public class MyFragment extends Fragment implements View.OnClickListener{


    private View rootview;
    private String telephone;
    private Context mcontext;
    private TextView userName;
    private ImageView headPic;
    private LinearLayout login;
    private static final int SHOW_USERNAME=101;
    private static final int SHOW_NOTLOGIN=102;
    private RelativeLayout mydata,personinfo,exit,setting;
    private OnFragmentInteractionListener mListener;
    private boolean isLogin;

    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case SHOW_USERNAME:
                    userName.setText(msg.getData().getString("username"));
                    break;
                case SHOW_NOTLOGIN:
                    userName.setText(msg.getData().getString("nologin"));
                    break;
            }

            return false;
        }
    });


    public MyFragment() {
        // Required empty public constructor
    }



    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isLogin = MyApplication.getInstance().isLogin();
            SharedPreferences sp = getActivity().getSharedPreferences(ModelConstant.LOGIN_INFO, MODE_PRIVATE);
            telephone = sp.getString("telephone", "");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootview == null) {

            rootview = inflater.inflate(R.layout.fragment_my, container, false);
        }
        headPic=rootview.findViewById(R.id.head_pic);
        login=rootview.findViewById(R.id.login_now);
        userName=rootview.findViewById(R.id.my_username);
        mydata=rootview.findViewById(R.id.my_data);
        personinfo=rootview.findViewById(R.id.personinfo);
        setting=rootview.findViewById(R.id.setting);
        exit=rootview.findViewById(R.id.exit);
        //设置点击监听
        setOnclickListener();
        //获取用户名


        // Inflate the layout for this fragment
        return rootview;
    }

    private void setOnclickListener() {
        userName.setOnClickListener(this);
        login.setOnClickListener(this);
        mydata.setOnClickListener(this);
        personinfo.setOnClickListener(this);
        setting.setOnClickListener(this);
        exit.setOnClickListener(this);
        headPic.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isLogin){
            userName.setText(MyApplication.getInstance().userVO.getName());
            //asyncGetUserWithXHttp2(telephone);
        }else {
            login.setVisibility(View.VISIBLE);
            userName.setText("未登录");
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mcontext=context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.head_pic:
            case R.id.my_username:
                if(!isLogin){
                    startActivity(new Intent(mcontext, MyLoginActivity.class));
                }
                break;
            case R.id.my_data:
                Intent intent=new Intent(mcontext, MyDataActivity.class);
                startActivity(intent);
                break;
            case R.id.personinfo:
                intent=new Intent(mcontext, MyinfoActivity.class);
                startActivity(intent);
                break;
            case R.id.setting:
                intent=new Intent(mcontext, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.exit:
                if(!isLogin){
                    ToastUtil.showToast(mcontext,"未登录！");
                }else{
                    MyApplication.getInstance().setLogin(false);
                    MyApplication.getInstance().reload();
                    userName.setText("未登录");
                    ToastUtil.showToast(mcontext,"退出登录成功！");
                    intent=new Intent("logout.kill.all.activities");
                    getActivity().sendBroadcast(intent);
                    startActivity(new Intent(mcontext,MyLoginActivity.class));

                }
                break;
            case R.id.login_now:
                intent=new Intent(mcontext, MyLoginActivity.class);
                startActivity(intent);
                break;
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
