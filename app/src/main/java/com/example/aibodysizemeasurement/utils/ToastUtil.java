package com.example.aibodysizemeasurement.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;

/**
 * Created by INvo
 * on 2019-07-10.
 */
public class ToastUtil {
    private static Toast mToast;
    final static String myMsg = "Error!!!";

    public static void showToast(Context ctx, @Nullable String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(ctx, msg, Toast.LENGTH_LONG);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }
}
