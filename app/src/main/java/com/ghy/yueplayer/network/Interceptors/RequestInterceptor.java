package com.ghy.yueplayer.network.Interceptors;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ghy.yueplayer.constant.HttpConfig;
import com.ghy.yueplayer.network.utils.ZipHelper;
import com.ghy.yueplayer.util.TimeUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import timber.log.Timber;

/**
 * ================================================
 * 解析框架中的网络请求和响应结果,并以日志形式输出
 * ================================================
 */
public class RequestInterceptor implements Interceptor {

    private final String API_LOG_TAG = HttpConfig.API_LOG_TAG;

    private final Level printLevel;

    public enum Level {
        NONE,       //不打印log
        REQUEST,    //只打印请求信息
        RESPONSE,   //只打印响应信息
        ALL         //所有数据全部打印
    }

    public RequestInterceptor(@Nullable Level level) {
        if (level == null)
            printLevel = Level.ALL;
        else
            printLevel = level;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        boolean isPrintNone = printLevel == Level.NONE;
        if (isPrintNone) return chain.proceed(request);

        boolean logRequest = printLevel == Level.ALL || printLevel == Level.REQUEST;//是否输出请求log

        if (logRequest) {
            boolean hasRequestBody = request.body() != null;
            //输出请求URl
            Timber.tag(API_LOG_TAG).i("---------------------****---------------------" + "\n" +
                    "时间-->>" + TimeUtils.getTimeStrHMSSSS(System.currentTimeMillis())
                    + "\n" + "请求Url-->>  " + request.url().toString() + "\n");
            //输出请求信息
            Timber.tag(API_LOG_TAG).i("Params : 「 %s 」%nConnection : 「 %s 」%nHeaders : %n「 %s 」"
                    , hasRequestBody ? parseParams(request.newBuilder().build().body()) : "Null"
                    , chain.connection()
                    , request.headers());
        }

        boolean logResponse = printLevel == Level.ALL || printLevel == Level.RESPONSE;//是否打印响应log
        long t1 = logResponse ? System.nanoTime() : 0;

        Response originalResponse = chain.proceed(request);
        long t2 = logResponse ? System.nanoTime() : 0;

        if (logResponse && originalResponse != null) {
            ResponseBody responseBody = originalResponse.body();
            if (responseBody != null) {
                String bodySize = responseBody.contentLength() != -1 ? responseBody.contentLength() + "-byte" : "unknown-length";
                //输出响应时间以及响应头
                Timber.tag(API_LOG_TAG).i("Received response in [ %d-ms ] , [ %s ]%n%s"
                        , TimeUnit.NANOSECONDS.toMillis(t2 - t1), bodySize, originalResponse.headers());
            }
        }

        if (originalResponse != null) {
            //输出响应信息
            printResult(originalResponse.newBuilder().build(), logResponse);
        }

        return originalResponse;
    }

    /**
     * 打印响应结果
     *
     * @param response
     * @param logResponse 是否输出响应信息
     * @return
     * @throws IOException
     */
    @Nullable
    private String printResult(Response response, boolean logResponse) throws IOException {
        //读取服务器返回的结果
        ResponseBody responseBody = response.body();
        String bodyString = null;
        if (isParseable(responseBody.contentType())) {
            try {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();
                //获取content的压缩类型
                String encoding = response.headers().get("Content-Encoding");
                Buffer clone = buffer.clone();
                //解析response content
                bodyString = parseContent(responseBody, encoding, clone);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (logResponse) {
                //打印响应结果
                Timber.tag(API_LOG_TAG).i("---------------------****---------------------" + "\n"
                        + "请求结束时间-->>" + TimeUtils.getTimeStrHMSSSS(System.currentTimeMillis()) + "\n"
                        + "请求结果---->>  " + bodyString
                        + "\n" + "---------------------****---------------------" + "\n");
            }
        } else {
            if (logResponse) {
                Timber.tag(API_LOG_TAG).i("This result isn't parsed");
            }
        }
        return bodyString;
    }

    /**
     * 解析服务器响应的内容
     *
     * @param responseBody
     * @param encoding
     * @param clone
     * @return
     */
    private String parseContent(ResponseBody responseBody, String encoding, Buffer clone) {
        Charset charset = Charset.forName("UTF-8");
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(charset);
        }
        if (encoding != null && encoding.equalsIgnoreCase("gzip")) {//content使用gzip压缩
            return ZipHelper.decompressForGzip(clone.readByteArray(), convertCharset(charset));//解压
        } else if (encoding != null && encoding.equalsIgnoreCase("zlib")) {//content使用zlib压缩
            return ZipHelper.decompressToStringForZlib(clone.readByteArray(), convertCharset(charset));//解压
        } else {//content没有被压缩
            return clone.readString(charset);
        }
    }

    /**
     * 解析请求服务器的请求参数
     *
     * @param body
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String parseParams(RequestBody body) throws UnsupportedEncodingException {
        if (isParseable(body.contentType())) {
            try {
                Buffer requestbuffer = new Buffer();
                body.writeTo(requestbuffer);
                Charset charset = Charset.forName("UTF-8");
                MediaType contentType = body.contentType();
                if (contentType != null) {
                    charset = contentType.charset(charset);
                }
                return URLDecoder.decode(requestbuffer.readString(charset), convertCharset(charset));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "This params isn't parsed";
    }

    /**
     * 是否可以解析
     *
     * @param mediaType
     * @return
     */
    public static boolean isParseable(MediaType mediaType) {
        if (mediaType == null) return false;
        return mediaType.toString().toLowerCase().contains("text")
                || isJson(mediaType) || isForm(mediaType)
                || isHtml(mediaType) || isXml(mediaType);
    }

    public static boolean isJson(MediaType mediaType) {
        return mediaType.toString().toLowerCase().contains("json");
    }

    public static boolean isXml(MediaType mediaType) {
        return mediaType.toString().toLowerCase().contains("xml");
    }

    public static boolean isHtml(MediaType mediaType) {
        return mediaType.toString().toLowerCase().contains("html");
    }

    public static boolean isForm(MediaType mediaType) {
        return mediaType.toString().toLowerCase().contains("x-www-form-urlencoded");
    }

    public static String convertCharset(Charset charset) {
        String s = charset.toString();
        int i = s.indexOf("[");
        if (i == -1)
            return s;
        return s.substring(i + 1, s.length() - 1);
    }
}
