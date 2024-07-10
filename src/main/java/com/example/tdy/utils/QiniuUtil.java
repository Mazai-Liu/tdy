package com.example.tdy.utils;

import com.example.tdy.entity.Video;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
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
    public static final String CNAME = "cdn.badcoder.icu";
//    public static final String CNAME = "sevc2x3hi.sabkt.gdipper.com";

    public static final String PROTOCOL = "http";

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;

    private static final long UP_TOKEN_EXPIRE = 3 * 60;
    private static final String ALLOW_MIME_TYPE = "image/*;video/*";

    private static final Configuration CFG;

    static {
        CFG = new Configuration(Region.region0());
    }


    public String getFileToken() {
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);

        StringMap policy = new StringMap();
        policy.put("fsizeLimit", MAX_FILE_SIZE);
        policy.put("mimeLimit", ALLOW_MIME_TYPE);

        return auth.uploadToken(BUCKET_NAME, null, UP_TOKEN_EXPIRE, policy);
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
