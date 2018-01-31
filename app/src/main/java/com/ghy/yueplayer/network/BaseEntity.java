package com.ghy.yueplayer.network;

import java.io.Serializable;

/**
 * Created by GHY on 2018/1/6.
 * Desc:基类实体
 */

public class BaseEntity<E> implements Serializable {

    private int code;//状态码
    private String message;//msg
    private boolean success;//success
    private E data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }
}
