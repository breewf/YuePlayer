package com.ghy.yueplayer.network;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by GHY on 2018/1/6.
 * Modify by GHY on 2018/1/6.
 * ClassDesc:Http网络请求回调
 * 返回已解析服务器result的json结果，jData为服务器返回的result结果
 **/
public abstract class ApiRequestCallBack extends ApiRequestResultCallBack {

    @Override
    public void callbackResult(JSONObject jData, String msg, int code, boolean success) {
        try {
            String jsonStr = jData.optString("result", "{}");
            if (jsonStr.startsWith("{")) {
                callback(new JSONObject(jsonStr), new JSONArray(), msg, code, success);
            } else if (jsonStr.startsWith("[")) {
                callback(new JSONObject(), new JSONArray(jsonStr), msg, code, success);
            } else {
                callback(new JSONObject(), new JSONArray(), msg, code, success);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public abstract void callback(JSONObject jData, JSONArray jArray, String msg, int code, boolean success);
}