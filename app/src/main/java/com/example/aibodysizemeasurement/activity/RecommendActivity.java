package com.example.aibodysizemeasurement.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.aibodysizemeasurement.R;
import com.example.aibodysizemeasurement.application.MyApplication;
import com.example.aibodysizemeasurement.bean.UserVO;
import com.example.aibodysizemeasurement.utils.ModelConstant;
import com.example.aibodysizemeasurement.constant.NetConstant;
import com.example.aibodysizemeasurement.utils.ToastUtil;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;

import static java.lang.Math.abs;
import static java.lang.Math.min;

public class RecommendActivity extends AppCompatActivity {

    private double height,chest,bu,shoulder,waist,crotch,shoulderRound;
    private boolean isLogin;
    private TextView tvrecommend0,tvrecommend1,tvbodytype,tvrecommend2,tvrecommend3,tvrecommend4,test;
    private double[] type =new double[5];;//Vtype0,Otype1,Xtype2,Htype3,Atype4;
    private ImageButton back;
    private UserVO userVO;
    private String t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        isLogin= MyApplication.getInstance().isLogin();
        test=findViewById(R.id.tv_test);
        back=findViewById(R.id.ib_navigation_back_info);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvrecommend0=findViewById(R.id.tv_recommend_0);
        tvrecommend1=findViewById(R.id.tv_recommend_1);
        tvrecommend2=findViewById(R.id.tv_recommend_2);
        tvrecommend3=findViewById(R.id.tv_recommend_3);
        tvrecommend4=findViewById(R.id.tv_recommend_4);
        tvbodytype=findViewById(R.id.body_type);
        getSizeData(isLogin);
        if(shoulder==0||bu==0||waist==0||chest==0){
            ToastUtil.showToast(getApplication(),"请先进行尺寸测量！");
        }else {
            checkBodyType();
        }

    }

    //判断体型
    private void checkBodyType() {
        if(checkX()){

            showX();
        }else if(checkH()){

            showH();
        }else if(checkO()){

            showO();
        }else if(checkA()){

            showA();
        }else if(checkV()){

            showV();
        }else {

            double min=999;
            int t=-1;
            for(int i=0;i<5;i++){
                if(type[i]<min){
                    min=type[i];
                    t=i;
                }
            }
            switch (t){
                case 0:
                    showV();
                    break;
                case 1:
                    showO();
                    break;
                case 2:
                    showX();
                    break;
                case 3:
                    showH();
                    break;
                case 4:
                    showA();
                    break;
            }
        }
    }

    private void showX() {
        tvbodytype.setText("X型");
        tvrecommend0.setText("   您是比较理想的体型，胸部和臀部比较均衡，腰部曲线明显。");
        SpannableStringBuilder style = new SpannableStringBuilder("上装：按照具体比例适当修饰胸部和臀部，同时突出腰部。");
        style.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tvrecommend1.setText(style);

        tvrecommend2.setText("   ★正装：神身材曲线和硬挺的服装轮廓产生一种冲突魅力；" +
                "\n   ★修身款上衣:修身剪裁的衬衫、夹克；" +
                "\n   ★收腰设计的上衣:收腰的外套，进一步突出完美腰线；" );
        SpannableStringBuilder style1 = new SpannableStringBuilder("下装：搭配得当，可以驾驭任何裤子和裙子。选择宽阔的裤子可以掩饰腿部" +
                "的小缺点,修身的裙裤则能凸显比例；");
        style1.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tvrecommend3.setText(style1);
        tvrecommend4.setText("   ★高腰裤或者铅笔裤；\n   ★修身牛仔裤；\n   ★" +
                "高腰包臀鱼尾裙；\n   ★A字裙；\n   ★阔腿裤或者直简裤。");
    }

    private void showH() {
        tvbodytype.setText("H型");
        tvrecommend0.setText("   您的身材曲线不太明显，整体比较扁平。" );
        SpannableStringBuilder style = new SpannableStringBuilder("上装：");
        style.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tvrecommend1.setText(style);

        tvrecommend2.setText("   ★修饰肩部：可以选择V领、船形领、一字领等的上衣，也可以选" +
                "择泡泡袖、飞飞袖的上衣增加肩部视觉感，同时手臂看起来更细；\n   ★修饰腰部：" +
                "可以选择收腰设计的衬衫，或者选择外廓的下摆的衣服，搭配腰带；\n   ★修饰胸" +
                "部：可以选择胸部周围有聚拢线条设计能增大胸围的衣服，或者用口袋的细节将" +
                "注意力引导胸部。" );
        SpannableStringBuilder style1 = new SpannableStringBuilder("下装：");
        style1.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tvrecommend3.setText(style1);

        tvrecommend4.setText("   ★工装裤：带有后口袋的工装裤；" +
                "\n   ★阔腿裤：裤腿适当外扩的" +
                "裤子；\n ★A型裙；\n   ★中腰或者低腰的下装，配上宽腰带；\n   ★带有猫须细节、" +
                "装饰口袋或翻盖口袋。");
    }

    private void showO() {
        tvbodytype.setText("O型");
        tvrecommend0.setText("您的腰部与臀部较宽阔，整体丰满圆润。"  );
        SpannableStringBuilder style = new SpannableStringBuilder("上装：弱化肩部，提高腰线，以款式简洁宽松的深色服饰为宜。");
        style.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tvrecommend1.setText(style);
        tvrecommend2.setText("   ★短袖：比起无袖衫，可弱化肩部线条；\n   ★长款宽松外套：茧型上衣、宽松开衫都" +
                "能起到很好的遮盖作用。\n   ★短上装：提高腰线，平衡身材比例。" );
        SpannableStringBuilder style1 = new SpannableStringBuilder("下装：高腰设计可以修饰身材，提高腰线，将腰腹部的线条加以视觉平衡。");
        style1.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tvrecommend3.setText(style1);
        tvrecommend4.setText("   ★超高腰长裤；\n   ★长半身裙。" );
    }

    private void showA() {
        tvbodytype.setText("A型");
        tvrecommend0.setText("   您的肩较较窄，腰部较细，下半身宽阔。"  );
        SpannableStringBuilder style = new SpannableStringBuilder("上装：与矩形类似，需要增大上半部分的体积感。");
        style.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tvrecommend1.setText(style);
        tvrecommend2.setText("   ★修饰肩部：船型领" +
                "或者一字领让肩部视觉膨胀，平衡上下对比\n   ★修饰胸部:有胸部装饰或设计" +
                "的衣服，配合衬垫内衣，也可以选择挺廓型的上衣，塑造更佳挺拔的上半身；\n   ★" +
                "修饰腰部：可以选择修身上衣，通过强调腰部强调上半身存在感。" );
        SpannableStringBuilder style1 = new SpannableStringBuilder("下装：减少下半身存在感，让视觉收缩。尽量选取简单干净的款式，无需太多细节。");
        style1.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tvrecommend3.setText(style1);

        tvrecommend4.setText("   ★A字裙：能让下半身看起来更加纤细; \n   ★0型裙：在不增加体积" +
                "的情况下能遮盖臀部； \n   ★直筒裤或者阔腿裤：利用视觉延伸原理，最大程度减" +
                "小臀部曲线：\n   ★深色水洗直筒牛仔裤,避免猫须等细节或其它过于抢眼的臀部装饰。");
    }

    private void showV() {
        tvbodytype.setText("V型");
        tvrecommend0.setText("您的肩膀宽阔，下半身相较肩部偏细窄。" );

        SpannableStringBuilder style = new SpannableStringBuilder("上装：需要平衡宽阔的肩膀、胸部与纤细的下体。");
        style.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tvrecommend1.setText(style);
        tvrecommend2.setText("   ★修饰肩部：可以" +
                "选择V领上衣，避免垫肩的衣服；\n   ★修饰胸部：避免胸部口袋会和过多的装" +
                "饰；\n   ★修饰腰部：可以选择略带收腰设计的上衣和外廓的下摆，可以增加臀部" +
                "空间，塑造沙漏型；\n   ★修饰细节：可以选择竖条纹的上衣，打破横向视觉。");

        SpannableStringBuilder style1 = new SpannableStringBuilder("下装：减少下半身存在感，让视觉收缩。尽量选取简单干净的款式，无需太" +
                "多细节。");
        style1.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tvrecommend3.setText(style1);

        tvrecommend4.setText("   ★A字裙：宽阔的下摆空间可以扩大下肢的视觉效果，也会让腿看起" +
                "来纤细；\n   ★直筒裤或者阔腿裤；\n   ★多口袋的工裝裤:臀部多的装饰的裤子能" +
                "够平衡上下体的视觉效果；\n   ★避免小脚裤、铅笔裤、铅笔裙。");
    }

    //判断V型体型
    private boolean checkV() {
        if((shoulderRound-bu)/shoulderRound>0.05||(chest-bu)/chest>0.05){
            return true;
        }
        else{
            type[0]=0.05-((shoulderRound-bu)/shoulderRound+(chest-bu)/chest)/2;
            return false;
        }
    }

    //判断A型体型
    private boolean checkA() {
        if((bu-shoulderRound)/bu>0.05||(bu-chest)/bu>0.05){
            return true;
        }
        else{
            type[4]=0.05-((bu-shoulderRound)/bu+(bu-chest)/bu)/2;
            return false;
        }
    }

    //判断O型体型
    private boolean checkO() {
        if(waist>shoulderRound&&waist>bu){
            return true;
        }
        else {
            type[1]=((shoulderRound-waist)/shoulderRound+(bu-waist)/bu)/2;
            return false;
        }
    }

    //判断H型体型
    private boolean checkH() {
        if((abs(shoulderRound-chest)/shoulderRound<0.05&&abs(shoulderRound-bu)/shoulderRound<0.05&&abs(bu-chest)/bu<0.05)){
            return true;
        }else if((abs(shoulderRound-waist)/shoulderRound<0.2&&abs(bu-waist)/bu<0.2)){
            return true;
        }
        else{
            double Htype1 = ((shoulderRound - chest) / shoulderRound + (shoulderRound - bu) / shoulderRound + (bu - chest) / bu) / 3 - 0.05;
            double Htype2 = ((shoulderRound - waist) / shoulderRound + (bu - waist) / bu) / 2 - 0.2;
            type[3]=min(Htype1,Htype2);
            return false;
        }
    }

    //判断X型体型
    private boolean checkX() {
        if((shoulderRound-waist)/shoulderRound>0.2&&(chest-waist)/chest>0.2&&(bu-waist)/bu>0.2){
            return true;
        }
        else {
            type[2]=0.2-(((shoulderRound-waist)/shoulderRound)+((chest-waist)/chest)+((bu-waist)/bu));
            return false;
        }
    }


    private void getSizeData(boolean isLogin) {
        if(isLogin){
            SharedPreferences sp = getSharedPreferences(ModelConstant.LOGIN_INFO, MODE_PRIVATE);
            String telephone = sp.getString("telephone", "");
            height=MyApplication.getInstance().userVO.getHeight();
            bu=MyApplication.getInstance().userVO.getButto();
            chest=MyApplication.getInstance().userVO.getChest();
            crotch=MyApplication.getInstance().userVO.getCrotch();
            shoulder=MyApplication.getInstance().userVO.getShoulder();
            waist=MyApplication.getInstance().userVO.getWaist();
            shoulderRound=shoulder*2+5;
            //getSizeLogin(telephone);
        }else{
            getSizeNotLogin();
        }
    }


    /**
     * 本地获取尺寸
     */
    private void getSizeNotLogin() {
        SharedPreferences sp = getSharedPreferences(ModelConstant.SIZE_INFO, MODE_PRIVATE);
        height=sp.getFloat("height",0f);
        bu=sp.getFloat("butto",0f);
        chest=sp.getFloat("chest",0f);
        crotch=sp.getFloat("crotch",0f);
        shoulder=sp.getFloat("shoulder",0f);
        waist=sp.getFloat("waist",0f);
        shoulderRound=shoulder*2+5;
    }

    /**在线获取尺寸
     * @param telephone
     */
    private void getSizeLogin(final String telephone){

        XHttp.post(NetConstant.getGetSizeURL())
                .params("telephone", telephone)
                .syncRequest(false)
                .execute(new SimpleCallBack<UserVO>() {
                    @Override
                    public void onSuccess(UserVO data) throws Throwable {
                        height=data.getHeight();
                        bu=data.getButto();
                        chest=data.getChest();
                        crotch=data.getCrotch();
                        shoulder=data.getShoulder();
                        waist=data.getWaist();
                        shoulderRound=shoulder*2+5;
                    }

                    @Override
                    public void onError(ApiException e) {
                        //在线获取失败则本地获取
                        getSizeNotLogin();
                    }
                });
    }
}
