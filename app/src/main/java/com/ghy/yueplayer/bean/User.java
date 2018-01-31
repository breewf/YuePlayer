package com.ghy.yueplayer.bean;

import java.io.Serializable;

/**
 * Created by GHY on 2018/1/6.
 * Desc:用户
 */

public class User implements Serializable {

    private String name;
    private String phone;
    private String token;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
