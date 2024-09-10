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
public class TextAudit extends AbstractAudit {

    public static Logger logger = LoggerFactory.getLogger(TextAudit.class);

    public static final String AUDIT_API_URL = "http://ai.qiniuapi.com/v3/text/censor";

    public static final String body = "{\n" +
            "    \"data\": {\n" +
            "        \"text\": \"${text}\",\n" +
            "    },\n" +
            "    \"params\": {\n" +
            "        \"scenes\": [\n" +
            "            \"antispam\",\n" +
            "        ]\n" +
            "    }\n" +
            "}";

    @Override
    public AuditResult doAudit(Video video) throws QiniuException {
//        AuditResult auditResult = process(video.getTitle() + "——" + video.getDescription());
        AuditResult auditResult = new AuditResult("成功", AuditStatus.PASS);

        logger.info("auditResult: {}", auditResult);
        return auditResult;
    }

    private AuditResult process(String realText) throws QiniuException {
        logger.info("进入文本审核process，视频url：{}", realText);
        Client client = new Client();

        String result = textCensor(client, realText);
        logger.info("result:{}", result);

        ResultJson resultJson = JSON.parseObject(result, ResultJson.class);
        if(!resultJson.getCode().equals(200) && !resultJson.getResult().getSuggestion().equals("pass")) {
            return new AuditResult("失败", AuditStatus.FAIL);
        }
        return new AuditResult("成功", AuditStatus.PASS);
    }

    private String textCensor(Client client, String realText) throws QiniuException {
        String requestBody = body.replace("${text}", realText);
        byte[] bodyByte = requestBody.getBytes();

        return post(client, AUDIT_API_URL, bodyByte);
    }

}
