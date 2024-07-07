package com.example.tdy.service.audit.entity;


import com.example.tdy.entity.Video;
import com.example.tdy.entity.resp.audit.BodyJson;
import com.example.tdy.entity.resp.audit.ScenesJson;
import com.example.tdy.entity.resp.audit.ScoreJson;
import com.example.tdy.enums.AuditStatus;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Client;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mazai-Liu
 * @time 2024/5/7
 */

@Component
public class VideoAudit extends AbstractAudit {
    public static Logger logger = LoggerFactory.getLogger(VideoAudit.class);

    public static final String AUDIT_API_URL = "http://ai.qiniuapi.com/v3/video/censor";
    public static final String RESULT_API_URL = "http://ai.qiniuapi.com/v3/jobs/video/";

    public static final String body = "{\n" +
            "    \"data\": {\n" +
            "        \"uri\": \"${url}\",\n" +
            "        \"id\": \"video_censor_test\"\n" +
            "    },\n" +
            "    \"params\": {\n" +
            "        \"scenes\": [\n" +
            "            \"pulp\",\n" +
            "            \"terror\",\n" +
            "            \"politician\"\n" +
            "        ],\n" +
            "        \"cut_param\": {\n" +
            "            \"interval_msecs\": 5000\n" +
            "        }\n" +
            "    }\n" +
            "}";

    public VideoAudit() {
        super();
    }


    @Override
    public AuditResult doAudit(Video video) throws QiniuException {
        logger.info("videoUrl: {}", video.getUrl());

//        AuditResult auditResult = process(fileService.getRealUrl(Integer.valueOf(video.getUrl())));
        AuditResult auditResult = new AuditResult("成功", AuditStatus.PASS);

        logger.info("auditResult: {}", auditResult);
        return auditResult;
    }

    private AuditResult process(String videoUrl) throws QiniuException {
        logger.info("进入视频审核process，视频url：{}", videoUrl);
        Client client = new Client();

        String result = VideoCensor(client, videoUrl);
        logger.info("result:{}", result);

        // 获取job_id
        Gson gson = new Gson();
        Map<String, String> jobMap = new HashMap();
        String jobID = (String) gson.fromJson(result, jobMap.getClass()).get("job");
        try {
            while (true) {
                String videoCensorResult = getVideoCensorResultByJobID(client, jobID);
                // System.out.println(videoCensorResult);

                final BodyJson bodyJson = gson.fromJson(videoCensorResult, BodyJson.class);
                logger.info("bodyJson:{}", bodyJson);

                if (bodyJson.getStatus().equals("FINISHED")) {
                    logger.info("获取到七牛云审核结果");
                    // 1.从系统配置表获取 pulp politician terror比例
//                    final Setting setting = settingService.getById(1);
//                    final SettingScoreJson settingScoreRule = objectMapper.readValue(setting.getAuditPolicy(), SettingScoreJson.class);
//                    final List<ScoreJson> auditRule = Arrays.asList(settingScoreRule.getManualScore(), settingScoreRule.getPassScore(), settingScoreRule.getSuccessScore());
                    final List<ScoreJson> auditRule = null;
                    return check(auditRule, bodyJson);
                }
                Thread.sleep(2000L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     *  suggestion=block：表示系统确认审核内容违规，建议您将其删除。
        suggestion=review：表示系统无法确认审核内容是否违规，建议您进行人工复核。
        suggestion=pass：表示系统确认审核内容正常，建议您忽略该文件。
     */
    public AuditResult check(List<ScoreJson> auditRule, BodyJson bodyJson) {
        logger.info("进行score和suggestion check");
        // TODO 暂且只根据七牛云的审核结果来判断
        ScenesJson scenes = bodyJson.getScenes();
        if(!scenes.getPulp().getSuggestion().equals("pass") ||
                !scenes.getPolitician().getSuggestion().equals("pass") ||
                !scenes.getTerror().getSuggestion().equals("pass")) {
            return new AuditResult("失败", AuditStatus.FAIL);
        }


        return new AuditResult("成功", AuditStatus.PASS);
    }

    public String VideoCensor(Client client, String videoUrl) throws QiniuException {

//        // 构造post请求body
//        Gson gson = new Gson();
//
//        Map bodyData = new HashMap();
//
//        Map<String, Object> uri = new HashMap();
//
//        uri.put("uri", videoUrl);
//
//        Map<String, Object> params = new HashMap();
//
////        Map<String, Object> scenes = new HashMap();
//
//        //pulp 黄  terror 恐  politician 敏感人物
//        String[] types = {"pulp", "terror", "politician"};
//
//        Map<String, Object> cut_param = new HashMap();
//        cut_param.put("interval_msecs", 5000);
//        params.put("scenes", types);
//        params.put("cut_param", cut_param);
//        bodyData.put("data", uri);
//        bodyData.put("params", params);
//
//        String paraR = gson.toJson(bodyData);

        String requestBody = body.replace("${url}", videoUrl);
        byte[] bodyByte = requestBody.getBytes();

        return post(client, AUDIT_API_URL, bodyByte);

    }

    /**
     * 查询视频审核内容结果
     * 参考
     * https://developer.qiniu.com/censor/api/5620/video-censor#4
     * @param ID : 视频审核返回的 job ID
     *
     */
    public String getVideoCensorResultByJobID(Client client, String ID){

        String url = RESULT_API_URL.concat(ID);
        Auth auth = qiniuUtil.getAuth();

        try {
            com.qiniu.http.Response resp = client.get(url,auth.authorizationV2(url));
            return resp.bodyString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }


}

//class ResourcesCensor {
//
//    //设置好账号的ACCESS_KEY和SECRET_KEY
//
//    private static final String ACCESS_KEY = "lBDalXr5WHT8jKNqL3urCjQlDUmLsI7cVsxM4tYG";
//
//    private static final String SECRET_KEY = "ZnC8jWlImLEBIiyG-_AiJSkIPfLiknuUnijLDDOb";
//
//    private final Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
//
//    private final Client client = new Client();
//
//
//    //参考api文档 https://developer.qiniu.com/dora/manual/4252/image-review
//
//    //图片审核
//
//    public String ImageCensor() throws QiniuException {
//
//        // 构造post请求body
//
//        Gson gson = new Gson();
//
//        Map<String, Object> uri = new HashMap();
//
//        uri.put("uri", "http://oayjpradp.bkt.clouddn.com/Audrey_Hepburn.jpg");
//
//        Map<String, Object> scenes = new HashMap();
//
//        //pulp 黄  terror 恐  politician 敏感人物
//
//        String[] types = {"pulp", "terror", "politician", "ads"};
//
//        scenes.put("scenes", types);
//
//        Map params = new HashMap();
//
//        params.put("data", uri);
//
//        params.put("params", scenes);
//
//        String paraR = gson.toJson(params);
//
//        byte[] bodyByte = paraR.getBytes();
//
//        // 接口请求地址
//
//        String url = "http://ai.qiniuapi.com/v3/image/censor";
//
//        return post(url, bodyByte);
//    }
//}

