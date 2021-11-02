package com.example.aibodysizemeasurement.constant;

public class NetConstant {
    public static final String baseService = "http://123.60.82.42/aimeasure/";
   // public static final String baseService = "http://427056i6d7.zicp.vip/";

    private static final String getSizeURL        = "user/getsize";
    private static final String updateUserURL     = "user/updateuser";
    private static final String insertSizeURL     = "user/insertsize";
    private static final String getUserURL        = "user/getuser";
    private static final String getOtpCodeURL     = "user/getotp";
    private static final String loginURL          = "user/login";
    private static final String registerURL       = "user/register";
    private static final String createItemURL     = "item/create";
    private static final String getItemListURL    = "item/list";
    private static final String submitOrderURL    = "order/createorder";




    public static String getUpdateUserURL() {
        return updateUserURL;
    }

    public static String getGetUserURL() {
        return getUserURL;
    }

    public static String getGetSizeURL() {
        return getSizeURL;
    }

    public static String getInsertSizeURL() {
        return insertSizeURL;
    }

    public static String getGetOtpCodeURL() {
        return getOtpCodeURL;
    }

    public static String getLoginURL() {
        return loginURL;
    }

    public static String getRegisterURL() {
        return registerURL;
    }

    public static String getCreateItemURL() {
        return createItemURL;
    }

    public static String getGetItemListURL() {
        return getItemListURL;
    }

    public static String getSubmitOrderURL() {
        return submitOrderURL;
    }


}
