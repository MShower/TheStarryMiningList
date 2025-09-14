package com.mininglist.thestarrymininglist;

import com.google.gson.Gson;
import com.mininglist.thestarrymininglist.dataType.FeedBackJson;
import com.mininglist.thestarrymininglist.dataType.Msg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class Feedback extends Thread {
    private final String modType;
    private boolean mCloseState;
    private String server_id;
    public static final Logger LOGGER = LogManager.getLogger();

    private synchronized boolean getCloseState() {
        return this.mCloseState;
    }

    private void genServerId()
    {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    this.server_id = sb.toString();
                    break;
                }
            }
            if (this.server_id.isEmpty()) {
                this.server_id = "unknown";
            }
        } catch (Exception e) {
            LOGGER.info(Msg.ANSI_RED + "发送遥测数据失败" + Msg.ANSI_RESET);
        }
    }

    private void onSendFeedback()//发送反馈
    {
        try {
            Gson gson = new Gson();
            FeedBackJson json_obj = new FeedBackJson(this.server_id, System.currentTimeMillis() / 1000, modType);
            String req_body = gson.toJson(json_obj);//构建json 字符串

            String FEED_BACK_URL = "https://api.starrylandmc.xyz/send_feedback";
            URL url = URL.of(URI.create(FEED_BACK_URL), null);
            BufferedReader in = getBufferedReader(url, req_body);
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
        } catch (Exception e) {
            LOGGER.info(Msg.ANSI_RED + "发送遥测数据失败" + Msg.ANSI_RESET);
        }
    }

    private static @NotNull BufferedReader getBufferedReader(URL url, String req_body) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);


        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = req_body.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
        return in;
    }

    @Override
    public void run() {//线程的主方法
        genServerId();//获取服务器的 mac 地址
        while (!getCloseState()) {
            try {
                onSendFeedback();
                sleep(43200000);//每隔12个小时发送一次反馈数据
            } catch (InterruptedException e) {
                break;//打断的时候退出
            }
        }
    }

    //关闭
    public synchronized void close() {
        try {
            this.mCloseState = true;//设置关闭的信号成立
            this.interrupt();
        } catch (Exception e)//不用处理打断的异常
        {

        }
    }

    public Feedback(String mod_type) {
        this.modType = mod_type;
    }
}
