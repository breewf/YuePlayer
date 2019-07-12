package com.ghy.yueplayer.constant;


/**
 * @author HY
 * UpdateTypeModel
 */
public class UpdateTypeModel {

    /**
     * 更新类型
     */
    public UpdateType updateType;

    public String data;

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
