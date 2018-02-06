package com.ghy.yueplayer.constant;


/**
 * UpdateTypeModel
 */
public class UpdateTypeModel {

    public UpdateType updateType;//更新类型
    public String data;//数据
    public int dataInt;

    public UpdateTypeModel(UpdateType updateType, String data) {
        this.updateType = updateType;
        this.data = data;
    }

    public UpdateTypeModel(UpdateType updateType, int dataInt) {
        this.updateType = updateType;
        this.dataInt = dataInt;
    }

    public UpdateTypeModel(UpdateType updateType) {
        this.updateType = updateType;
    }

}
