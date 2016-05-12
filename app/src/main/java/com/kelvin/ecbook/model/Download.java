package com.kelvin.ecbook.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Kelvin on 2016/5/12.
 */
public class Download extends BmobObject {

    private String downloader;
    private String book;

    public String getDownloader() {
        return downloader;
    }

    public void setDownloader(String downloader) {
        this.downloader = downloader;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }
}
