package com.example.ttsinterface.service;

import cn.hutool.setting.Setting;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sodream
 * @date 2022/6/2 10:59
 * @content
 */
public class PlayAudioService {
    /**
     * 创建队列
     */
    public static void createQueues(String queueId, String audioPath) {

        Setting setting = new Setting("application.properties");
        String callUrl = "http://" + setting.get("tts.callUrl");

        String url = callUrl + ":8080/api/ola/queues/" + queueId;
        String param = "strategy=fifo_onhook&moh=" + audioPath + "&other_attrs{'chim_freq':'180','agent_timeout':'180', hidden_caller_number:'false'}";
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("Content-Typ", "application/x-www-form-urlencoded");

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * 删除队列
     */
    public static void deleteQueues(String queueId) {
        try {
            Setting setting = new Setting("application.properties");
            String callUrl = "http://" + setting.get("tts.callUrl");
            CloseableHttpClient client = HttpClients.createDefault();
            String url = callUrl + ":8080/api/ola/queues/" + queueId;
            HttpDelete delete = new HttpDelete(url);
            StringEntity reqEntity = new StringEntity("");
            reqEntity.setContentType("application/x-www-form-urlencoded");
            HttpResponse response = client.execute(delete);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                String message = EntityUtils.toString(entity, "utf-8");
                System.out.println(message);
            } else {
                System.out.println("删除队列失败");
            }

        } catch (Exception e) {
            System.out.println("删除队列失败");
        }
    }


    /**
     * 发送语音
     */
    public static void putPlayAudio(String numbers, String batch_accept, String queueId) {
        try {
            Setting setting = new Setting("application.properties");
            String callUrl = "http://" + setting.get("tts.callUrl");
            String url = callUrl + ":8080/api/ola/agents/1001/group_call/" + queueId;
            Map<String, String> map = new HashMap<String, String>();
            map.put("Content-type", "application/x-www-form-urlencoded");
            HttpResponse response = HttpRequestHelper.putRequest(url, "numbers=" + numbers + "&batch_accept=" + batch_accept, map);
            System.out.println(response);
        } catch (Exception e) {
            String str = e.getMessage();
        }

    }


    public static boolean getQueuesStatus(String queueId, String id) throws Exception {
        Map<String, String> map = new HashMap<>();
        Setting setting = new Setting("application.properties");
        String callUrl = "http://" + setting.get("tts.callUrl");
        String url = callUrl + ":8080/api/ola/queues/" + queueId;
        String rep = HttpRequestHelper.getRequest(url);
        if (rep.contains(id)) {
            return true;
        } else {
            return false;
        }
    }
}
