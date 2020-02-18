package com.leyongleshi.idea.plugin.pasteimageintomarkdown;

import okhttp3.OkHttpClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * @author pengqingsong
 * @date 07/09/2017
 * @desc http请求工具类
 */
public class HttpRequestUtils {

    public static final ThreadLocal<OkHttpClient> OK_HTTP_CLIENT_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS).build();
        return okHttpClient;
    });
    public static final ThreadLocal<OkHttpClient> OK_HTTP_CLIENT_WITH_SSL_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .sslSocketFactory(getSSLSocketFactory())
                .writeTimeout(10, TimeUnit.SECONDS).build();
        return okHttpClient;
    });

    public static String get(String url) {
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();
        return executeRequest(request, false);
    }

    public static String executeRequest(okhttp3.Request request, boolean ssl) {
        try {
            OkHttpClient okHttpClient = null;
            if (ssl) {
                okHttpClient = OK_HTTP_CLIENT_WITH_SSL_THREAD_LOCAL.get();
            } else {
                okHttpClient = OK_HTTP_CLIENT_THREAD_LOCAL.get();
            }

            okhttp3.Response response = okHttpClient.newCall(request).execute();
            int code = response.code();
            String bodyStr = response.body().string();
            if (code != 200) {
                throw new RuntimeException(request.method() + "请求失败,url[" + request.url() + "],response[" + bodyStr + "]");
            }
            return bodyStr;
        } catch (Exception e) {
            throw new RuntimeException(request.method() + "请求失败,url[" + request.url() + "]", e);
        }
    }

    private static SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new X509TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, null);
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
