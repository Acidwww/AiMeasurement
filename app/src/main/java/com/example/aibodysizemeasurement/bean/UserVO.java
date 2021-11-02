package com.example.aibodysizemeasurement.bean;
/* 用于呈现给前端的模型 */
public class UserVO {
    private Integer id;
    private String name;
    private Byte gender;
    private Integer age;
    private String telephone;
    private double height;
    private double crotch;
    private double waist;
    private double shoulder;
    private double chest;
    private double butto;
    private Integer style;

    public UserVO(String name, Byte gender, Integer age, String telephone, double height,
             double crotch, double waist, double shoulder, double chest, double butto, Integer style){
        this.name=name;
        this.gender=gender;
        this.age=age;
        this.telephone=telephone;
        this.height=height;
        this.crotch=crotch;
        this.waist=waist;
        this.shoulder=shoulder;
        this.chest=chest;
        this.butto=butto;
        this.style=style;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getCrotch() {
        return crotch;
    }

    public void setCrotch(double crotch) {
        this.crotch = crotch;
    }

    public double getWaist() {
        return waist;
    }

    public void setWaist(double waist) {
        this.waist = waist;
    }

    public double getShoulder() {
        return shoulder;
    }

    public void setShoulder(double shoulder) {
        this.shoulder = shoulder;
    }

    public double getChest() {
        return chest;
    }

    public void setChest(double chest) {
        this.chest = chest;
    }

    public double getButto() {
        return butto;
    }

    public void setButto(double butto) {
        this.butto = butto;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }


    public Integer getStyle() {
        return style;
    }

    public void setStyle(Integer style) {
        this.style = style;
    }

}
