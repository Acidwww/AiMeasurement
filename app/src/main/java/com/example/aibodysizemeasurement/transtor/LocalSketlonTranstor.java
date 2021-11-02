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

package com.example.aibodysizemeasurement.transtor;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.aibodysizemeasurement.others.CameraImageGraphic;
import com.example.aibodysizemeasurement.others.FrameMetadata;
import com.example.aibodysizemeasurement.others.GraphicOverlay;
import com.example.aibodysizemeasurement.others.LocalSkeletonGraphic;
import com.example.aibodysizemeasurement.application.MyApplication;
import com.example.aibodysizemeasurement.callback.CameraCallback;
import com.example.aibodysizemeasurement.utils.ToastUtil;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.skeleton.MLJoint;
import com.huawei.hms.mlsdk.skeleton.MLSkeleton;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzer;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerFactory;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerSetting;


import java.io.IOException;
import java.util.List;


import static com.example.aibodysizemeasurement.activity.HumanSkeletonActivity.CAPTURE_FRONT;
import static com.example.aibodysizemeasurement.activity.HumanSkeletonActivity.CAPTURE_SIDE;
import static com.example.aibodysizemeasurement.activity.HumanSkeletonActivity.FOOT;


/**
 *  SketlonTranstor
 *
 * @since  2020-12-10
 */

public class LocalSketlonTranstor extends BaseTransactor<List<MLSkeleton>> {
    private static final String TAG = "LocalSketlonTransactor";

    private static MLSkeletonAnalyzer analyzer;

    private CameraCallback mCameraCallback;

    private Handler mHandler;

    private boolean haveFront=false;
    private boolean haveSide=false;
    private double foot=0;

    Context context = MyApplication.getInstance();

    public LocalSketlonTranstor(MLSkeletonAnalyzerSetting setting, Context context, Handler handler) {
        super(context);
        Log.i(TAG, "analyzer init");
        this.mHandler = handler;
        if (analyzer != null) {
            stop();
        }
        analyzer = MLSkeletonAnalyzerFactory.getInstance().getSkeletonAnalyzer(setting);
    }

    @Override
    public void stop() {
        try {
            Log.i(TAG,   "analyzer stop.");
            this.analyzer.stop();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close sketlon transactor: " + e.getMessage());
        }
    }

    @Override
    public Task<List<MLSkeleton>> detectInImage(MLFrame image) {
        return this.analyzer.asyncAnalyseFrame(image);
    } 

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<MLSkeleton> MLSkeletons,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        Log.d(TAG, "Total MLSkeletons graphicOverlay start");
        if (originalCameraImage != null) {
            mCameraCallback.callCameraBitmap(originalCameraImage);
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.addGraphic(imageGraphic);
        }

        if (MLSkeletons == null || MLSkeletons.isEmpty()) {
            return;
        }
        Log.d(TAG, "Total MLSkeletons hmsMLLocalFaceGraphic start");
        LocalSkeletonGraphic hmsMLLocalSkeletonGraphic = new LocalSkeletonGraphic(graphicOverlay, MLSkeletons);
        graphicOverlay.addGraphic(hmsMLLocalSkeletonGraphic);
        graphicOverlay.postInvalidate();
        Log.d(TAG, "Total MLSkeletons graphicOverlay end");

        if (mHandler == null) {
            return;
        }

        if( MLSkeletons.size()==1) {
            MLSkeleton skeleton = MLSkeletons.get(0);
            foot=skeleton.getJointPoint(MLJoint.TYPE_LEFT_ANKLE).getPointY()+skeleton.getJointPoint(MLJoint.TYPE_RIGHT_ANKLE).getPointY();
        }

