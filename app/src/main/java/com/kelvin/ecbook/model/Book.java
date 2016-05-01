package com.kelvin.ecbook.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Kelvin on 2016/5/1.
 */
public class Book extends BmobObject {

    private String delcarerId;

    private String title;
    private String category;
    private int cost;

    private int scanTime;
    private int payTime;

    private String photo01;
    private String photo02;

    public String getDelcarerId() {
        return delcarerId;
    }

    public void setDelcarerId(String delcarerId) {
        this.delcarerId = delcarerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getPayTime() {
        return payTime;
    }

    public void setPayTime(int payTime) {
        this.payTime = payTime;
    }

    public int getScanTime() {
        return scanTime;
    }

    public void setScanTime(int scanTime) {
        this.scanTime = scanTime;
    }

    public String getPhoto02() {
        return photo02;
    }

    public void setPhoto02(String photo02) {
        this.photo02 = photo02;
    }

    public String getPhoto01() {
        return photo01;
    }

    public void setPhoto01(String photo01) {
        this.photo01 = photo01;
    }
}
