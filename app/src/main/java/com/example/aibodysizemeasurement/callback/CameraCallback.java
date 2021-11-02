package com.example.aibodysizemeasurement.callback;

import android.graphics.Bitmap;

public interface CameraCallback {
    /**
     * @param bitmap 返回摄像头图像
     */
    void callCameraBitmap(Bitmap bitmap);
}
