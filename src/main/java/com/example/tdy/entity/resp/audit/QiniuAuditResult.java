package com.example.tdy.entity.resp.audit;

import lombok.Data;

/**
 * @author Mazai-Liu
 * @time 2024/5/9
 */
@Data
public class QiniuAuditResult {
    private String suggestion;
    private ScenesJson scenes;
}
