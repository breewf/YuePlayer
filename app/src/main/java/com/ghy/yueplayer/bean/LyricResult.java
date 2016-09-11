package com.ghy.yueplayer.bean;

import java.util.List;

/**
 * Created by GHY on 2015-08-28.
 */
public class LyricResult {

    //注意参数一定要一致
    private String count;
    private String code;
    private List<LyricInfo> result;//注意参数一定是result

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<LyricInfo> getResult() {
        return result;
    }

    public void setResult(List<LyricInfo> result) {
        this.result = result;
    }
}
