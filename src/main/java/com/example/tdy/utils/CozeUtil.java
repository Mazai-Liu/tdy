package com.example.tdy.utils;

import com.alibaba.fastjson.JSON;
import com.example.tdy.entity.resp.coze.MessageObject;
import com.example.tdy.entity.resp.coze.NonStreamResp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Mazai-Liu
 * @time 2024/6/29
 */
@Component
public class CozeUtil {
    @Value("${coze.secret-key}")
    private String SECRET_KEY;

    @Value("${coze.bot-id}")
    private String BOT_ID;

    private static final String API_PATH = "https://api.coze.cn/open_api/v2/chat";

    public String promptToAvatar(String prompt) {
        HttpEntity<String> request = buildRequest(prompt);
        System.out.println(request);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<NonStreamResp> responseEntity = restTemplate.postForEntity(API_PATH, request, NonStreamResp.class);
        Optional<MessageObject> answer = responseEntity.getBody().getMessages().
                                            stream().filter(messageObject -> messageObject.getType().equals("answer")).
                                            findFirst();
        System.out.println(answer);

        return answer.get().getContent();
    }


    private HttpEntity<String> buildRequest(String prompt) {
        HttpHeaders headers = new HttpHeaders();

        // 请求头
        headers.add("Authorization", "Bearer " + SECRET_KEY);
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
        headers.add("Host", "api.coze.cn");
        headers.add("Connection", "keep-alive");

        // 请求体
        Map<String, Object> body = new HashMap<>();
//        body.put("conversation_id", "whatever");
        body.put("bot_id", BOT_ID);
        body.put("user", "whoever");
        body.put("query", prompt);
        body.put("stream", false);

        return new HttpEntity<>(JSON.toJSONString(body), headers);
    }
}
