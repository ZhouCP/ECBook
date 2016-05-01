package com.kelvin.ecbook.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Kelvin on 2016/5/1.
 */
public class BookCategory extends BmobObject {

    private String title;
    private int sum;

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
