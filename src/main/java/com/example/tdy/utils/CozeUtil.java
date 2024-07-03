package com.example.tdy.utils;

import com.alibaba.fastjson.JSON;
import com.example.tdy.entity.Comment;
import com.example.tdy.entity.Video;
import com.example.tdy.entity.resp.coze.MessageObject;
import com.example.tdy.entity.resp.coze.NonStreamResp;
import com.example.tdy.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    Logger logger = LoggerFactory.getLogger(CozeUtil.class);

    @Value("${coze.secret-key}")
    private String SECRET_KEY;

    @Value("${coze.avatar-bot-id}")
    private String AVATAR_BOT_ID;

    @Value("${coze.reply-bot-id}")
    private String REPLY_BOT_ID;

    @Autowired
    private VideoService videoService;

    public static final RestTemplate restTemplate = new RestTemplate();

    private static final String API_PATH = "https://api.coze.cn/open_api/v2/chat";

    public String promptToAvatar(String prompt) {
        HttpEntity<String> request = buildRequest(prompt, AVATAR_BOT_ID, null);
        logger.info("promptToAvatar request: {}", request);

        MessageObject answer = getMessageObject(API_PATH, request, NonStreamResp.class);
        logger.info("answer: {}", answer);

        return answer.getContent();
    }

    public String getBotReply(Comment comment) {
        Video video = videoService.getVideoById(comment.getVideoId());
        String videoTitle = video.getTitle();
        String videoDescription = video.getDescription();
        String videoLabel = video.buildLabel().toString();

        Map<String, String> vars = new HashMap<>();
        vars.put("video_title", videoTitle);
        vars.put("video_description", videoDescription);
        vars.put("video_label", videoLabel);
        vars.put("comment", comment.getContent());


        HttpEntity<String> request = buildRequest(comment.getContent(), REPLY_BOT_ID, vars);
        logger.info("getBotReply request: {}", request);

        MessageObject answer = getMessageObject(API_PATH, request, NonStreamResp.class);
        logger.info("answer: {}", answer);
        return answer.getContent();
    }

    private MessageObject getMessageObject(String path, HttpEntity<String> request, Class<NonStreamResp> respType) {
        ResponseEntity<NonStreamResp> responseEntity = restTemplate.postForEntity(path, request, respType);
        Optional<MessageObject> answer = responseEntity.getBody().getMessages().
                stream().filter(messageObject -> messageObject.getType().equals("answer")).
                findFirst();


        return answer.get();
    }

    private HttpEntity<String> buildRequest(String prompt, String botId, Map<String, String> var) {
        HttpHeaders headers = buildHeaders();

        // 请求体
        Map<String, Object> body = new HashMap<>();
//        body.put("conversation_id", "whatever");
        body.put("bot_id", botId);
        body.put("user", "whoever");
        body.put("query", prompt);
        body.put("stream", false);
        if(var != null) {
            body.put("custom_variables", var);
        }

        return new HttpEntity<>(JSON.toJSONString(body), headers);
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();

        // 请求头
        headers.add("Authorization", "Bearer " + SECRET_KEY);
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
        headers.add("Host", "api.coze.cn");
        headers.add("Connection", "keep-alive");

        return headers;
    }
}
