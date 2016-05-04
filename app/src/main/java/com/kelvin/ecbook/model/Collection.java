package com.kelvin.ecbook.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Kelvin on 2016/5/3.
 */
public class Collection extends BmobObject {

    private String collector;
    private String book;

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        this.collector = collector;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }
}
