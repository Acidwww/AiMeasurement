package com.example.aibodysizemeasurement.bean;

import com.example.aibodysizemeasurement.R;

import java.util.ArrayList;
import java.util.List;

public class DataBean {
    public Integer imageRes;
    public String title;
    public int viewType;

    public DataBean(Integer imageRes, String title, int viewType) {
        this.imageRes = imageRes;
        this.title = title;
        this.viewType = viewType;
    }



    public static List<DataBean> getTestData() {
        List<DataBean> list = new ArrayList<>();
        list.add(new DataBean(R.drawable.img1, "相信自己,你努力的样子真的很美", 1));
        list.add(new DataBean(R.drawable.img2, "极致简约,梦幻小屋", 3));
        list.add(new DataBean(R.drawable.img3, "超级卖梦人", 3));
        return list;
    }





}