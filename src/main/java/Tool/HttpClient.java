package Tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import okhttp3.internal.http2.Header;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zhaolong
 * @Time: 17:44
 * @Date: 2021年07月11日 17:44
 **/
public class HttpClient {
    private static final String GROUP = "http://gchat.qpic.cn/";
    private static final String PERSON = "http://c2cpicdw.qpic.cn/";
    private static final String PROXY = "https://c2cpicdw.orisland.workers.dev/";
    private static final String PIXY = "https://blue-dawn-a7a7.orisland.workers.dev/";
    private static ObjectMapper mapper = new ObjectMapper();
    private static final String smallToken = "a696d19b8e12c9e5ca70aaafcd33c285";
    private static final MediaType FORM_CONTENT_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");


    /**
     * httpclient，获取url
     * @param url
     * @return
     * @throws IOException
     */
    public static byte[] getUrlByByte(String url) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .callTimeout(60, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Connection", "keep-alive")
                .build();

        return client.newCall(request).execute().body().bytes();
    }

    /**
     * 获取url的内容
     * @param url
     * @return
     * @throws IOException
     */
    public static String getUrlByString(String url) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .callTimeout(60, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Connection", "keep-alive")
                .build();
        String res = client.newCall(request).execute().body().string();
        if (res == null){
            return null;
        }else{
            return client.newCall(request).execute().body().string();
        }
    }

    /**
     * json访问网页返回
     * @param url
     * @return
     * @throws IOException
     */
    public static JsonNode getUrlByJson(String url) throws IOException{
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .callTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Connection", "keep-alive")
                .build();

        return mapper.readTree(client.newCall(request).execute().body().bytes());
    }

    /**
     * api get方法，专用于api调用
     * @param url
     * @param args
     * @return
     * @throws IOException
     */
    public static JsonNode apiGetByJson(String url, String ...args) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .callTimeout(30, TimeUnit.SECONDS)
                .build();

        url += "?";
        StringBuilder arg = new StringBuilder();
        for (String str : args){
            arg.append(str);
            arg.append("&");
        }

        url += arg;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Connection", "keep-alive")
                .build();
        return mapper.readTree(client.newCall(request).execute().body().bytes());
    }

    /**
     * url地址转换方法，这个方法用于转换图片地址为proxy地址
     * @param url
     * @return
     */
    public static String urlProxy(String url){
        if (url.indexOf(GROUP) == 0){
            url = PROXY + url.split(GROUP)[1];
        }else if (url.indexOf(PERSON) == 0){
            url = PROXY + url.split(PERSON)[1];
        }else {
            return "error";
        }

        return url;
    }

    /**
     * 缩链百度版
     * @param url
     * @return
     * @throws IOException
     */
    public static String smallUrl(String url) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .callTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody body = new FormBody.Builder()
                .add("LongUrl", url)
                .add("TermOfValidity", "1-year")
                .build();

        RequestBody body1 = RequestBody.create(FORM_CONTENT_TYPE, body.toString());

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Dwz-Token", smallToken)
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .post(body1)
                .build();

        return mapper.readTree(client.newCall(request).execute().body().bytes()).get("ShortUrl").toString();
    }

    /**
     * p站反代理
     * @param url
     * @return
     */
    public static String pixyProxy(String url){
        return PIXY + url.split("https://i.pximg.net/")[1];
    }

    public static void main(String[] args) throws IOException {
        String str = pixyProxy("https://i.pximg.net/c/600x1200_90_webp/img-master/img/2019/01/11/14/16/18/72603092_p0_master1200.jpg");
        System.out.println(str);
    }
}
