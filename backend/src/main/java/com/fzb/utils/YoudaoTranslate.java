package com.fzb.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class YoudaoTranslate {

    private static final String APP_ID = "28ab3daa6c03dcb1";
    private static final String APP_SECRET = "oBN0KUaUo0fBvCWHp1rrRpQsdgk1bI4A";
    private static final String API_URL = "http://openapi.youdao.com/api";

    public static void main(String[] args) {
        String query = "我也想拥有这样的青春";
        String translatedText = translate(query);
        int start = translatedText.indexOf("\"translation\":[\"") + "\"translation\":[\"".length();
        int end = translatedText.indexOf("\"]", start);
        String translatedText1 = translatedText.substring(start, end);
        System.out.println("翻译结果: " + translatedText1);
    }

    /**
     * 实际调用方法
     */
    public String GetTranslation(String text){
        String translatedText = translate(text);
        int start = translatedText.indexOf("\"translation\":[\"") + "\"translation\":[\"".length();
        int end = translatedText.indexOf("\"]", start);
        String resultText = translatedText.substring(start, end);
        return resultText;
    }

    public static String translate(String query) {
        String salt = String.valueOf(new Date().getTime());
        String sign = generateSign(query, salt);

        Map<String, String> params = new HashMap<>();
        params.put("q", query);
        params.put("from", "zh-CHS");
        params.put("to", "EN");
        params.put("appKey", APP_ID);
        params.put("salt", salt);
        params.put("sign", sign);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(API_URL);
            StringEntity entity = new StringEntity(mapToUrl(params), StandardCharsets.UTF_8);
            post.setEntity(entity);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String generateSign(String query, String salt) {
        String combined = APP_ID + query + salt + APP_SECRET;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(combined.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String mapToUrl(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() != 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }
}