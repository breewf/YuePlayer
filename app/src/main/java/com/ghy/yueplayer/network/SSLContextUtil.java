package com.ghy.yueplayer.network;


import com.ghy.yueplayer.PlayerApplication;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by GHY on 2016/12/30.
 * Desc: Https
 */

public class SSLContextUtil {

    /**
     * 有安全证书的SSLContext
     * 把Https的证书放在assets目录下，然后通过流加载
     *
     * @return
     */
    public static SSLContext getSSLContext() {
        // 生成SSLContext对象
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            // 从assets中加载证书
            InputStream inStream = PlayerApplication.getInstance().getAssets().open("srca.cer");

            // 证书工厂
            CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");
            Certificate cer = cerFactory.generateCertificate(inStream);

            // 密钥库
            KeyStore kStore = KeyStore.getInstance("PKCS12");
            kStore.load(null, null);
            kStore.setCertificateEntry("trust", cer);// 加载证书到密钥库中

            // 密钥管理器
            KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyFactory.init(kStore, null);// 加载密钥库到管理器

            // 信任管理器
            TrustManagerFactory tFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tFactory.init(kStore);// 加载密钥库到信任管理器

            // 初始化
            sslContext.init(keyFactory.getKeyManagers(), tFactory.getTrustManagers(), new SecureRandom());

        } catch (NoSuchAlgorithmException | IOException | CertificateException | UnrecoverableKeyException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
        }

        return sslContext;

    }

    /**
     * 没有安全证书的SSLContext
     *
     * @return
     */
    public static SSLContext getSSLContextNoCer() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslContext;
    }


    /**
     * 使用没有安全证书的SSLContext时
     * 代码和上边的NoHttp加载证书的一样，多了一句setHostnameVerifier
     * httpsRequest.setHostnameVerifier(SSLContextUtil.hostnameVerifier);
     */
    public static HostnameVerifier hostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

}
