package com.example.tdy.entity.audit;


import com.example.tdy.entity.Video;
import com.example.tdy.enums.AuditStatus;
import com.example.tdy.enums.ContentType;
import com.example.tdy.utils.QiniuUtil;

/**
 * @author Mazai-Liu
 * @time 2024/5/7
 */
public class TextAudit extends AbstractAudit {

    public TextAudit(ContentType businessName, QiniuUtil qiniuUtil) {
        super(businessName, qiniuUtil);
    }

    @Override
    public AuditResult doAudit(Video video) {
        return new AuditResult("成功", AuditStatus.PASS);
    }
}
