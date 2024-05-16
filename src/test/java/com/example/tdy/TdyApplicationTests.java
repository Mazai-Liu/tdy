package com.example.tdy;

import com.example.tdy.constant.SystemConstant;
import com.example.tdy.entity.User;
import com.example.tdy.entity.audit.VideoAudit;
import com.example.tdy.entity.resp.audit.BodyJson;
import com.example.tdy.mapper.UserMapper;
import com.example.tdy.utils.QiniuUtil;
import com.google.gson.Gson;
import com.qiniu.http.Client;
import com.qiniu.storage.model.FileInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TdyApplicationTests {

    @Autowired
    QiniuUtil qiniuUtil;

    @Test
    void contextLoads() {
        VideoAudit videoAudit = new VideoAudit();
        videoAudit.qiniuUtil = qiniuUtil;
        Client client = new Client();

        String result = videoAudit.getVideoCensorResultByJobID(client, "664598e54802da5d32e37df4");

        System.out.println(result);
        Gson gson = new Gson();
        final BodyJson bodyJson = gson.fromJson(result, BodyJson.class);

        System.out.println("bodyJson:" + bodyJson);

    }
}
