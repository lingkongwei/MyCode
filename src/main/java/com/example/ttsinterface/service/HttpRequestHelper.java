package com.example.ttsinterface.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * @author sodream
 * @date 2022/6/2 11:01
 * @content
 */
public class HttpRequestHelper {
    private static final String ENCODE = "UTF-8";
    /**
     * 超时时间(秒)
     */
    private static final int TIMEOUT_MS = 20000;

    /**
     * http GET 请求
     *
     * @param url
     * @param parameters
     * @return
     * @throws Exception
     */
    public static String getRequest(String url, Map<String, String> parameters) throws Exception {
        String paramUrl = setGetParams(url, parameters);
        return getRequestNomal(paramUrl, TIMEOUT_MS, TIMEOUT_MS);
    }

    public static String getRequest(String url) throws Exception {
        return getRequestNomal(url, TIMEOUT_MS, TIMEOUT_MS);
    }


    /**
     * http PUT 请求
     *
     * @param url
     * @param data
     * @param headers
     * @return
     * @throws Exception
     */
    public static HttpResponse putRequest(String url, String data, Map<String, String> headers) throws Exception {
        return notGetRequest(new HttpPut(url), data, headers, TIMEOUT_MS, TIMEOUT_MS);
    }

    /**
     * http POST 请求
     *
     * @param url
     * @param data
     * @param headers
     * @return
     * @throws Exception
     */
    public static HttpResponse postRequest(String url, String data, Map<String, String> headers) throws Exception {
        return notGetRequest(new HttpPost(url), data, headers, TIMEOUT_MS, TIMEOUT_MS);
    }

    /**
     * http 非Get 请求
     *
     * @param url
     * @param data
     * @param headers
     * @param connectTimeoutMs
     * @param readTimeoutMs
     * @return
     * @throws Exception
     */
    private static HttpResponse notGetRequest(HttpEntityEnclosingRequestBase httpRequest, String data,
                                              Map<String, String> headers, int connectTimeoutMs, int readTimeoutMs) throws Exception {

        HttpClient httpClient = HttpClientBuilder.create().build();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeoutMs)
                .setConnectTimeout(connectTimeoutMs).build();
        httpRequest.setConfig(requestConfig);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpRequest.addHeader(entry.getKey(), entry.getValue());
        }
        httpRequest.setEntity(new StringEntity(data, ENCODE));
        HttpResponse httpResponse = httpClient.execute(httpRequest);
        return httpResponse;
    }

    /**
     * 设置Get请求参数
     *
     * @param url
     * @param parameters
     * @return
     * @throws Exception
     */
    private static String setGetParams(String url, Map<String, String> parameters) throws Exception {
        String params = "";// 编码之后的参数
        StringBuffer sb = new StringBuffer();// 处理请求参数
        if (parameters.size() == 1) {
            for (String name : parameters.keySet()) {
                sb.append(name).append("=").append(java.net.URLEncoder.encode(parameters.get(name), ENCODE));
            }
            params = sb.toString();
        } else {
            for (String name : parameters.keySet()) {
                sb.append(name).append("=").append(java.net.URLEncoder.encode(parameters.get(name), ENCODE))
                        .append("&");
            }
            String temp_params = sb.toString();
            params = temp_params.substring(0, temp_params.length() - 1);
        }

        String full_url = url + "?" + params;
        System.out.println(full_url);

        return full_url;
    }

    /**
     * @param url              请求地址
     * @param connectTimeoutMs 超时时间
     * @param readTimeoutMs    超时时间
     * @return
     * @throws Exception
     */
    private static String getRequestNomal(String url, int connectTimeoutMs, int readTimeoutMs) throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpget = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeoutMs)
                .setConnectTimeout(connectTimeoutMs).build();
        httpget.setConfig(requestConfig);
        httpget.addHeader("Content-Type", "text/xml");
        HttpResponse httpResponse = httpClient.execute(httpget);
        HttpEntity httpEntity = httpResponse.getEntity();
        return EntityUtils.toString(httpEntity, "UTF-8");
    }

    /**
     * @param url      请求地下
     * @param certPath 证书路径
     * @param certPwd  证书密码
     * @return
     * @throws Exception
     */
    private String getRequestSSL(String url, String certPath, String certPwd) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(new File(certPath)), certPwd.toCharArray());
        SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return true;
            }
        }).loadKeyMaterial(keyStore, certPwd.toCharArray()).build();
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslcontext,
                new String[]{"TLSv1.2"}, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
        HttpGet httpget = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpget);
        return EntityUtils.toString(response.getEntity(), ENCODE);
    }
}
