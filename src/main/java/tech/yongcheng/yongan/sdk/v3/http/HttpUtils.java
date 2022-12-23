package tech.yongcheng.yongan.sdk.v3.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import tech.yongcheng.yongan.sdk.v3.model.Response;

/**
 * @description:
 * @auther: Warren
 * @date: 2022/10/18 15:05
 */
public class HttpUtils {

    public static Response httpRequest(String methodUrl, String body, String auth, int timeout)
            throws Exception {
        // String methodUrl = "http://xxx.xxx.xx.xx:8280/xx/adviserxx ";
        HttpURLConnection connection = null;
        OutputStream dataout = null;
        BufferedReader reader = null;
        String line = null;
        try {
            URL url = new URL(methodUrl);
            connection = (HttpURLConnection) url.openConnection(); // 根据URL生成HttpURLConnection
            connection.setDoOutput(
                    true); // 设置是否向connection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true,默认情况下是false
            connection.setDoInput(true); // 设置是否从connection读入，默认情况下是true;
            connection.setRequestMethod("POST"); // 设置请求方式为post,默认GET请求
            connection.setUseCaches(false); // post请求不能使用缓存设为false
            connection.setConnectTimeout(timeout); // 连接主机的超时时间
            connection.setReadTimeout(timeout); // 从主机读取数据的超时时间
            connection.setInstanceFollowRedirects(true); // 设置该HttpURLConnection实例是否自动执行重定向
            connection.setRequestProperty("connection", "Keep-Alive"); // 连接复用
            connection.setRequestProperty("charset", "utf-8");

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", String.format("%s %s", "Bearer", auth));
            connection.connect(); // 建立TCP连接,getOutputStream会隐含的进行connect,所以此处可以不要

            dataout = new DataOutputStream(connection.getOutputStream()); // 创建输入输出流,用于往连接里面输出携带的参数
            // String body = "[{\"orderNo\":\"44921902\",\"adviser\":\"测试\"}]";
            dataout.write(body.getBytes());
            dataout.flush();
            dataout.close();
            // 该部分也可传递键值对参数（同上述）
            // String body = "userName=zhangsan&password=123456";
            // BufferedWriter writer = new BufferedWriter(new
            // //OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            // writer.write(body);
            // writer.close();
            Response response = new Response();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream(), "UTF-8")); // 发送http请求
                StringBuilder result = new StringBuilder();
                // 循环读取流
                while ((line = reader.readLine()) != null) {
                    result.append(line).append(System.getProperty("line.separator")); //
                }

                ObjectMapper mapper = new ObjectMapper();

                // 反序列化 JSON 到树
                JsonNode jsonNode = mapper.readTree(result.toString());
                // 从树中读取 data 节点
                int code = jsonNode.get("error_code").asInt();
                String message = jsonNode.get("error_message").asText();
                response.setErrorCode(code);
                response.setErrorMessage(message);
                String resultStr = jsonNode.get("content").toString();
                response.setContent(resultStr.getBytes(StandardCharsets.UTF_8));
                return response;
            } else {
                response.setErrorCode(connection.getResponseCode());
                response.setErrorMessage(connection.getResponseMessage());
                return response;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}
