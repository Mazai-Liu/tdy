package com.example.tdy.entity.audit;


import com.example.tdy.entity.Video;
import com.example.tdy.enums.AuditStatus;
import com.example.tdy.enums.ContentType;
import com.example.tdy.utils.QiniuUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author Mazai-Liu
 * @time 2024/5/7
 */
@Component
public class ImageAudit extends AbstractAudit {
    @Override
    public AuditResult doAudit(Video video) {
        return new AuditResult("成功", AuditStatus.PASS);
    }
}
