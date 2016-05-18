package com.kelvin.ecbook.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Kelvin on 2016/4/21.
 */
public class EcBookUser extends BmobObject {

    private String username;
    private String email;
    private String password;

    private String avatar;

    private int credit;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }
}
