package com.example.aibodysizemeasurement.bean;

import com.contrarywind.interfaces.IPickerViewData;

public class HeightBean implements IPickerViewData {
    int id;
    String No;

    public HeightBean(int id, String cardNo) {
        this.id = id;
        this.No = cardNo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeightNo() {
        return No;
    }

    public void setHeightNo(String cardNo) {
        this.No = cardNo;
    }

    @Override
    public String getPickerViewText() {
        return No;
    }

}
