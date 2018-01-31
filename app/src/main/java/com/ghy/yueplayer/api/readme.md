```
    @FormUrlEncoded
    @POST("url")
    Observable<BaseEntity<User>> getUser(@FieldMap Map<String, String> map);
    
    @GET("url")
    Observable<BaseEntity<User>> getUser(@QueryMap Map<String, String> map);
```

```
   可针对某个接口设置header
   @Headers({"Accept: application/vnd.github.v3.full+json",
   "User-Agent: Retrofit-your-App"})
   @GET("users/{username}")
   Call<User> getUser(@Path("username") String username);
```

```
   此种写法，url可以传半路径或全路径均可
   @GET public Observable<ResponseBody> getResponseBody(@Url String url);
```

## 使用

#### 普通请求，返回JSONObject

```
requestApi(URL.BASE_URL, "请求中...", new ApiRequestCallBack() {
            @Override
            public void requestCallback(JSONObject jData, String msg, int code, boolean success) {
                if (success) {

                } else {
                    
                }
            }
        });
```

#### 使用实体接收

> getAPiService() == RetrofitManager.getInstance().API();

```
getAPiService().getOnLineListInfo(url, requestParams)
                .compose(RetrofitManager.schedulersTransformer())
                .compose(this.bindToLifecycle())//绑定生命周期
                .subscribe(new EntityObserver<BaseEntity<OnLineListInfo>>(getActivity(), "") {
                    @Override
                    public void requestCallback(BaseEntity<OnLineListInfo> onLineListInfoBaseEntity, String msg, int code, boolean success) {
                        if (success) {

                        } else {

                        }
                    }
                });
```