        Message msg=Message.obtain();
        msg.what=FOOT;
        Bundle bundle=new Bundle();
        bundle.putDouble("foot",foot);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        //compareSimilarity(MLSkeletons);
        currectPosition(MLSkeletons,(int)(originalCameraImage.getWidth()*0.5),(int)(originalCameraImage.getHeight()*0.5));
    }

    private void currectPosition(List<MLSkeleton> skeletons, int width, int height) {
        ToastUtil.showToast(context,"In");
        for (int i = 0; i < skeletons.size(); i++) {
            MLSkeleton skeleton = skeletons.get(i);
            if (skeleton.getJoints() == null || skeleton.getJoints().size() < 14) {
                continue;
            }
            if(!haveFront&&!haveSide){
                if(currectHead(skeleton, width, height)&&currectHand(skeleton, width,  height)&&currectFoot(skeleton,  width, height)){
                    haveFront=true;
                    mHandler.sendEmptyMessage(CAPTURE_FRONT);
                }
            }
            else if(haveFront&&!haveSide){
                if(currectHead(skeleton, width, height)&&currectSideHand(skeleton, width,  height)&&currectSideFoot(skeleton,  width, height)){
                    haveSide=true;
                    mHandler.sendEmptyMessage(CAPTURE_SIDE);
                }
            }

        }
    }

    private boolean currectSideFoot(MLSkeleton skeleton, int width, int height) {
        if(skeleton.getJointPoint(MLJoint.TYPE_LEFT_ANKLE).getPointX()>=0.392*width
                &&skeleton.getJointPoint(MLJoint.TYPE_LEFT_ANKLE).getPointX()<=0.641*width
                &&skeleton.getJointPoint(MLJoint.TYPE_LEFT_ANKLE).getPointY()>=0.822*height
                &&skeleton.getJointPoint(MLJoint.TYPE_LEFT_ANKLE).getPointY()<=0.95*height
                &&skeleton.getJointPoint(MLJoint.TYPE_RIGHT_ANKLE).getPointX()>=0.392*width
                &&skeleton.getJointPoint(MLJoint.TYPE_RIGHT_ANKLE).getPointX()<=0.641*width
                &&skeleton.getJointPoint(MLJoint.TYPE_RIGHT_ANKLE).getPointY()>=0.822*height
                &&skeleton.getJointPoint(MLJoint.TYPE_RIGHT_ANKLE).getPointY()<=0.95*height){
            ToastUtil.showToast(context,"FOOT");
            return true;
        }
        else {
            return false;
        }

    }

    private boolean currectSideHand(MLSkeleton skeleton, int width, int height) {
        if(skeleton.getJointPoint(MLJoint.TYPE_LEFT_WRIST).getPointX()>=0.337*width
                &&skeleton.getJointPoint(MLJoint.TYPE_LEFT_WRIST).getPointX()<=0.658*width
                &&skeleton.getJointPoint(MLJoint.TYPE_LEFT_WRIST).getPointY()>=0.4275*height
                &&skeleton.getJointPoint(MLJoint.TYPE_LEFT_WRIST).getPointY()<=0.596*height
                &&skeleton.getJointPoint(MLJoint.TYPE_RIGHT_WRIST).getPointX()>=0.337*width
                &&skeleton.getJointPoint(MLJoint.TYPE_RIGHT_WRIST).getPointX()<=0.658*width
                &&skeleton.getJointPoint(MLJoint.TYPE_RIGHT_WRIST).getPointY()>=0.4275*height
                &&skeleton.getJointPoint(MLJoint.TYPE_RIGHT_WRIST).getPointY()<=0.596*height){
            ToastUtil.showToast(context,"HAND");
            return true;
        }
        else {
            return false;
        }
    }

    private boolean currectFoot(MLSkeleton skeleton, int width, int height) {

        if(skeleton.getJointPoint(MLJoint.TYPE_LEFT_ANKLE).getPointX()>=0.555*width
        &&skeleton.getJointPoint(MLJoint.TYPE_LEFT_ANKLE).getPointX()<=0.726*width
        &&skeleton.getJointPoint(MLJoint.TYPE_LEFT_ANKLE).getPointY()>=0.827*height
        &&skeleton.getJointPoint(MLJoint.TYPE_LEFT_ANKLE).getPointY()<=0.92*height
        &&skeleton.getJointPoint(MLJoint.TYPE_RIGHT_ANKLE).getPointX()>=0.275*width
        &&skeleton.getJointPoint(MLJoint.TYPE_RIGHT_ANKLE).getPointX()<=0.447*width
        &&skeleton.getJointPoint(MLJoint.TYPE_RIGHT_ANKLE).getPointY()>=0.827*height
        &&skeleton.getJointPoint(MLJoint.TYPE_RIGHT_ANKLE).getPointY()<=0.92*height){
            ToastUtil.showToast(context,"FOOT");
            return true;
        }
        else {
            return false;
        }

    }

    private boolean currectHand(MLSkeleton skeleton, int width, int height) {
        if(skeleton.getJointPoint(MLJoint.TYPE_LEFT_WRIST).getPointX()>=0.746*width
                &&skeleton.getJointPoint(MLJoint.TYPE_LEFT_WRIST).getPointX()<=0.954*width
                &&skeleton.getJointPoint(MLJoint.TYPE_LEFT_WRIST).getPointY()>=0.438*height
                &&skeleton.getJointPoint(MLJoint.TYPE_LEFT_WRIST).getPointY()<=0.567*height
                &&skeleton.getJointPoint(MLJoint.TYPE_RIGHT_WRIST).getPointX()>=0.05*width
                &&skeleton.getJointPoint(MLJoint.TYPE_RIGHT_WRIST).getPointX()<=0.258*width
                &&skeleton.getJointPoint(MLJoint.TYPE_RIGHT_WRIST).getPointY()>=0.438*height
                &&skeleton.getJointPoint(MLJoint.TYPE_RIGHT_WRIST).getPointY()<=0.567*height){
            ToastUtil.showToast(context,"HAND");
            return true;
        }
        else {
            return false;
        }
    }

    private boolean currectHead(MLSkeleton skeleton, int width, int height) {

        if(skeleton.getJointPoint(MLJoint.TYPE_HEAD_TOP).getPointX()>=0.369*width
                &&skeleton.getJointPoint(MLJoint.TYPE_HEAD_TOP).getPointX()<=0.631*width
                &&skeleton.getJointPoint(MLJoint.TYPE_HEAD_TOP).getPointY()>=0.039*height
                &&skeleton.getJointPoint(MLJoint.TYPE_HEAD_TOP).getPointY()<=0.191*height){
            ToastUtil.showToast(context,"HEAD");
            return true;
        }
        else {
            return false;
        }
    }



    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Skeleton detection failed: " + e.getMessage());
    }

    @Override
    public boolean isFaceDetection() {
        return true;
    }

    public void setmCameraCallback(CameraCallback mCameraCallback) {
        this.mCameraCallback = mCameraCallback;
    }
}
