# 网络请求相关

### JSONObject请求示例：

```javascript
Request request1 = new JsonObjectRequest(URL.TEST);
request1.add("username","110");
request1.add("password","110");
requestAPI(request1, objectListener);

/**
* 接收JSONObject请求响应
*/
private HttpListener<JSONObject> objectListener = new HttpListener<JSONObject>() {
    @Override
    public void onSucceed(JSONObject response) {
            showSnackBar("请求成功");
        }

    @Override
    public void onFailed(int errorCode, String msg) {
            showSnackBar(msg);
        }

    };
```

### JavaBean请求示例：

```javascript
Request<LoginBean> request = new JavaBeanRequest<>(URL.TEST, LoginBean.class);
request.add("username","110");
request.add("password","110");
requestAPI(request, loginHttpListener);

/**
* 接收JavaBean请求响应
*/
private HttpListener<LoginBean> loginHttpListener = new HttpListener<LoginBean>() {
    @Override
    public void onSucceed(LoginBean bean) {
            showSnackBar("请求成功");
       }

    @Override
    public void onFailed(int errorCode, String msg) {
            showSnackBar(msg);
       }
    };
```