package com.example.aibodysizemeasurement.activity;

import androidx.annotation.NonNull;


import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.os.Message;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.aibodysizemeasurement.bean.DemoBean;
import com.example.aibodysizemeasurement.bean.HeightBean;
import com.example.aibodysizemeasurement.application.MyApplication;
import com.example.aibodysizemeasurement.R;
import com.example.aibodysizemeasurement.transtor.MeasureTranstor;
import com.example.aibodysizemeasurement.utils.ExcelUtil;
import com.example.aibodysizemeasurement.utils.ImageUtils;
import com.example.aibodysizemeasurement.utils.ModelConstant;
import com.example.aibodysizemeasurement.constant.NetConstant;
import com.example.aibodysizemeasurement.utils.ToastUtil;
import com.huawei.hiai.vision.image.segmentation.ImageSegmentation;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentation;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationAnalyzer;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationSetting;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static org.opencv.imgproc.Imgproc.findContours;

public class SegActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "SegActivity";
    private ImageView originFront,originSide,dstF,dstS,opencvf,opencvs;
    private LinearLayout showRecommend;
    private Bitmap dstbmp,frontbmp,sidebmp;
    private Bitmap bitF,bitS;
    private Mat src,srcs;
    private double footSt,footFt;
    private static final int LOADING = 101;
    public static final int START_TASK = 102;
    public static final int TOAST_MSG = 103;
    public static final int SHOW_THRESHOLD=104;

    ImageSegmentation isEngine;
    private RelativeLayout choseHeight;
    private LinearLayout lldata;
    private int realheight=0;
    private MLImageSegmentationAnalyzer analyzer;
    private Bitmap foregroundF,foregroundS;
    private TextView tvheight,tvchest,tvbu,tvshoulder,tvwaist,tvcrotch;
    private OptionsPickerView pvCustomOptions;
    private ArrayList<HeightBean> heights = new ArrayList<>();
    Context context = MyApplication.getInstance();
    private LoadingPopupView loadingPopup=null;

    private boolean haveFinished=false;
    private MeasureTranstor ms=null;
    int width,height;
    private ImageButton mIbNavigationBack,more;
    private String telephone;
    private String chest,butto,crotch,waist,shoulder,watohip,frontchest,armw;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;




    private Handler mMyHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case LOADING:
                    if(loadingPopup==null){
                        loadingPopup = (LoadingPopupView) new XPopup.Builder(SegActivity.this)
                                .dismissOnBackPressed(false)
                                .asLoading("加载中")
                                .show();
                    }else {
                        loadingPopup.show();
                    }
                    break;
                case START_TASK:
                    if(foregroundF!=null&&foregroundS!=null){
                        new MyAsyncTask().execute();
                    }
                    break;
                case TOAST_MSG:


                    chest    = String .format("%.2f",ms.getChest());
                    shoulder = String .format("%.2f",ms.getShoulder());
                    butto = String .format("%.2f",ms.getButto());
                    crotch = String .format("%.2f",ms.getCrotch());
                    waist = String .format("%.2f",ms.getWaist());
                    watohip = String .format("%.2f",ms.getWatohip());
                    frontchest = String .format("%.2f",ms.getFrontchest());
                    armw = String .format("%.2f",ms.getArmw());

                    tvbu.setText(butto);
                    tvchest.setText(chest);
                    tvcrotch.setText(crotch);
                    tvshoulder.setText(shoulder);
                    tvwaist.setText(waist);

                    break;
                case SHOW_THRESHOLD:
                    if(ms!=null){
                        opencvf.setImageBitmap(ms.getBitF());
                        opencvs.setImageBitmap(ms.getBitS());
                    }

                    break;
            }

            return false;
        }
    });
    private boolean isLogin;


    @Override
    protected void onStart() {
        super.onStart();
        Log.e("TTTT","onstart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("TTTT","onrestart");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            new XPopup.Builder(this).asConfirm("", "是否保存本次测量结果？",
                    new OnConfirmListener() {
                        @Override
                        public void onConfirm() {

                            if(isLogin){
                                saveLocal();
                                asyncinsertWithXHttp2(telephone,(double)realheight,Double.valueOf(crotch),
                                        Double.valueOf(waist),Double.valueOf(shoulder),Double.valueOf(chest),
                                        Double.valueOf(butto));
                                //saveLocal();
                            }
                            else {
                                saveLocal();
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
        }else{
            return super.onKeyDown(keyCode, event);
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("TTTT","oncreat");
        setContentView(R.layout.activity_seg);
        lldata=findViewById(R.id.ll_data);
        showRecommend=findViewById(R.id.show_recommend);
        originFront=findViewById(R.id.origin_front);
        originSide=findViewById(R.id.origin_side);
        more=findViewById(R.id.ib_navigation_more_seg);
        opencvf=findViewById(R.id.opencv_pic_f);
        opencvs=findViewById(R.id.opencv_pic_s);
        choseHeight=findViewById(R.id.show_height);
        tvheight=findViewById(R.id.data_height);
        tvbu=findViewById(R.id.data_bu);
        tvshoulder=findViewById(R.id.data_shoulder);
        tvchest=findViewById(R.id.data_chest);
        tvwaist=findViewById(R.id.data_waist);
        tvcrotch=findViewById(R.id.data_leg);
        mIbNavigationBack = findViewById(R.id.ib_navigation_back_seg);
        more.setOnClickListener(this);
        showRecommend.setOnClickListener(this);
        mIbNavigationBack.setOnClickListener(this);
        choseHeight.setOnClickListener(this);


        String frontPath=this.getIntent().getStringExtra("front");
        String sidePath=this.getIntent().getStringExtra("side");

        footFt= this.getIntent().getDoubleExtra("footf",0);
        footSt= this.getIntent().getDoubleExtra("foots",0);

        isLogin=MyApplication.getInstance().isLogin();
        SharedPreferences sp = getSharedPreferences(ModelConstant.LOGIN_INFO, MODE_PRIVATE);
        telephone = sp.getString("telephone", "");
        SharedPreferences sp1 = getSharedPreferences(ModelConstant.SIZE_INFO, MODE_PRIVATE);
        realheight=(int)sp1.getFloat("height",0f);
        tvheight.setText(realheight+"");
        frontbmp=BitmapFactory.decodeFile(frontPath);
        sidebmp=BitmapFactory.decodeFile(sidePath);
        deleteSingleFile(frontPath);
        deleteSingleFile(sidePath);
        originFront.setImageBitmap(frontbmp);
        originSide.setImageBitmap(sidebmp);
        initCustomOptionPicker();

    }



    /**
     * 进行人像分割
     */
    private void createImageTransactor(String F_S) {
        MLFrame mlFrame;
        if(F_S=="F"){
            MLImageSegmentationSetting setting = new MLImageSegmentationSetting.Factory().setAnalyzerType(MLImageSegmentationSetting.BODY_SEG).create();
            this.analyzer = MLAnalyzerFactory.getInstance().getImageSegmentationAnalyzer(setting);
            mlFrame = new MLFrame.Creator().setBitmap(this.frontbmp).create();
        }else {
            MLImageSegmentationSetting setting = new MLImageSegmentationSetting.Factory().setAnalyzerType(MLImageSegmentationSetting.BODY_SEG).create();
            this.analyzer = MLAnalyzerFactory.getInstance().getImageSegmentationAnalyzer(setting);
            mlFrame = new MLFrame.Creator().setBitmap(this.sidebmp).create();
        }
            Task<MLImageSegmentation> task = this.analyzer.asyncAnalyseFrame(mlFrame);
            task.addOnSuccessListener(new OnSuccessListener<MLImageSegmentation>() {
                @Override
                public void onSuccess(MLImageSegmentation mlImageSegmentationResults) {
                    // Transacting logic for segment success.
                    if (mlImageSegmentationResults != null) {
                        if(F_S=="F"){
                            foregroundF = mlImageSegmentationResults.getGrayscale();

                            createImageTransactor("S");
                        }else {
                            foregroundS=mlImageSegmentationResults.getGrayscale();
                            if(realheight==0){
                                pvCustomOptions.show();
                            }else{

                                mMyHandler.sendEmptyMessage(START_TASK);
                            }

                        }

//                        ImageUtils imageUtils = new ImageUtils(SegActivity.this.getApplicationContext());
//                        imageUtils.setImageUtilCallBack(new ImageUtilCallBack() {
//                            @Override
//                            public void callSavePath(String path) {
//                                Log.i("Humansekeleton", "PATH:" + path);
//                            }
//                        });
//                        imageUtils.saveToAlbum(SegActivity.this.foreground);
                    } else {
                        ToastUtil.showToast(getApplicationContext(),"分割失败！");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    // Transacting logic for segment failure.
                    ToastUtil.showToast(getApplicationContext(),"分割失败！");
                    return;
                }
            });

    }
    //opencv初始化完成后的回调
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mMyHandler.sendEmptyMessage(LOADING);
                            createImageTransactor("F");
                        }
                    }).start();

                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
    @Override
    public void onResume()
    {
        super.onResume();
        Log.e("TTTT","onresume"+"havefinished"+haveFinished);
        if(!haveFinished){
            if (!OpenCVLoader.initDebug()) {
                Log.i("cv", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
            } else {
                Log.i("cv", "OpenCV library found inside package. Using it!");
                mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            }
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_navigation_back_seg:
                //返回
                new XPopup.Builder(this).asConfirm("", "是否保存本次测量结果？",
                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                                if(isLogin){
                                    asyncinsertWithXHttp2(telephone,(double)realheight,Double.valueOf(crotch),
                                            Double.valueOf(waist),Double.valueOf(shoulder),Double.valueOf(chest),
                                            Double.valueOf(butto));
                                    //saveLocal();
                                }
                                else {
                                    saveLocal();
                                }

                            }
                        }, new OnCancelListener() {
                            @Override
                            public void onCancel() {
                                finish();
                            }
                        })
                        .show();



                break;
            case R.id.show_height:
                pvCustomOptions.show();
                break;
            case R.id.show_recommend:
                if(isLogin){
                    saveonce();
                    asyncinsertWithXHttp2(telephone,(double)realheight,Double.valueOf(crotch),
                            Double.valueOf(waist),Double.valueOf(shoulder),Double.valueOf(chest),
                            Double.valueOf(butto));
                    //saveLocal();
                }
                else {
                    saveLocal();
                }
                startActivity(new Intent(SegActivity.this,RecommendActivity.class));
                break;
            case R.id.ib_navigation_more_seg:
                new XPopup.Builder(this)
                        .atView(view)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                        .asAttachList(new String[]{ "保存数据截图","保存到Excel"},
                                new int[]{},
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, String text) {
                                        if(position==0){
                                            Bitmap bitmap=getViewBitmapNoBg(lldata);
                                            saveImg(bitmap);
                                            ToastUtil.showToast(getApplication(),"保存成功！");
                                        }else{
                                            exportExcel(SegActivity.this);
                                        }

                                    }
                                })
                        .show();
                break;
        }
    }

    private void exportExcel(Context context) {

        String filePath = "/sdcard/AndroidExcelDemo";
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }


        String excelFileName = "/demo.xls";


        String[] title = {"身高", "腰围", "臀围","胸围","大腿根围","肩宽","腰到臀部的距离","前胸宽","臂围"};
        String sheetName = "demoSheetName";


        List<DemoBean> demoBeanList = new ArrayList<>();
        DemoBean demoBean1 = new DemoBean((double)realheight,
                Double.valueOf(shoulder),
                Double.valueOf(chest),
                Double.valueOf(waist),
                Double.valueOf(butto),
                Double.valueOf(crotch),
                Double.valueOf(watohip),
                Double.valueOf(frontchest),
                Double.valueOf(armw));

        demoBeanList.add(demoBean1);

        filePath = filePath + excelFileName;


        ExcelUtil.initExcel(filePath, sheetName, title);


        ExcelUtil.writeObjListToExcel(demoBeanList, filePath, context);

        ToastUtil.showToast(this,"excel已导出至：" + filePath);

    }

    private void saveonce() {
        MyApplication.getInstance().userVO.setHeight((double)realheight);
        MyApplication.getInstance().userVO.setButto(Double.valueOf(crotch));
        MyApplication.getInstance().userVO.setChest(Double.valueOf(waist));
        MyApplication.getInstance().userVO.setCrotch(Double.valueOf(shoulder));
        MyApplication.getInstance().userVO.setShoulder(Double.valueOf(chest));
        MyApplication.getInstance().userVO.setWaist(Double.valueOf(butto));
    }

    private void saveLocal() {
        sp = getSharedPreferences(ModelConstant.SIZE_INFO, MODE_PRIVATE);
        editor = sp.edit();
        editor.clear();
        editor.putFloat("height", realheight);
        editor.putFloat("chest", Float.valueOf(chest));
        editor.putFloat("crotch", Float.valueOf(crotch));
        editor.putFloat("shoulder", Float.valueOf(shoulder));
        editor.putFloat("waist", Float.valueOf(waist));
        editor.putFloat("butto", Float.valueOf(butto));

        if (editor.commit()) {
            Log.d("111111", "success： " );

            finish();
        } else {
            ToastUtil.showToast(getApplicationContext(),"保存失败！");
        }
    }

    //由于处理图像耗时较长，需要使用异步任务
    class  MyAsyncTask extends AsyncTask<Void,Void,Bitmap> {



    //第一阶段————准备阶段让进度条显示
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        //第二阶段——opencv处理图像
        @Override
        protected Bitmap doInBackground(Void... voids) {
            bitF = foregroundF.copy(Bitmap.Config.ARGB_8888, false);
            bitS = foregroundS.copy(Bitmap.Config.ARGB_8888, false);

            width=bitF.getWidth();
            height=bitF.getHeight();
            src = new Mat(height, width, CvType.CV_8UC(3));
            srcs= new Mat(height, width, CvType.CV_8UC(3));
            Utils.bitmapToMat(bitF, src);
            Utils.bitmapToMat(bitS, srcs);
            //测量人体尺寸
            ms=new MeasureTranstor(realheight,src,srcs,footSt,footFt,mMyHandler);
            Bitmap bitmap = ms.MeasureBody();

            return bitmap;
        }

        //第三阶段，拿到结果bitmap图片，更新ui
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            loadingPopup.smartDismiss();
//            pvCustomOptions.show();

            mMyHandler.sendEmptyMessage(TOAST_MSG);
            haveFinished=true;
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
                        Log.d(TAG, "success： " );
                        finish();
                        ToastUtil.showToast(getApplicationContext(),"保存成功！");
                    }

                    @Override
                    public void onError(ApiException e) {
                        Log.d(TAG, "请求URL异常： " + e.toString());
                        ToastUtil.showToast(getApplicationContext(),"保存失败！");
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
        pvCustomOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = heights.get(options1).getPickerViewText().split("cm")[0];
                realheight= Integer.valueOf(tx);
                tvheight.setText(realheight+"");

                mMyHandler.sendEmptyMessage(START_TASK);
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
    private boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e("--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 获取控件截图（黑色背景）
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

    private void saveImg(Bitmap bitmap) {
        ImageUtils imageUtils = new ImageUtils(SegActivity.this.getApplicationContext());

        imageUtils.saveToAlbum(bitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
