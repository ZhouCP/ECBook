package com.kelvin.ecbook.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Kelvin on 2016/5/1.
 */
public class Banner extends BmobObject {

    private String title;
    private String picture;
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
