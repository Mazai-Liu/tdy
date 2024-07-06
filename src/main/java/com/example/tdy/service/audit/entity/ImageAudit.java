package com.example.tdy.service.audit.entity;


import com.example.tdy.entity.Video;
import com.example.tdy.enums.AuditStatus;
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
