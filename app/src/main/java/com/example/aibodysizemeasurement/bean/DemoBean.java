package com.example.aibodysizemeasurement.bean;


/**
 * @author dmrfcoder
 * @date 2019/2/14
 */

public class DemoBean {
    public String getHeight() {
        return height+"";
    }

    public String getCrotch() {
        return crotch+"";
    }

    public String getWaist() {
        return waist+"";
    }

    public String getShoulder() {
        return shoulder+"";
    }

    public String getChest() {
        return chest+"";
    }

    public String getButto() {
        return butto+"";
    }

    private double height;

    public void setHeight(double height) {
        this.height = height;
    }

    public void setCrotch(double crotch) {
        this.crotch = crotch;
    }

    public void setWaist(double waist) {
        this.waist = waist;
    }

    public void setShoulder(double shoulder) {
        this.shoulder = shoulder;
    }

    public void setChest(double chest) {
        this.chest = chest;
    }

    public void setButto(double butto) {
        this.butto = butto;
    }

    private double crotch;
    private double waist;
    private double shoulder;
    private double chest;
    private double butto;
    private double watohip;
    private double frontchest;

    public String getWatohip() {
        return watohip+"";
    }

    public String getFrontchest() {
        return frontchest+"";
    }

    public String getArmw() {
        return armw+"";
    }

    private double armw;

    public DemoBean(double height, double crotch, double waist, double shoulder, double chest, double butto,double watohip,double frontchest,double armw){

        this.height=height;
        this.crotch=crotch;
        this.waist=waist;
        this.shoulder=shoulder;
        this.chest=chest;
        this.butto=butto;
        this.armw=armw;
        this.frontchest=frontchest;
        this.watohip=watohip;

    }


}
