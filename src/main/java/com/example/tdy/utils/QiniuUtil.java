package com.example.tdy.utils;

import com.example.tdy.entity.Video;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Mazai-Liu
 * @time 2024/3/27
 */

@Component
public class QiniuUtil {

    @Value("${qiniu.bucket-name}")
    private String BUCKET_NAME;
    @Value("${qiniu.access-key}")
    private String ACCESS_KEY;
    @Value("${qiniu.secret-key}")
    private String SECRET_KEY;
    public static final String CNAME = "scys30o00.hb-bkt.clouddn.com";

    public static final String PROTOCOL = "http";

    private static final Configuration CFG;

    static {
        CFG = new Configuration(Region.region0());
    }


    public String getFileToken() {
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        return auth.uploadToken(BUCKET_NAME);
    }

    public Auth getAuth() {
        return Auth.create(ACCESS_KEY, SECRET_KEY);
    }


    public FileInfo getFileInfo(String fileKey) {
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        BucketManager bucketManager = new BucketManager(auth, CFG);

        FileInfo fileInfo = null;
        try {
            fileInfo = bucketManager.stat(BUCKET_NAME, fileKey);
        } catch (QiniuException e) {
            throw new RuntimeException(e);
        }

        return fileInfo;
    }

    public String getQiNiuToken(String url, String method, String body, String contentType) {
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        return "Qiniu " + auth.signQiniuAuthorization(url, method, body == null ? null : body.getBytes(), contentType);
    }

}
