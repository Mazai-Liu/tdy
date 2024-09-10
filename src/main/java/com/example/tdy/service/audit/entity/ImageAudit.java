package com.example.tdy.service.audit.entity;

import com.alibaba.fastjson.JSON;
import com.example.tdy.entity.Video;
import com.example.tdy.entity.resp.audit.ResultJson;
import com.example.tdy.enums.AuditStatus;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Mazai-Liu
 * @time 2024/5/7
 */
@Component
public class ImageAudit extends AbstractAudit {

    public static Logger logger = LoggerFactory.getLogger(ImageAudit.class);
    public static final String AUDIT_API_URL = "http://ai.qiniuapi.com/v3/image/censor";

    public static final String body = "{\n" +
            "    \"data\": {\n" +
            "        \"uri\": \"${url}\",\n" +
            "    },\n" +
            "    \"params\": {\n" +
            "        \"scenes\": [\n" +
            "            \"pulp\",\n" +
            "            \"terror\",\n" +
            "            \"politician\"\n" +
            "        ]\n" +
            "    }\n" +
            "}";
    @Override
    public AuditResult doAudit(Video video) throws QiniuException {
//        AuditResult auditResult = process(fileService.getRealUrl(Integer.valueOf(video.getCover())));
        AuditResult auditResult = new AuditResult("成功", AuditStatus.PASS);

        logger.info("auditResult: {}", auditResult);
        return auditResult;
    }

    private AuditResult process(String realUrl) throws QiniuException {
        logger.info("进入图片审核process，视频url：{}", realUrl);
        Client client = new Client();

        String result = imageCensor(client, realUrl);
        logger.info("result:{}", result);

        ResultJson resultJson = JSON.parseObject(result, ResultJson.class);
        if(!resultJson.getCode().equals(200) && !resultJson.getResult().getSuggestion().equals("pass")) {
            return new AuditResult("失败", AuditStatus.FAIL);
        }
        return new AuditResult("成功", AuditStatus.PASS);
    }

    private String imageCensor(Client client, String realUrl) throws QiniuException {
        String requestBody = body.replace("${url}", realUrl);
        byte[] bodyByte = requestBody.getBytes();

        return post(client, AUDIT_API_URL, bodyByte);
    }
}
