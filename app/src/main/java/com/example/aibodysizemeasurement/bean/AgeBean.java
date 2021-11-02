package com.example.aibodysizemeasurement.bean;
import com.contrarywind.interfaces.IPickerViewData;

public class AgeBean implements IPickerViewData {
    int id;
    String No;

    public AgeBean(int id, String cardNo) {
        this.id = id;
        this.No = cardNo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNo() {
        return No;
    }

    public void setNo(String cardNo) {
        this.No = cardNo;
    }

    @Override
    public String getPickerViewText() {
        return No;
    }

}