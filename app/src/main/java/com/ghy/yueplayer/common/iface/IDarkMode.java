package com.ghy.yueplayer.common.iface;

/**
 * @author HY
 * @date 2019-07-23
 * ClassDesc:DarkMode实现接口.
 **/
public interface IDarkMode {

    /**
     * DarkMode模式改变
     *
     * @param isDayMode true:日间模式
     */
    void darkModeChange(boolean isDayMode);
}
