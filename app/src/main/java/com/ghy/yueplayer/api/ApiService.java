package com.ghy.yueplayer.api;


import com.ghy.yueplayer.bean.OnLineListInfo;
import com.ghy.yueplayer.bean.User;
import com.ghy.yueplayer.network.BaseEntity;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by GHY on 2018/1/6.
 * Desc:api接口管理
 * 使用请看目录下readme.md文件
 */

/**
 * ================================================
 * 注意：
 * Observable(被观察者)/Observer（观察者）
 * Flowable(被观察者)/Subscriber(观察者)
 * RxJava2.X中，Observeable用于订阅Observer，是不支持背压的，
 * 而Flowable用于订阅Subscriber，是支持背压(Backpressure)的。
 * 背压是指在异步场景中，被观察者发送事件速度远快于观察者的处理速度的情况下，一种告诉上游的被观察者降低发送速度的策略。
 * ================================================
 */

public interface ApiService {

    /**
     * 通用POST请求
     *
     * @param url 请求url
     * @param map 参数集合
     * @return
     */
    @FormUrlEncoded//修饰Field注解和FieldMap注解
    @POST()
    Observable<String> requestApiJSON(@Url String url, @FieldMap Map<String, Object> map);

    @POST()
    Observable<String> requestApiJSONNoParams(@Url String url);

    @Multipart
    @POST()
    Observable<String> requestApiImage(@Url String url, @Part List<MultipartBody.Part> partList);

    /**
     * 通用GET请求
     *
     * @param url 请求url
     * @param map 参数集合
     * @return
     */
    @GET()
    Observable<String> requestApiJSONGet(@Url String url, @QueryMap Map<String, Object> map);

    @GET()
    Observable<String> requestApiJSONGetNoParams(@Url String url);


    /***-----------------提交Json---------------------------****/

    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST()
    Observable<String> requestApiPostJson(@Url String url, @Body RequestBody requestBody);//传入的参数为RequestBody


    /***-----------------返回Entity----start-----------------------****/

    @FormUrlEncoded
    @POST()
    Observable<BaseEntity<User>> requestUserAPi(@Url String url, @FieldMap Map<String, Object> map);

    @POST()
    Observable<BaseEntity<User>> requestUserAPiNoParams(@Url String url);

    @GET()
    Observable<BaseEntity<User>> requestUserAPiGet(@Url String url, @QueryMap Map<String, Object> map);

    @GET()
    Observable<BaseEntity<User>> requestUserAPiGetNoParams(@Url String url);

    /***-----------------返回Entity----end-----------------------****/


    /***-----------------自定义请求---------------------------****/

    @GET()
    Observable<OnLineListInfo> getOnLineListInfo(@Url String url, @QueryMap Map<String, Object> map);

}
